package com.finogeeks.finclip.http


import com.finogeeks.finclip.http.NetWorkManager.API_PREFIX_V1
import com.finogeeks.finclip.http.NetWorkManager.API_PREFIX_V2
import okhttp3.Interceptor
import okhttp3.Response

class URLV2Interceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.toString()
        return if (url.endsWith(NetWorkManager.LOGIN_V2)) {
            val request =
                original.newBuilder().url(url.replace(API_PREFIX_V1, "")).build()
            chain.proceed(request)
        } else {
            chain.proceed(original)
        }
    }
}