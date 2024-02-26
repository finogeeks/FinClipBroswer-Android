package com.finogeeks.finclip.business.home


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.blankj.utilcode.util.*
import com.drakeet.multitype.MultiTypeAdapter
import com.finogeeks.finclip.BuildConfig
import com.finogeeks.finclip.R
import com.finogeeks.finclip.base.BaseActivity
import com.finogeeks.finclip.business.common.LocalData
import com.finogeeks.finclip.business.common.ScanQRCodeActivity
import com.finogeeks.finclip.business.common.WebViewActivity
import com.finogeeks.finclip.business.common.model.URLBean
import com.finogeeks.finclip.business.home.model.HomeAppletListBinder
import com.finogeeks.finclip.business.home.model.UIAppletBean
import com.finogeeks.finclip.business.login.LoginHelper
import com.finogeeks.finclip.http.NetWorkManager
import com.finogeeks.finclip.http.SingleObserverImpl

import com.finogeeks.finclip.utils.ActivitySkip
import com.finogeeks.finclip.utils.extensions.*
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.modules.callback.FinSimpleCallback
import com.finogeeks.lib.applet.sdk.api.request.IFinAppletRequest
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.FullScreenPopupView


class MainActivity : BaseActivity() {
    override fun isStatusBarLightMode() = false
    override fun configStatusBarColor() =
        if (LocalData.isOPType()) R.color.green_27B7A2 else R.color.blue_125aff

    override fun getLayoutId(): Int = R.layout.activity_main

    private lateinit var btn_scan_qr: View
    private lateinit var tv_version: TextView
    private lateinit var tv_use_tips: TextView
    private lateinit var tv_logout: View
    private lateinit var rl_applets: RecyclerView
    private lateinit var ll_empty: View
    private lateinit var v_top_bg: View
    private lateinit var cl_ee_applets: View
    private lateinit var cl_op_qr: View
    private lateinit var ll_op_scan_qr: View
    private lateinit var iv_bottom_op: ImageView
    private val mdAdapter = MultiTypeAdapter()
    private val uiApplets = mutableListOf<Any>()

    private val requestQRDataLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val qr = result.data?.getStringExtra(ScanQRCodeActivity.EXTRA_RESULT)
                if (qr.isNullOrEmpty()) {
                    return@registerForActivityResult
                }
                FinAppClient.appletApiManager.startApplet(
                    this,
                    IFinAppletRequest.fromQrCode(qr), null
                )
            }
        }


    override fun configView(savedInstanceState: Bundle?) {

        ActivityUtils.finishOtherActivities(MainActivity::class.java)

        btn_scan_qr = findViewById(R.id.btn_scan_qr)
        tv_version = findViewById(R.id.tv_version)
        tv_use_tips = findViewById(R.id.tv_use_tips)
        tv_logout = findViewById(R.id.tv_logout)
        rl_applets = findViewById(R.id.rl_applets)
        ll_empty = findViewById(R.id.ll_empty)
        v_top_bg = findViewById(R.id.v_top_bg)
        cl_ee_applets = findViewById(R.id.cl_ee_applets)
        cl_op_qr = findViewById(R.id.cl_op_qr)
        iv_bottom_op = findViewById(R.id.iv_bottom_op)
        ll_op_scan_qr = findViewById(R.id.ll_op_scan_qr)

    }


    override fun configData() {
        super.configData()

        if (LocalData.isOPType()) {
            v_top_bg.background = ResourceUtils.getDrawable(R.drawable.green_solid_ee_main_top)
            btn_scan_qr.background = ResourceUtils.getDrawable(R.drawable.green_solid_v2)
            cl_ee_applets.isInvisible = true
            cl_op_qr.isInvisible = false
            btn_scan_qr.isVisible = false
            iv_bottom_op.load(R.drawable.home_cardbg) {
                transformations(RoundedCornersTransformation(ConvertUtils.dp2px(8F).toFloat()))
            }
        } else {
            btn_scan_qr.isVisible = true
            cl_ee_applets.isInvisible = false
            cl_op_qr.isInvisible = true

            getApplets()
        }

        tv_version.text =
            "${StringUtils.getString(R.string.fpt_app_version)} : ${BuildConfig.VERSION_NAME} " +
                    "| ${StringUtils.getString(R.string.fpt_sdk_version)} : ${com.finogeeks.lib.applet.BuildConfig.VERSION_NAME}"


    }

    private fun getApplets() {
        if (LocalData.isPRType()) {
            NetWorkManager.getApiService().getPriApplets()
        } else {
            NetWorkManager.getApiService().getApplets()
        }.convertResp().map {
            val hot = it.hot.map { item ->
                UIAppletBean(
                    iconURL = if (URLUtil.isNetworkUrl(item.logo)) item.logo
                        ?: "" else LocalData.getServerBaseUrl() + item.logo,
                    appName = item.name ?: "",
                    info = item
                )
            }
            return@map hot
        }.io2Main().autoDispose(this)
            .subscribe(object : SingleObserverImpl<List<UIAppletBean>>() {
                override fun onSuccess(data: List<UIAppletBean>) {
                    super.onSuccess(data)
                    if (data.isEmpty()) {
                        showEmptyView(true)
                    } else {
                        showEmptyView(false)
                        uiApplets.clear()
                        uiApplets.addAll(data)
                        mdAdapter.notifyDataSetChanged()
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    showEmptyView(true)
                }
            })
    }


    fun showEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            rl_applets.isVisible = false
            ll_empty.isVisible = true
        } else {
            rl_applets.isVisible = true
            ll_empty.isVisible = false
        }
    }


    override fun configEvent() {
        super.configEvent()

        tv_use_tips.onClick {

            ActivitySkip.startActivityWithData(this@MainActivity,
                WebViewActivity::class.java,
                URLBean().apply {
                    url =
                        "https://www.finclip.com/mop/document/introduce/functionDescription/finclip.html"
                })

        }

        btn_scan_qr.onClick {
            val intent = Intent(this, ScanQRCodeActivity::class.java)
            requestQRDataLauncher.launch(intent)
        }

        ll_op_scan_qr.onClick {
            btn_scan_qr.performClick()
        }


        tv_logout.onClick {
            showLogout()
        }


        rl_applets.layoutManager = GridLayoutManager(this, 4)
        mdAdapter.register(UIAppletBean::class.java, HomeAppletListBinder {

            if (it.info.appId.isNullOrEmpty()) {
                ToastUtils.showLong(getString(R.string.fpt_appid_empty))
                return@HomeAppletListBinder
            }

            FinAppClient.appletApiManager.startApplet(
                this,
                IFinAppletRequest.fromAppId(it.info.appId!!),
                null
            )
        })
        mdAdapter.items = uiApplets
        rl_applets.adapter = mdAdapter
    }


    private fun showLogout() {
        XPopup.Builder(this)
            .asCustom(object : FullScreenPopupView(this) {
                override fun getImplLayoutId(): Int {
                    return R.layout.pop_logout_layout
                }

                override fun onCreate() {
                    super.onCreate()
                    contentView.findViewById<View>(R.id.tv_cancel).onClick {
                        dismiss()
                    }
                    contentView.findViewById<View>(R.id.tv_ok).onClick {
                        dismiss()
                        LoginHelper.logout(this@MainActivity)
                        LoginHelper.toLoginPage(
                            this@MainActivity,
                            LocalData.isPRType()
                        )
                    }
                }
            }).show()
    }

}