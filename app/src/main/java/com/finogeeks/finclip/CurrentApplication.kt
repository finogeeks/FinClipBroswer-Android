package com.finogeeks.finclip


import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.ToastUtils.MODE.DARK
import com.finogeeks.finclip.base.BaseApplication
import com.finogeeks.finclip.business.common.LocalData

class CurrentApplication : BaseApplication() {


    override fun doInMain() {
        LogUtils.getConfig().isLogSwitch = BuildConfig.DEBUG
        LogUtils.getConfig().globalTag = "FinClip"
        ToastUtils.getDefaultMaker().setMode(DARK)
    }

    override fun doInMainProcess() {
        if (LocalData.isLogin()) {
            FinAppletInitializer.init(this)
        }
    }
}