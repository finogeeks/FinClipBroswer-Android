package com.finogeeks.finclip.business.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import coil.load
import com.blankj.utilcode.util.ToastUtils
import com.finogeeks.finclip.R
import com.finogeeks.finclip.base.BaseActivity
import com.finogeeks.finclip.business.common.AreaCodeActivity
import com.finogeeks.finclip.business.common.AreaCodeKey
import com.finogeeks.finclip.business.common.DefaultAreaCode
import com.finogeeks.finclip.business.common.LocalData
import com.finogeeks.finclip.business.login.model.SmsType
import com.finogeeks.finclip.http.FLHttpException
import com.finogeeks.finclip.http.NetWorkManager
import com.finogeeks.finclip.http.SingleObserverImpl
import com.finogeeks.finclip.http.model.FLResponse
import com.finogeeks.finclip.http.model.request.CaptchaReq
import com.finogeeks.finclip.http.model.request.OrganVerifyCodeReq
import com.finogeeks.finclip.http.model.response.CaptchaResp
import com.finogeeks.finclip.utils.ActivitySkip
import com.finogeeks.finclip.utils.JsonUtils.toMap
import com.finogeeks.finclip.utils.extensions.*

class SendSMSCodeActivity : BaseActivity() {
    private lateinit var tv_to_ps: View
    private lateinit var iv_back: View
    private lateinit var tv_use_sms_login: View
    private lateinit var tv_a_code: TextView
    private lateinit var tv_sms_tl: TextView
    private lateinit var et_phone: EditText
    private lateinit var et_pic_code: EditText
    private lateinit var iv_code: ImageView
    private lateinit var group_code: Group
    private var mAreaCode = DefaultAreaCode
    private var codeData: CaptchaResp? = null
    private var smsType: SmsType? = null
    private val requestAreaCodeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    mAreaCode = data.getStringExtra(AreaCodeKey) ?: DefaultAreaCode
                    tv_a_code.text = " $mAreaCode "
                }
            }
        }

    override fun getLayoutId() = R.layout.activity_smslogin

    override fun configView(savedInstanceState: Bundle?) {
        iv_code = findViewById(R.id.iv_code)
        group_code = findViewById(R.id.group_code)
        tv_to_ps = findViewById(R.id.tv_to_ps)
        iv_back = findViewById(R.id.iv_back)
        tv_use_sms_login = findViewById(R.id.tv_use_sms_login)
        tv_a_code = findViewById(R.id.tv_a_code)
        et_phone = findViewById(R.id.et_phone)
        et_pic_code = findViewById(R.id.et_pic_code)
        tv_sms_tl = findViewById(R.id.tv_sms_tl)

    }

    override fun configData() {
        super.configData()
        et_phone.isFocusableInTouchMode = true
        et_phone.isFocusable = true
        et_phone.requestFocus()

        smsType = ActivitySkip.getIntentData(this, SmsType::class.java)
        group_code.isVisible = isForgetType()


        if (isForgetType()) {
            tv_sms_tl.text = getString(R.string.fpt_verify_phone)
            tv_to_ps.isVisible = false
        }

    }


    private fun isForgetType(): Boolean {
        return smsType?.type == SmsType.TypeForget
    }

    override fun configEvent() {
        super.configEvent()

        iv_back.onClick {
            finish()
        }

        tv_to_ps.onClick {
            ActivitySkip.startActivityAndFinish(this, UserNameLoginActivity::class.java)
        }

        tv_a_code.onClick {
            requestAreaCodeLauncher.launch(Intent(this, AreaCodeActivity::class.java))
        }

        iv_code.onClick {
            getCaptcha()
        }

        //Forget password, come in to get verification code
        if (isForgetType()) {
            iv_code.performClick()
        }


        et_phone.doAfterTextChanged {
            smsBtnIsEnable()
        }

        et_pic_code.doAfterTextChanged {
            smsBtnIsEnable()
        }

        tv_use_sms_login.onClick {
            val req = OrganVerifyCodeReq()
            val phone = et_phone.text.toString()
            req.type = if (isForgetType()) OrganVerifyCodeReq.TypeForget
            else OrganVerifyCodeReq.TypeOneClickVerifyCode
            req.phone = phone.accountEncrypt()
            req.areaCode = mAreaCode
            if (isForgetType()) {
                req.captchaId = codeData?.id
                req.captchaStr = et_pic_code.text.toString()
                smsType?.captchaId = req.captchaId
                smsType?.captchaStr = req.captchaStr
            }

            if (LocalData.isOPType()) {
                NetWorkManager.getApiService().operateGetVerifyCode(req.toMap())
            } else {
                NetWorkManager.getApiService().organGetVerifyCode(req)
            }.convertDataNullResp()
                .io2Main().autoDispose(this@SendSMSCodeActivity)
                .subscribe(object :
                    SingleObserverImpl<FLResponse<Any>>() {
                    override fun onSuccess(data: FLResponse<Any>) {
                        super.onSuccess(data)
                        smsType?.run {
                            this.phone = phone
                            this.areaCode = mAreaCode
                            ActivitySkip.startActivityWithData(this@SendSMSCodeActivity,
                                VerifySMSCodeActivity::class.java, this)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        ToastUtils.showShort(getString(R.string.fpt_sms_send_fail))
                    }
                })
        }
    }

    private fun getCaptcha() {
        val req = CaptchaReq()
        req.width = iv_code.width
        req.height = iv_code.height
        NetWorkManager.getApiService().getCaptcha(req).convertResp()
            .io2Main().autoDispose(this@SendSMSCodeActivity)
            .subscribe(object :
                SingleObserverImpl<CaptchaResp>() {
                override fun onSuccess(data: CaptchaResp) {
                    super.onSuccess(data)
                    try {
                        codeData = data
                        iv_code.load(data.imageBase64?.imageBase64ToBitmap())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    ToastUtils.showShort(getString(R.string.fpt_sms_send_fail))
                }
            })
    }

    private fun smsBtnIsEnable() {

        if (isForgetType()) {
            forgetTypeBtnStatus()
        } else {
            if (et_phone.text.toString().isNotEmpty()) {
                //China will limit the length of mobile phone numbers
                if (mAreaCode == DefaultAreaCode) {
                    tv_use_sms_login.isEnabled = et_phone.text.toString().length == 11
                } else {
                    tv_use_sms_login.isEnabled = true
                }
            } else {
                tv_use_sms_login.isEnabled = false
            }
        }
    }

    private fun forgetTypeBtnStatus() {
        if (et_phone.text.toString().isNotEmpty() && et_pic_code.text.toString().isNotEmpty()) {
            //China will limit the length of mobile phone numbers
            if (mAreaCode == DefaultAreaCode) {
                tv_use_sms_login.isEnabled = et_phone.text.toString().length == 11
            } else {
                tv_use_sms_login.isEnabled = true
            }
        } else {
            tv_use_sms_login.isEnabled = false
        }
    }

}