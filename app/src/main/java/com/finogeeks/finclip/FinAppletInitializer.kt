package com.finogeeks.finclip

import android.app.Application
import android.os.Build
import android.util.Log
import com.finogeeks.finclip.business.common.LocalData
import com.finogeeks.finclip.utils.extensions.accountDecrypt
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.client.FinAppConfig
import com.finogeeks.lib.applet.client.FinAppConfig.UIConfig
import com.finogeeks.lib.applet.interfaces.FinCallback
import com.finogeeks.mop.plugins.apis.live.AnyRtcLiverSDK
import com.finogeeks.mop.plugins.maps.map.MapSDKInitializer
import com.finogeeks.xlog.XLogLevel

import java.io.File
import java.util.Locale

/**
 * Applet SDK initializes the class
 */
object FinAppletInitializer {

    private const val TAG = "FinAppletInitializer"
    fun init(application: Application) {
        val uiConfig = UIConfig()
        uiConfig.isHideForwardMenu = true
       uiConfig.isUseNativeLiveComponent = true
        val config = FinAppConfig.Builder()
            .setSdkKey(BuildConfig.SDK_KEY)
            .setSdkSecret(BuildConfig.SDK_SECRET)
            .setApiUrl(LocalData.getServerBaseUrl())
            .setApiPrefix(BuildConfig.API_PREFIX)
            .setEncryptionType(FinAppConfig.ENCRYPTION_TYPE_SM)
            .setLogLevel(XLogLevel.LEVEL_VERBOSE)
            .setXLogDir(File(application.getExternalFilesDir(null), "fino_xLog"))
            .setAppletDebugMode(if (BuildConfig.DEBUG) FinAppConfig.AppletDebugMode.appletDebugModeEnable
            else FinAppConfig.AppletDebugMode.appletDebugModeDisable)
            .setDebugMode(BuildConfig.DEBUG)
            .setUiConfig(uiConfig)
            .setAppletIntervalUpdateLimit(3)
            .setMaxRunningApplet(5)
            .setPageCountLimit(5)
            .setEnablePreNewProcess(true)
            .setBindAppletWithMainProcess(false)
            .setEnableApmDataCompression(true)
            .setDisableGetSuperviseInfo(true)
            .setLocale(Locale.getDefault())
            .setCustomWebViewUserAgent("android 7.1.1")
            .setMinAndroidSdkVersion(Build.VERSION_CODES.KITKAT)
            .apply {
                val account = LocalData.getLoginInfo()?.phone?.accountDecrypt()
                if (!account.isNullOrEmpty()) {
                    setUserId(account)
                }
            }
            .setUseLocalTbsCore(true)
            .setTbsCoreUrl("https://www-cdn.finclip.com/sdk/x5/latest/")
            .build()

        FinAppClient.init(application, config, object : FinCallback<Any?> {
            override fun onSuccess(result: Any?) {
                Log.d(TAG, "init result : $result")
                AnyRtcLiverSDK.install()
                MapSDKInitializer.setAMapPrivacy(application, true, true, true)
                MapSDKInitializer.setAMapLocationPrivacy(application, true, true, true)
                MapSDKInitializer.setBaiduMapAgreePrivacy(application, true)
                MapSDKInitializer.setBaiduLocationAgreePrivacy(application, true)
                MapSDKInitializer.setTencentMapAgreePrivacy(application, true)
                MapSDKInitializer.setTencentLocationAgreePrivacy(application, true)
                MapSDKInitializer.setTencentSecretKey(application, null)
            }

            override fun onError(code: Int, error: String) {
                Log.d(TAG, "init onError : error=$error, code=$code")
            }

            override fun onProgress(status: Int, error: String) {

            }
        })
    }
}