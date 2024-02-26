package com.finogeeks.finclip.business.login

import android.app.Activity
import android.content.Context
import com.finogeeks.finclip.business.common.LocalData
import com.finogeeks.finclip.business.common.TokenHelper
import com.finogeeks.finclip.utils.ActivitySkip
import com.finogeeks.lib.applet.client.FinAppClient

object LoginHelper {
    fun toLoginPage(activity: Activity, isPrivate: Boolean) {
        if (isPrivate) {
            ActivitySkip.startActivityAndFinish(activity, PrivateLoginTypeActivity::class.java)
        } else {
            ActivitySkip.startActivityAndFinish(activity, SassLoginTypeActivity::class.java)
        }
    }

    fun logout(context: Context) {
        LocalData.clearLoginInfo()
        FinAppClient.appletApiManager.clearApplets()
        TokenHelper.stopTokenTimer()
    }
}