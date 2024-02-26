package com.finogeeks.finclip.business.common


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zbar.ZBarView
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.UriUtils
import com.finogeeks.finclip.R
import com.finogeeks.finclip.base.BaseActivity
import com.finogeeks.finclip.utils.extensions.onClick
import com.permissionx.guolindev.PermissionX

class ScanQRCodeActivity : BaseActivity(), QRCodeView.Delegate {
    companion object {

        const val EXTRA_RESULT = "ScanQRresult"

    }


    override fun getLayoutId(): Int = R.layout.activity_scan_qrcode
    private lateinit var zBarView: ZBarView
    private lateinit var iv_light: View
    private lateinit var iv_album: View
    private lateinit var iv_close: View
    private var isOpenFlashlight = false

    private val mLauncherAlbum = registerForActivityResult(
        ActivityResultContracts.GetContent()) { result ->
        val path = result?.path
        if (path.isNullOrEmpty()) {
            ToastUtils.showLong(getString(R.string.fpt_choose_img_error))
        } else {
            zBarView.decodeQRCode(UriUtils.uri2File(result).absolutePath)
        }
    }

    override fun configView(savedInstanceState: Bundle?) {
        zBarView = findViewById(R.id.zBarView)
        iv_light = findViewById(R.id.iv_light)
        iv_album = findViewById(R.id.iv_album)
        iv_close = findViewById(R.id.iv_close)
    }

    override fun configEvent() {
        super.configEvent()
        iv_close.onClick {
            finish()
        }
        iv_light.onClick {
            if (isOpenFlashlight) {
                zBarView.openFlashlight()
            } else {
                zBarView.closeFlashlight()
            }
            isOpenFlashlight = !isOpenFlashlight
        }

        iv_album.onClick {
            PermissionX.init(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        val type = "image/*"
                        zBarView.stopSpot()
                        mLauncherAlbum.launch(type)
                    } else {
                        ToastUtils.showLong(getString(R.string.fpt_per_albums_denied))
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList,
                    getString(R.string.fpt_per_camera_denied_tips),
                    getString(R.string.fpt_confirm),
                    getString(R.string.fpt_cancel))
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    zBarView.isVisible = true
                    zBarView.setDelegate(this)
                    zBarView.startCamera()
                    zBarView.startSpotAndShowRect()
                } else {
                    ToastUtils.showLong(getString(R.string.fpt_per_camera_denied))
                }
            }
    }

    override fun onStop() {
        zBarView.stopCamera()
        super.onStop()
    }

    override fun onDestroy() {
        zBarView.onDestroy()
        zBarView.closeFlashlight()
        super.onDestroy()
    }


    override fun onScanQRCodeSuccess(result: String?) {
        zBarView.startSpotAndShowRect()
        if(result.isNullOrEmpty()){
            ToastUtils.showLong(R.string.fpt_qr_validation_failed)
        }else{
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_RESULT, result))
            finish()
        }

    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

    }

    override fun onScanQRCodeOpenCameraError() {
        ToastUtils.showLong(getString(R.string.fpt_open_camera_err))
    }


}