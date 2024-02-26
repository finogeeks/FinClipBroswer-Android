package com.finogeeks.finclip.http

import com.finogeeks.finclip.http.model.FLResponse
import io.reactivex.functions.Function

class FLConvertResponse<T> : Function<FLResponse<T>, T> {
    override fun apply(t: FLResponse<T>): T {
        if (t.isSuccess() && t.data != null  ) {
            return t.data!!
        }
        throw FLHttpException(errCode = t.errcode, error = t.error)
    }
}