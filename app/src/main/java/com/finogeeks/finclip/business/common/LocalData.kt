package com.finogeeks.finclip.business.common

import com.blankj.utilcode.util.SPUtils
import com.finogeeks.finclip.FinAppletInitializer
import com.finogeeks.finclip.base.BaseApplication
import com.finogeeks.finclip.http.NetWorkManager.FIN_CLIP_EE_UAT_BASE_URL
import com.finogeeks.finclip.http.NetWorkManager.FIN_CLIP_EE_UAT_BASE_URL_B
import com.finogeeks.finclip.http.NetWorkManager.FIN_CLIP_EE_UAT_BASE_URL_TEST
import com.finogeeks.finclip.http.model.response.LoginResp
import com.finogeeks.finclip.utils.JsonUtils


object LocalData {
    private const val LOCAL_DATA_SP_NAME = "Local-Data-Fin-Clip-Private"
    private val FIN_CLIP_SP by lazy { SPUtils.getInstance(LOCAL_DATA_SP_NAME) }
    private const val AGREE_PROTOCOL_KEY = "AGREE_PROTOCOL_KEY"
    private const val APP_USER_TYPE_KEY = "APP_USER_TYPE_KEY"
    private const val USER_LOGIN_INFO_KEY = "USER_LOGIN_INFO_KEY"
    private const val SERVER_HOST_KEY = "SERVER_HOST_KEY"
    const val ENV_UAT = "mop-uat"
    const val ENV_PRI = "mop-private"

    private var loginInfo: LoginResp? = null

    private val uatHosts = listOf(FIN_CLIP_EE_UAT_BASE_URL, FIN_CLIP_EE_UAT_BASE_URL_B,
        FIN_CLIP_EE_UAT_BASE_URL_TEST)

    enum class UserType {
        EE_UAT, // Enterprise-UAT
        EE_PR,  // Enterprise-Private
        OP_UAT, // Operations-UAT
        OP_PR   // Operations-Private
    }

    fun isAgreeProtocol(): Boolean {
        return FIN_CLIP_SP.getBoolean(AGREE_PROTOCOL_KEY)
    }

    fun agreedProtocol() {
        FIN_CLIP_SP.put(AGREE_PROTOCOL_KEY, true)
    }

    fun isLogin(): Boolean {
        if (getLoginInfo() != null) {
            return true
        }
        return false
    }

    fun getLoginInfo(): LoginResp? {
        return if (this.loginInfo == null) {
            val infoJson = FIN_CLIP_SP.getString(USER_LOGIN_INFO_KEY)
            if (infoJson.isNullOrEmpty()) {
                null
            } else {
                JsonUtils.fromJson(infoJson, LoginResp::class.java)
            }
        } else {
            this.loginInfo
        }
    }

    fun setLoginInfo(info: LoginResp, isLogin: Boolean = false) {
        this.loginInfo = info
        val json = JsonUtils.toJson(info)
        FIN_CLIP_SP.put(USER_LOGIN_INFO_KEY, json)
        TokenHelper.startTokenTimer()
        if (isLogin) {
            FinAppletInitializer.init(BaseApplication.context)
        }
    }

    fun clearLoginInfo() {
        this.loginInfo = null
        FIN_CLIP_SP.put(USER_LOGIN_INFO_KEY, "")
    }


    fun getUserType(): UserType? {
        val type = FIN_CLIP_SP.getString(APP_USER_TYPE_KEY)
        return UserType.values().firstOrNull { it.name == type }
    }

    fun setUserType(type: UserType) {
        FIN_CLIP_SP.put(APP_USER_TYPE_KEY, type.name)
    }

    fun isOPType(): Boolean {
        val type = getUserType()
        return type == UserType.OP_UAT || type == UserType.OP_PR
    }

    fun isPRType(): Boolean {
        val type = getUserType()
        return type == UserType.EE_PR || type == UserType.OP_PR
    }


    fun setServerBaseUrl(host: String) {
        if (host.isEmpty()) {
            return
        }
        var saveHost = host
        if (host.endsWith("/")) {
            saveHost = host.dropLast(1)
        }
        FIN_CLIP_SP.put(SERVER_HOST_KEY, saveHost)
    }

    fun getServerBaseUrl(): String {
        return FIN_CLIP_SP.getString(SERVER_HOST_KEY, "")
    }

    fun useFinClipBaseURL() {
        setServerBaseUrl(FIN_CLIP_EE_UAT_BASE_URL)
    }

    fun isUATHost(baseURL:String): Boolean {
        return uatHosts.contains(baseURL)
    }
}