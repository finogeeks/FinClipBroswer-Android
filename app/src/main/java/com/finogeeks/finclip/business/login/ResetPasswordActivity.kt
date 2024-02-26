package com.finogeeks.finclip.business.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.finogeeks.finclip.R
import com.finogeeks.finclip.base.BaseActivity
import com.finogeeks.finclip.business.common.LocalData
import com.finogeeks.finclip.business.login.model.SmsType
import com.finogeeks.finclip.http.FLHttpException
import com.finogeeks.finclip.http.NetWorkManager
import com.finogeeks.finclip.http.SingleObserverImpl
import com.finogeeks.finclip.http.model.FLResponse
import com.finogeeks.finclip.http.model.request.UpdatePasswordReq
import com.finogeeks.finclip.utils.ActivitySkip
import com.finogeeks.finclip.utils.extensions.*

class ResetPasswordActivity : BaseActivity() {
    // 6-20 characters, including upper - and lowercase letters, numbers, and symbols
    private val RULE_PSD = "(?![0-9A-Z]+\$)(?![0-9a-z]+\$)(?![a-zA-Z]+\$)[0-9A-Za-z]{6,20}\$"

    private lateinit var iv_back: View
    private lateinit var iv_eye_new: View
    private lateinit var iv_eye: View
    private lateinit var et_new_psd: EditText
    private lateinit var et_psd: EditText
    private lateinit var tv_new_psd_count: TextView
    private lateinit var tv_psd_count: TextView
    private lateinit var tv_reset_psd: View
    private var smsType: SmsType? = null


    override fun getLayoutId(): Int = R.layout.activity_reset_password

    override fun configView(savedInstanceState: Bundle?) {
        iv_back = findViewById(R.id.iv_back)
        iv_eye_new = findViewById(R.id.iv_eye_new)
        iv_eye = findViewById(R.id.iv_eye)
        et_new_psd = findViewById(R.id.et_new_psd)
        et_psd = findViewById(R.id.et_psd)
        tv_new_psd_count = findViewById(R.id.tv_new_psd_count)
        tv_psd_count = findViewById(R.id.tv_psd_count)
        tv_reset_psd = findViewById(R.id.tv_reset_psd)
    }

    override fun configData() {
        super.configData()
        smsType = ActivitySkip.getIntentData(this, SmsType::class.java)

        et_new_psd.isFocusableInTouchMode = true
        et_new_psd.isFocusable = true
        et_new_psd.requestFocus()
    }

    override fun configEvent() {
        super.configEvent()
        iv_back.onClick {
            finish()
        }

        iv_eye_new.onClick {
            if (iv_eye_new.isSelected) {
                et_new_psd.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                et_new_psd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            iv_eye_new.isSelected = !iv_eye_new.isSelected
            if (et_new_psd.text.isNotEmpty()) {
                et_new_psd.setSelection(et_new_psd.text.toString().length)
            }
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

        et_new_psd.doAfterTextChanged {
            val text = it.toString()
            val count = text.length
            tv_new_psd_count.text = "${count}/20"
            resetBtnEnable()
        }

        et_psd.doAfterTextChanged {
            val text = it.toString()
            val count = text.length
            tv_psd_count.text = "${count}/20"
            resetBtnEnable()
        }

        tv_reset_psd.onClick {
            if (!et_new_psd.text.toString().matches(Regex(RULE_PSD))) {
                ToastUtils.showLong(getString(R.string.fpt_psd_type_err))
                return@onClick
            }
            if (et_new_psd.text.toString() != et_psd.text.toString()) {
                ToastUtils.showLong(getString(R.string.fpt_psd_two_no_same))
                return@onClick
            }
            val req = UpdatePasswordReq()
            val phone = smsType?.phone?.accountEncrypt()
            val password = et_new_psd.text.toString()
            req.verifyCode = smsType?.verifyCode
            req.phone = phone

            NetWorkManager.getApiService().getPasswordEncryptionType().map {
                if (it.isSuccess() && it.data != null && it.data?.type == 1) {
                    req.newPassword = password.passwordEncryptV1()
                } else {
                    req.newPassword = password.passwordEncrypt()
                }
                return@map req
            }.onErrorReturn {
                req.newPassword = password.passwordEncrypt()
                return@onErrorReturn req
            }.flatMap {
                if(LocalData.isOPType()){
                    NetWorkManager.getApiService().operateUpdatePassword(it)
                }else{
                    NetWorkManager.getApiService().organUpdatePassword(it)
                }
            }.convertDataNullResp()
                .io2Main().autoDispose(this@ResetPasswordActivity).subscribe(object :
                    SingleObserverImpl<FLResponse<Any>>() {
                    override fun onSuccess(data: FLResponse<Any>) {
                        ActivityUtils.finishAllActivities()
                        LoginHelper.toLoginPage(this@ResetPasswordActivity, LocalData.isPRType())
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        ToastUtils.showShort(getString(R.string.fpt_psd_change_fail))
                    }
                })
        }
    }

    // Whether the reset button is clickable
    private fun resetBtnEnable() {
        tv_reset_psd.isEnabled =
            et_new_psd.text.toString().isNotEmpty() && et_psd.text.toString().isNotEmpty()
    }
}

