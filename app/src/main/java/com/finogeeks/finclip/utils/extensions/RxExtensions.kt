package com.finogeeks.finclip.utils.extensions



import androidx.lifecycle.LifecycleOwner
import com.finogeeks.finclip.http.FLConvertDataNullResponse
import com.finogeeks.finclip.http.FLConvertResponse
import com.finogeeks.finclip.http.model.FLResponse
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.ObservableSubscribeProxy
import com.uber.autodispose.SingleSubscribeProxy
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers



private fun <T> applySchedulersIO2Main(): SingleTransformer<T, T> {
    return SingleTransformer<T, T> { upstream: Single<T> ->
        upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}


fun <T> applySchedulersIO2MainOb(): ObservableTransformer<T, T> {
    return ObservableTransformer<T, T> { upstream: Observable<T> ->
        upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}


fun <T> applySchedulersWork(): SingleTransformer<T, T> {
    return SingleTransformer<T, T> { upstream: Single<T> ->
        upstream.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }
}

fun <T> Single<T>.autoDispose(owner: LifecycleOwner): SingleSubscribeProxy<T> {
    return this.`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner)))
}

fun <T> Observable<T>.autoDispose(owner: LifecycleOwner): ObservableSubscribeProxy<T> {
    return this.`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner)))
}

fun <T> Single<FLResponse<T>>.convertResp(): Single<T> {
    return this.map(FLConvertResponse())
}

fun <T> Single<FLResponse<T>>.convertDataNullResp(): Single<FLResponse<T>> {
    return this.map(FLConvertDataNullResponse())
}


fun <T> Single<T>.io2Main(): Single<T> {
    return this.compose(applySchedulersIO2Main())
}