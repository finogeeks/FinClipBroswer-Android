package com.finogeeks.finclip.http.model.response

import androidx.annotation.Keep

@Keep
class LoginResp {
    var account: String? = null
    var name: String? = null
    var role: String? = null
    var phone: String? = null
    var jwtToken: String? = null
    var jwtTokenExpireTime = 0
    var refreshToken: String? = null
    var isAccountDesensitization: Boolean? = null

    // Unique to the enterprise
    var accountTraceId: String? = null
    var organTraceId: String? = null
    var organName: String? = null
    var needOrganApply: Boolean? = null
    // Unique to operation
    var traceId: String? = null
}