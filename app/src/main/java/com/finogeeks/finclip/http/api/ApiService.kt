package com.finogeeks.finclip.http.api


import com.finogeeks.finclip.http.NetWorkManager.LOGIN_V2
import com.finogeeks.finclip.http.model.FLResponse
import com.finogeeks.finclip.http.model.request.*
import com.finogeeks.finclip.http.model.response.*
import io.reactivex.Single
import retrofit2.http.*


interface ApiService {

    // Enterprise account login
    @POST("applets-ecol-account/organ/login")
    fun organLogin(@Body req: LoginReq): Single<FLResponse<LoginResp>>

    // Operator account login
    @POST("applets-ecol-account/operation/login")
    fun operationLogin(@Body req: LoginReq): Single<FLResponse<LoginResp>>

    // Encryption method judgment
    @GET("applets-ecol-account/common/password/encryption")
    fun getPasswordEncryptionType(): Single<FLResponse<PasswordEncryptionTypeResp>>

    // Refresh token
    @POST("applets-ecol-device-security/refresh/token")
    fun refreshToken(@Body req: RefreshTokenReq): Single<FLResponse<RefreshTokenResp>>

    // Get the graphic CAPTCHA
    @POST("applets-ecol-account/common/captcha")
    fun getCaptcha(@Body req: CaptchaReq): Single<FLResponse<CaptchaResp>>

    // The enterprise obtains the mobile phone verification code
    @POST("applets-ecol-account/organ/phone/verify-code")
    fun organGetVerifyCode(@Body req: OrganVerifyCodeReq):Single<FLResponse<Any>>

    // The operator gets the CAPTCHA
    @GET("applets-ecol-account/operation/worker/phone/verifyCode")
    fun operateGetVerifyCode(@QueryMap req: Map<String,String>): Single<FLResponse<Any>>

    // Enterprise account login V2, currently mainly used for one-click login \ SMS
    @POST(LOGIN_V2)
    fun organLoginV2(@Body req: LoginReqV2): Single<FLResponse<LoginResp>>

    // Enterprise administrator or member forgot login password
    @POST("applets-ecol-account/organ/account/password/forget")
    fun organUpdatePassword(@Body req: UpdatePasswordReq): Single<FLResponse<Any>>

    // The operator forgot the password to change the password
    @POST("applets-ecol-account/operation/worker/forgot/password")
    fun operateUpdatePassword(@Body req: UpdatePasswordReq):Single<FLResponse<Any>>

    @GET("applets-ecol-account/organ/auth/get-evn")
    fun getEnv(): Single<FLResponse<Env>>

    // Home page applet list
    @GET("finstore/dev/config/helper")
    fun getApplets(
        @Query("type") type: String = "finclip",
        @Query("pageNo") pageNo: Int = 0,
        @Query("pageSize") pageSize: Int = 100,
    ): Single<FLResponse<AppletResp>>

    @GET("finstore/dev/config/helper")
    fun getPriApplets(
        @Query("classification") type: String = "published",
        @Query("pageNo") pageNo: Int = 0,
        @Query("pageSize") pageSize: Int = 100,
    ): Single<FLResponse<AppletResp>>

}

