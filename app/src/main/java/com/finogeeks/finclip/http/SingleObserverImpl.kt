package com.finogeeks.finclip.http


import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.JsonUtils
import com.blankj.utilcode.util.LogUtils
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

open class SingleObserverImpl<T> : SingleObserver<T> {
    override fun onSubscribe(d: Disposable) {
    }

    override fun onSuccess(data: T) {
        LogUtils.d("onSuccess :" + GsonUtils.toJson(data))
    }

    override fun onError(e: Throwable) {
        LogUtils.d("onError :" + e.localizedMessage)
    }
}