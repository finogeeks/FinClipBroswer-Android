package com.finogeeks.finclip.http


import android.net.sip.SipErrorCode.TIME_OUT
import com.blankj.utilcode.util.GsonUtils
import com.finogeeks.finclip.BuildConfig
import com.finogeeks.finclip.business.common.LocalData
import com.finogeeks.finclip.http.api.ApiService
import com.finogeeks.finclip.http.model.FLResponse
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.IOException
import java.util.concurrent.TimeUnit

object NetWorkManager {

    const val API_PREFIX_V1 = "/api/v1/mop/"
    const val API_PREFIX_V2 = "/api/v2/mop/"
    const val TEST_URL = "applets-ecol-account/operation/ftHelper/testUrl"

    // Enterprise account login V2, relatively special, a separate
    const val LOGIN_V2 = API_PREFIX_V2 + "applets-ecol-account/organ/login"
    const val FIN_CLIP_EE_UAT_BASE_URL = BuildConfig.API_URL
    const val FIN_CLIP_EE_UAT_BASE_URL_B = "https://finchat-mop-b.finogeeks.club"
    const val FIN_CLIP_EE_UAT_BASE_URL_TEST = "https://finclip-testing.finogeeks.club"
    private const val TIME_OUT = 20L
    private lateinit var mOkHttpClient: OkHttpClient

    @Volatile
    private lateinit var mRetrofit: Retrofit

    @Volatile
    private lateinit var mApiService: ApiService

    init {
        initOkHttpClient()
        val baseUrl = LocalData.getServerBaseUrl()
        if (baseUrl.isNotEmpty()) {
            createRetrofit(baseUrl)
        }
    }

    private fun initOkHttpClient() {
        mOkHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .pingInterval(TIME_OUT, TimeUnit.SECONDS)
            .followRedirects(true)
            .apply {
                addInterceptor(URLV2Interceptor())
                addInterceptor(HeaderInterceptor())
                addInterceptor(ErrorConvertInterceptor())
                if (BuildConfig.DEBUG) {
                    val logInterceptor = HttpLoggingInterceptor()
                    logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(logInterceptor)
                }
            }
            .build()
    }

    private fun createRetrofit(baseUrl: String) {
        mRetrofit = RetrofitUtils.createRetrofit(mOkHttpClient, baseUrl)
        mApiService = mRetrofit.create(ApiService::class.java)
    }

    fun getRetrofit(): Retrofit {
        return mRetrofit
    }

    fun getApiService(): ApiService {
        if (!this::mApiService.isInitialized) {
            val baseUrl = LocalData.getServerBaseUrl()
            if (baseUrl.isNotEmpty()) {
                createRetrofit(baseUrl)
            } else {
                throw Exception("baseUrl is empty ！")
            }
        } else {
            val currBaseUrl = RetrofitUtils.getRetrofitBaseUrl(mRetrofit)
            val baseUrl = LocalData.getServerBaseUrl()
            //Different hosts
            if (!currBaseUrl.startsWith(baseUrl)) {
                createRetrofit(baseUrl)
            }
        }
        return mApiService
    }

    fun getApiTest(testUrl: String): Single<FLResponse<Any>> {
        return Single.create {
            val build = Request.Builder().url(testUrl + API_PREFIX_V1 + TEST_URL).build()
            mOkHttpClient.newCall(build).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (!it.isDisposed) {
                        it.onError(e)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!it.isDisposed && response.isSuccessful && response.body != null) {
                        it.onSuccess(GsonUtils.fromJson(response.body?.string(),
                            object : TypeToken<FLResponse<Any>>() {}.type))
                    } else {
                        if (!it.isDisposed) {
                            it.onError(Exception("${response.code} ${response.message}"))
                        }
                    }
                }
            })
        }
    }
}