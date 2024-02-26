package com.finogeeks.finclip.business.common

import com.auth0.android.jwt.JWT
import com.blankj.utilcode.util.LogUtils
import com.finogeeks.finclip.http.NetWorkManager
import com.finogeeks.finclip.http.SingleObserverImpl
import com.finogeeks.finclip.http.model.request.RefreshTokenReq
import com.finogeeks.finclip.http.model.response.RefreshTokenResp

import com.finogeeks.finclip.utils.extensions.convertResp
import com.finogeeks.finclip.utils.extensions.io2Main
import java.util.*

object TokenHelper {

    private lateinit var timer:Timer
    private const val DF_TIME: Long = 30 * 60 * 1000 // 30 minutes

    @Volatile
    private var refreshInterval = DF_TIME

    @Volatile
    var isRefresh = false
        private set

    @Synchronized
    fun startTokenTimer() {
        if (isRefresh) {
            return
        }
        isRefresh = true
        refreshInterval = (getExpireTime() * 0.5).toLong()
//        refreshInterval = 5 * 1000
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                refreshToken()
            }
        }, refreshInterval, refreshInterval)
    }

    @Synchronized
    fun stopTokenTimer() {
        timer.cancel()
        isRefresh = false
    }

    private fun getExpireTime(): Long {
        val info = LocalData.getLoginInfo()
        val jwtToken = info?.jwtToken
        if (jwtToken != null && jwtToken.isNotEmpty()) {
            val jwt = JWT(jwtToken)
            val time = (jwt.expiresAt?.time ?: 0L) - (jwt.issuedAt?.time ?: 0L)
            if (time > 0) {
                return time
            }
        }
        return info?.jwtTokenExpireTime?.toLong()?.times(1000) ?: DF_TIME
    }

    private fun refreshToken() {

        val refreshTokenReq = RefreshTokenReq()
        val info = LocalData.getLoginInfo()
        refreshTokenReq.refreshToken = info?.refreshToken
        NetWorkManager.getApiService().refreshToken(refreshTokenReq).convertResp().io2Main()
            .subscribe(object :
                SingleObserverImpl<RefreshTokenResp>() {
                override fun onError(e: Throwable) {
                    super.onError(e)
                    LogUtils.dTag("TokenHelper", "${e.message}")
                }

                override fun onSuccess(data: RefreshTokenResp) {
                    super.onSuccess(data)
                    //Update token
                    if (info != null) {
                        info.jwtToken = data.jwtToken
                        info.jwtTokenExpireTime = data.expireTime
                        info.refreshToken = data.refreshToken
                        LocalData.setLoginInfo(info)
                        LogUtils.dTag("TokenHelper", "refreshToken")
                    }
                }
            })
    }

}