package com.finogeeks.finclip.http.model.response

import androidx.annotation.Keep

@Keep
class CaptchaResp {
    var imageBase64: String? = null
    var id: String? = null
}