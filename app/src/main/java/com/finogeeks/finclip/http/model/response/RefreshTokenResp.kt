package com.finogeeks.finclip.http.model.response

import androidx.annotation.Keep

@Keep
class RefreshTokenResp {
    var jwtToken: String? = null
    var userId: String? = null
    var expireTime = 0
    var refreshToken: String? = null
}