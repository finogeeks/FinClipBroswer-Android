package com.finogeeks.finclip.http

import com.finogeeks.finclip.http.model.FLResponse
import io.reactivex.functions.Function

class FLConvertDataNullResponse<T> : Function<FLResponse<T>, FLResponse<T>> {
    override fun apply(t: FLResponse<T>): FLResponse<T> {
        if (t.isSuccess()) {
            return t
        }
        throw FLHttpException(errCode = t.errcode, error = t.error)
    }
}