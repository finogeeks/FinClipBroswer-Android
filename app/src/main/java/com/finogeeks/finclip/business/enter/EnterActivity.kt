package com.finogeeks.finclip.business.enter


import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.finogeeks.finclip.R
import com.finogeeks.finclip.base.BaseActivity
import com.finogeeks.finclip.business.common.LocalData
import com.finogeeks.finclip.business.home.MainActivity
import com.finogeeks.finclip.business.login.LoginHelper
import com.finogeeks.finclip.http.NetWorkManager
import com.finogeeks.finclip.http.SingleObserverImpl
import com.finogeeks.finclip.http.model.request.RefreshTokenReq
import com.finogeeks.finclip.http.model.response.RefreshTokenResp
import com.finogeeks.finclip.utils.ActivitySkip
import com.finogeeks.finclip.utils.extensions.autoDispose
import com.finogeeks.finclip.utils.extensions.convertResp
import com.finogeeks.finclip.utils.extensions.io2Main


class EnterActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_enter
    override fun configView(savedInstanceState: Bundle?) {

        //Agreed to the agreement landed
        if (LocalData.isAgreeProtocol() && LocalData.isLogin()) {
            val refreshTokenReq = RefreshTokenReq()
            val info = LocalData.getLoginInfo()
            refreshTokenReq.refreshToken = info?.refreshToken
            NetWorkManager.getApiService().refreshToken(refreshTokenReq).convertResp().io2Main()
                .autoDispose(this).subscribe(object :
                    SingleObserverImpl<RefreshTokenResp>() {
                    override fun onError(e: Throwable) {
                        super.onError(e)
                        //Expiration
                        ToastUtils.showLong(getString(R.string.fpt_login_info_exp))
                        val loginInfo = LocalData.getLoginInfo()
                        loginInfo?.run {
                            jwtToken = ""
                            LocalData.setLoginInfo(this)
                        }

                        LoginHelper.toLoginPage(this@EnterActivity, LocalData.isPRType())
                    }

                    override fun onSuccess(data: RefreshTokenResp) {
                        super.onSuccess(data)
                        //Update token
                        if (info != null) {
                            info.jwtToken = data.jwtToken
                            info.jwtTokenExpireTime = data.expireTime
                            info.refreshToken = data.refreshToken
                            LocalData.setLoginInfo(info)
                        }
                        if(LocalData.isPRType()){
                            ActivitySkip.startActivityAndFinish(this@EnterActivity,
                                MainActivity::class.java)
                        }else{
                            ActivitySkip.startActivityAndFinish(this@EnterActivity,
                                SplashActivity::class.java)
                        }

                    }
                })
        } else {
            ActivitySkip.startActivityAndFinish(this@EnterActivity,
                ConfigServiceActivity::class.java)
        }
    }
}