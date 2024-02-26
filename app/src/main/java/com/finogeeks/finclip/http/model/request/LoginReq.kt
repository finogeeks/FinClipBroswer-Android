package com.finogeeks.finclip.http.model.request

import androidx.annotation.Keep

@Keep
class LoginReq {
    var account: String? = null
    var password: String? = null
    var isWeb: Boolean = false
    var captchaId: String? = null
    var captchaStr: String? = null
}