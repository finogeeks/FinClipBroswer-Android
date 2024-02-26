package com.finogeeks.finclip.http


import com.finogeeks.finclip.http.model.FLResponse
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

open class FLSingleObserverImpl<T> : SingleObserver<FLResponse<T>> {
    override fun onSubscribe(d: Disposable) {
    }
    override fun onError(e: Throwable) {
    }

    override fun onSuccess(t: FLResponse<T>) {

    }
}