package com.finogeeks.finclip.http

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Handle exception handling information during login
 */
class ErrorConvertInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        return if(response.code == 400){
            response.newBuilder().code(200).build()
        }else{
            response
        }
    }
}