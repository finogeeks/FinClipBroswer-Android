package com.finogeeks.finclip.business.login


import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ToastUtils
import com.finogeeks.finclip.R
import com.finogeeks.finclip.base.BaseActivity
import com.finogeeks.finclip.business.common.LocalData
import com.finogeeks.finclip.business.home.MainActivity
import com.finogeeks.finclip.business.login.model.SmsType
import com.finogeeks.finclip.http.FLHttpException
import com.finogeeks.finclip.http.NetWorkManager
import com.finogeeks.finclip.http.SingleObserverImpl
import com.finogeeks.finclip.http.model.FLResponse
import com.finogeeks.finclip.http.model.request.LoginReqV2
import com.finogeeks.finclip.http.model.request.OrganVerifyCodeReq
import com.finogeeks.finclip.http.model.response.LoginResp
import com.finogeeks.finclip.utils.ActivitySkip
import com.finogeeks.finclip.utils.JsonUtils.toMap
import com.finogeeks.finclip.utils.extensions.*
import com.jkb.vcedittext.VerificationAction
import com.jkb.vcedittext.VerificationCodeEditText
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class VerifySMSCodeActivity : BaseActivity() {
    private lateinit var iv_back: View
    private lateinit var tv_sms_ok: TextView
    private lateinit var tv_resend: TextView
    private lateinit var tv_phone: TextView
    private lateinit var vc_code: VerificationCodeEditText
    private var smsType: SmsType? = null

    override fun getLayoutId() = R.layout.activity_input_smsactivity

    override fun configView(savedInstanceState: Bundle?) {

        iv_back = findViewById(R.id.iv_back)
        tv_sms_ok = findViewById(R.id.tv_sms_ok)
        tv_resend = findViewById(R.id.tv_resend)
        tv_phone = findViewById(R.id.tv_phone)
        vc_code = findViewById(R.id.vc_code)
    }

    override fun configData() {
        super.configData()
        smsType = ActivitySkip.getIntentData(this, SmsType::class.java)
        tv_phone.text = "${getString(R.string.fpt_v_code_send_to)} ${smsType?.phone} "

        vc_code.isFocusableInTouchMode = true
        vc_code.isFocusable = true
        vc_code.requestFocus()

        if (isForgetType()) {
            tv_sms_ok.text = getString(R.string.fpt_v_code_next)
        } else {
            tv_sms_ok.text = getString(R.string.fpt_login)
        }
        interval()
    }


    override fun configEvent() {
        super.configEvent()
        iv_back.onClick {
            finish()
        }

        tv_sms_ok.onClick {
            if (isForgetType()) {

                smsType?.run {
                    verifyCode = vc_code.text.toString()
                    ActivitySkip.startActivityWithData(this@VerifySMSCodeActivity,
                        ResetPasswordActivity::class.java,
                        this)
                }


            } else {
                login()
            }
        }

        tv_resend.onClick {
            tv_resend.isEnabled = false
            val req = OrganVerifyCodeReq()
            val phone = smsType?.phone ?: ""
            req.type =
                if (isForgetType()) OrganVerifyCodeReq.TypeForget else OrganVerifyCodeReq.TypeOneClickVerifyCode

            req.phone = phone.accountEncrypt()
            req.areaCode = smsType?.areaCode

            if (isForgetType()) {
                req.captchaId = smsType?.captchaId
                req.captchaStr = smsType?.captchaStr
            }

            if (LocalData.isOPType()) {
                NetWorkManager.getApiService().operateGetVerifyCode(req.toMap())
            } else {
                NetWorkManager.getApiService().organGetVerifyCode(req)
            }.convertDataNullResp()
                .io2Main().autoDispose(this@VerifySMSCodeActivity)
                .subscribe(object :
                    SingleObserverImpl<FLResponse<Any>>() {
                    override fun onSuccess(data: FLResponse<Any>) {
                        super.onSuccess(data)
                        interval()
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        tv_resend.isEnabled = true
                        ToastUtils.showShort(getString(R.string.fpt_sms_send_fail))
                    }
                })
        }

        vc_code.setOnVerificationCodeChangedListener(object :
            VerificationAction.OnVerificationCodeChangedListener {
            override fun onVerCodeChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.toString()?.run {
                    tv_sms_ok.isEnabled = length == 6
                }
            }

            override fun onInputCompleted(s: CharSequence?) {

            }
        })

    }

    private fun isForgetType(): Boolean {
        return smsType?.type == SmsType.TypeForget
    }


    private fun interval() {
        val count = 60L
        tv_resend.isEnabled = false
        Observable.intervalRange(0, count, 0, 1, TimeUnit.SECONDS)
            .compose(applySchedulersIO2MainOb())
            .autoDispose(this).subscribe(object :
                Observer<Long> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Long) {
                    tv_resend.setTextColor(ColorUtils.getColor(R.color.gray_9d9d9d))
                    tv_resend.text = "${getString(R.string.fpt_re_get_v_code)}(${count - t}s)"
                }

                override fun onError(e: Throwable) {}

                override fun onComplete() {
                    tv_resend.isEnabled = true
                    tv_resend.setTextColor(ColorUtils.getColor(R.color.blue_4285f4))
                    tv_resend.text = getString(R.string.fpt_re_get_v_code)
                }
            })
    }

    private fun login() {
        val rep = LoginReqV2()
        rep.from = LoginReqV2.FROM_APP
        rep.type = LoginReqV2.TYPE_ONE_CLICK_VERIFY_CODE
        rep.account = smsType?.phone?.accountEncrypt()
        rep.verifyCode = vc_code.text.toString()
        rep.areaCode = smsType?.areaCode
        rep.carrier = ""
        rep.oneClickToken = ""

        NetWorkManager.getApiService().organLoginV2(rep).convertResp().io2Main()
            .autoDispose(this)
            .subscribe(object :
                SingleObserverImpl<LoginResp>() {
                override fun onSuccess(data: LoginResp) {
                    super.onSuccess(data)
                    LocalData.setLoginInfo(data,true)
                    ActivitySkip.startActivityAndFinish(this@VerifySMSCodeActivity,
                        MainActivity::class.java)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    ToastUtils.showLong(getString(R.string.fpt_login_err))
                }
            })
    }
}