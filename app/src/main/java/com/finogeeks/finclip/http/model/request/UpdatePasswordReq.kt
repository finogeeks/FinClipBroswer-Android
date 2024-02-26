package com.finogeeks.finclip.http.model.request

import androidx.annotation.Keep

@Keep
 class UpdatePasswordReq {
    var phone: String? = null
    var newPassword: String? = null
    var verifyCode: String? = null
}