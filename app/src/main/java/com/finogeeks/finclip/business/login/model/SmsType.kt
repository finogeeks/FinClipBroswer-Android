package com.finogeeks.finclip.business.login.model

import androidx.annotation.Keep

@Keep
class SmsType(val type: String = TypeLogin ,var phone:String? = null,var areaCode:String? = null ,var verifyCode:String? = null,var captchaId:String? = null,var captchaStr:String? = null) {
    companion object {
        const val TypeLogin = "TypeLogin"
        const val TypeForget = "TypeForget"

    }
}