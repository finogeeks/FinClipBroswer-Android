package com.finogeeks.finclip.http

import com.finogeeks.finclip.BuildConfig
import com.finogeeks.finclip.business.common.LocalData
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val loginInfo = LocalData.getLoginInfo()
        val original = chain.request()
        val request = original.newBuilder().apply {
            val jwt = loginInfo?.jwtToken
            if (!jwt.isNullOrEmpty()) {
                header("Authorization", "Bearer $jwt")
            }
            header("SDK-Key", BuildConfig.SDK_KEY)
            header("mop-sdk-key", BuildConfig.SDK_KEY)
        }.build()
        return chain.proceed(request)

    }
}