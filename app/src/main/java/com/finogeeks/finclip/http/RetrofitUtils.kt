package com.finogeeks.finclip.http


import com.finogeeks.finclip.http.NetWorkManager.API_PREFIX_V1
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitUtils {

    fun createRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl + API_PREFIX_V1)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun getRetrofitBaseUrl(retrofit: Retrofit): String {
        return retrofit.baseUrl().toString()
    }
}