package com.finogeeks.finclip.http.model

import androidx.annotation.Keep

@Keep
class FLResponse<T> {
    var errcode: String? = null
    var error: String? = null
    var data: T? = null
    fun isSuccess() = errcode == "OK"
}
