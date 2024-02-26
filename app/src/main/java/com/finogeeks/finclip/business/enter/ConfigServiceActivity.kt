package com.finogeeks.finclip.business.enter


import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isInvisible
import androidx.core.widget.doAfterTextChanged
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ToastUtils

import com.finogeeks.finclip.R
import com.finogeeks.finclip.base.BaseActivity
import com.finogeeks.finclip.business.common.LocalData
import com.finogeeks.finclip.business.common.ScanQRCodeActivity
import com.finogeeks.finclip.business.common.WebViewActivity
import com.finogeeks.finclip.business.common.model.URLBean
import com.finogeeks.finclip.business.login.LoginHelper
import com.finogeeks.finclip.http.NetWorkManager
import com.finogeeks.finclip.http.SingleObserverImpl
import com.finogeeks.finclip.http.model.response.Env
import com.finogeeks.finclip.utils.ActivitySkip
import com.finogeeks.finclip.utils.extensions.*
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.FullScreenPopupView
import io.reactivex.Single
import kotlin.system.exitProcess


class ConfigServiceActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_config_service
    private lateinit var iv_close: View
    private lateinit var et_server_url: EditText
    private lateinit var iv_apply: View
    private lateinit var btn_scan_qr: View
    private lateinit var btn_finclip_url: View
    private val requestQRDataLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val baseUrl = result.data?.getStringExtra(ScanQRCodeActivity.EXTRA_RESULT) ?: ""
                if (!baseUrl.isURL()) {
                    ToastUtils.showLong(R.string.fpt_qr_validation_failed)
                } else {
                    testURL(baseUrl, fail = {
                        ToastUtils.showLong(R.string.fpt_qr_validation_failed)
                    })
                }

            }
        }

    private fun testURL(baseUrl: String, fail: () -> Unit) {
        NetWorkManager.getApiTest(baseUrl).flatMap {
            LocalData.setServerBaseUrl(baseUrl)
            if(LocalData.isUATHost(baseUrl)){
                return@flatMap Single.just(Env().apply { env = LocalData.ENV_UAT})
            }
            return@flatMap NetWorkManager.getApiService().getEnv().convertResp()
                .onErrorReturn { Env() } // An exception in the request environment is treated as a private environment
        }.io2Main()
            .autoDispose(this).subscribe(object : SingleObserverImpl<Env>() {
                override fun onError(e: Throwable) {
                    super.onError(e)
                    fail.invoke()
                }

                override fun onSuccess(data: Env) {
                    super.onSuccess(data)
                    if (data.env == LocalData.ENV_UAT) {
                        LoginHelper.toLoginPage(this@ConfigServiceActivity, false)
                    } else {
                        LoginHelper.toLoginPage(this@ConfigServiceActivity, true)
                    }
                }
            })
    }


    override fun configView(savedInstanceState: Bundle?) {
        iv_close = findViewById(R.id.iv_close)
        et_server_url = findViewById(R.id.et_server_url)
        iv_apply = findViewById(R.id.iv_apply)
        btn_scan_qr = findViewById(R.id.btn_scan_qr)
        btn_finclip_url = findViewById(R.id.btn_finclip_url)
    }


    override fun configData() {
        super.configData()
        et_server_url.setText(LocalData.getServerBaseUrl())
        val isAgree = LocalData.isAgreeProtocol()
        if (isAgree) {
            iv_close.isInvisible = false
        } else {
            iv_close.isInvisible = true
            showProtocolPop()
        }
    }


    override fun configEvent() {
        super.configEvent()

        val configURL = et_server_url.text?.toString()
        if (configURL.isNullOrEmpty()) {
            iv_apply.isEnabled = false
        }

        et_server_url.doAfterTextChanged {
            iv_apply.isEnabled = !et_server_url.text?.toString().isNullOrEmpty()
        }

        configBaseUrlAction()

        btn_scan_qr.onClick {
            val intent = Intent(this, ScanQRCodeActivity::class.java)
            requestQRDataLauncher.launch(intent)
        }

        btn_finclip_url.onClick {
            LocalData.useFinClipBaseURL()
            LoginHelper.toLoginPage(this, false)
        }

        iv_close.onClick {
            finish()
        }
    }

    private fun configBaseUrlAction() {
        iv_apply.onClick {
            val baseUrl = et_server_url.text.toString()
            if (!baseUrl.isURL()) {
                ToastUtils.showLong(R.string.fpt_error_url)
            } else {

                testURL(baseUrl, fail = {
                    ToastUtils.showLong(R.string.fpt_invalid_url)
                })
            }
        }
    }


    private fun showProtocolPop() {
        XPopup.Builder(this)
            .dismissOnBackPressed(false)
            .dismissOnTouchOutside(false)
            .isDestroyOnDismiss(true)
            .asCustom(object : FullScreenPopupView(this) {
                override fun getImplLayoutId(): Int {
                    return R.layout.pop_protocol_layout
                }

                override fun onCreate() {
                    super.onCreate()
                    contentView.findViewById<View>(R.id.tv_agree).onClick {
                        LocalData.agreedProtocol()
                        dismiss()
                    }
                    contentView.findViewById<View>(R.id.tv_do_not_use).onClick {
                        dismiss()
                        exitProcess(0)
                    }

                    contentView.findViewById<TextView>(R.id.tv_content).apply {
                        movementMethod = LinkMovementMethod.getInstance()
                        highlightColor = ColorUtils.getColor(R.color.color_transparent)
                        text = protocolText()
                    }

                }
            }).show()
    }


    private fun protocolText(): SpannableStringBuilder {
        val fullText = getString(R.string.fpt_clause_content)

        val spannableStringBuilder = SpannableStringBuilder(fullText)

        val serviceAgreementClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                ActivitySkip.startActivityWithData(this@ConfigServiceActivity,
                    WebViewActivity::class.java,
                    URLBean().apply { url = "https://www.finclip.com/terms#service" })
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ColorUtils.getColor(R.color.blue_409eff)
                ds.isUnderlineText = false

            }
        }

        val privacyPolicyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                ActivitySkip.startActivityWithData(this@ConfigServiceActivity,
                    WebViewActivity::class.java,
                    URLBean().apply { url = "https://www.finclip.com/terms#privacy" })
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ColorUtils.getColor(R.color.blue_409eff)
                ds.isUnderlineText = false

            }
        }

        val text1 = getString(R.string.fpt_service_agreement)
        val serviceAgreementStart = fullText.indexOf(text1)
        val serviceAgreementEnd = serviceAgreementStart + text1.length
        spannableStringBuilder.setSpan(serviceAgreementClickableSpan,
            serviceAgreementStart,
            serviceAgreementEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val text2 = getString(R.string.fpt_privacy_policy)
        val privacyPolicyStart = fullText.indexOf(text2)
        val privacyPolicyEnd = privacyPolicyStart + text2.length
        spannableStringBuilder.setSpan(privacyPolicyClickableSpan,
            privacyPolicyStart,
            privacyPolicyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannableStringBuilder
    }

}