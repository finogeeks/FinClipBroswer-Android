package com.finogeeks.finclip.business.login


import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import coil.load
import com.blankj.utilcode.util.*
import com.finogeeks.finclip.R

import com.finogeeks.finclip.base.BaseActivity
import com.finogeeks.finclip.business.common.LocalData
import com.finogeeks.finclip.business.home.MainActivity
import com.finogeeks.finclip.business.login.model.SmsType
import com.finogeeks.finclip.http.FC_OPER_CAPTCHA_EMPTY_ERROR
import com.finogeeks.finclip.http.FLHttpException
import com.finogeeks.finclip.http.NetWorkManager
import com.finogeeks.finclip.http.SingleObserverImpl
import com.finogeeks.finclip.http.model.request.CaptchaReq
import com.finogeeks.finclip.http.model.request.LoginReq
import com.finogeeks.finclip.http.model.response.CaptchaResp
import com.finogeeks.finclip.http.model.response.LoginResp
import com.finogeeks.finclip.utils.ActivitySkip
import com.finogeeks.finclip.utils.extensions.*

class UserNameLoginActivity : BaseActivity() {
    private lateinit var iv_back: View
    private lateinit var tv_use_ps_login: View
    private lateinit var tv_fgt_psd: View
    private lateinit var iv_eye: View

    private lateinit var et_phone: EditText
    private lateinit var et_psd: EditText
    private lateinit var et_pic_code: EditText

    private lateinit var tv_to_sms: View
    private lateinit var group_code: View
    private lateinit var iv_code: ImageView
    private var codeData: CaptchaResp? = null


    override fun getLayoutId() = R.layout.activity_user_name_login

    override fun configView(savedInstanceState: Bundle?) {
        iv_back = findViewById(R.id.iv_back)
        tv_use_ps_login = findViewById(R.id.tv_use_ps_login)
        tv_fgt_psd = findViewById(R.id.tv_fgt_psd)
        iv_eye = findViewById(R.id.iv_eye)
        et_phone = findViewById(R.id.et_phone)
        et_psd = findViewById(R.id.et_psd)
        tv_to_sms = findViewById(R.id.tv_to_sms)
        et_pic_code = findViewById(R.id.et_pic_code)
        group_code = findViewById(R.id.group_code)
        iv_code = findViewById(R.id.iv_code)
    }

    override fun configData() {
        super.configData()
        if (LocalData.getUserType() == LocalData.UserType.EE_UAT) {
            tv_to_sms.isInvisible = false
        }

        et_phone.isFocusableInTouchMode = true
        et_phone.isFocusable = true
        et_phone.requestFocus()

    }


    override fun configEvent() {
        super.configEvent()

        iv_back.onClick {
            finish()
        }

        tv_to_sms.onClick {
            ActivitySkip.startActivityWithDataAndFinish(this, SendSMSCodeActivity::class.java,
                SmsType(type = SmsType.TypeLogin))
        }

        iv_eye.onClick {
            if (iv_eye.isSelected) {
                et_psd.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                et_psd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            iv_eye.isSelected = !iv_eye.isSelected
            if (et_psd.text.isNotEmpty()) {
                et_psd.setSelection(et_psd.text.toString().length)
            }
        }


        tv_fgt_psd.onClick {
            ActivitySkip.startActivityWithData(this, SendSMSCodeActivity::class.java, SmsType(type = SmsType.TypeForget))
        }

        et_phone.doAfterTextChanged {
            loginBtnIsEnable()
        }

        et_psd.doAfterTextChanged {
            loginBtnIsEnable()
        }

        et_pic_code.doAfterTextChanged {
            loginBtnIsEnable()
        }

        iv_code.onClick {
            getCaptcha()
        }

        tv_use_ps_login.onClick {
            val req = LoginReq()
            val account = et_phone.text.toString().trim().accountEncrypt()
            val password = et_psd.text.toString().trim()
            if (et_pic_code.isVisible) {
                val captcha = et_pic_code.text.toString().trim()
                req.captchaStr = captcha
                req.captchaId = codeData?.id
            }

            NetWorkManager.getApiService().getPasswordEncryptionType().map {
                req.account = account
                if (it.isSuccess() && it.data != null && it.data?.type == 1) {
                    req.password = password.passwordEncryptV1()
                } else {
                    req.password = password.passwordEncrypt()
                }
                return@map req
            }.onErrorReturn {
                val req = LoginReq()
                req.account = account
                req.password = password.passwordEncrypt()
                return@onErrorReturn req
            }.flatMap {
                if (LocalData.isOPType()) {
                    NetWorkManager.getApiService().operationLogin(it)
                } else {
                    NetWorkManager.getApiService().organLogin(it)
                }
            }.convertResp().io2Main().autoDispose(this).subscribe(object :
                SingleObserverImpl<LoginResp>() {
                override fun onSuccess(data: LoginResp) {
                    super.onSuccess(data)
                    LocalData.setLoginInfo(data,true)
                    ActivitySkip.startActivityAndFinish(this@UserNameLoginActivity,
                        MainActivity::class.java)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e is FLHttpException) {
                        when (e.errCode) {
                            FC_OPER_CAPTCHA_EMPTY_ERROR -> {
                                getCaptcha()
                            }
                            else -> {
                                ToastUtils.showLong(getString(R.string.fpt_login_err))
                            }
                        }
                    } else {
                        ToastUtils.showLong(getString(R.string.fpt_login_err))
                    }
                }
            })
        }
    }

    private fun getCaptcha() {
        val req = CaptchaReq()
        req.width = iv_code.width
        req.height = iv_code.height
        NetWorkManager.getApiService().getCaptcha(req).convertResp()
            .io2Main().autoDispose(this@UserNameLoginActivity)
            .subscribe(object :
                SingleObserverImpl<CaptchaResp>() {
                override fun onSuccess(data: CaptchaResp) {
                    super.onSuccess(data)
                    try {
                        codeData = data
                        group_code.isVisible = true
                        iv_code.load(data.imageBase64?.imageBase64ToBitmap())
                        loginBtnIsEnable()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    ToastUtils.showShort(getString(R.string.fpt_sms_send_fail))
                }
            })
    }

    private fun loginBtnIsEnable() {
        if (et_pic_code.isVisible) {
            tv_use_ps_login.isEnabled =
                et_phone.text.toString().isNotEmpty() && et_psd.text.toString()
                    .isNotEmpty() && et_pic_code.text.toString()
                    .isNotEmpty() && et_pic_code.text.toString().length == 4
        } else {
            tv_use_ps_login.isEnabled =
                et_phone.text.toString().isNotEmpty() && et_psd.text.toString().isNotEmpty()
        }

    }
}