package com.finogeeks.finclip.business.common

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

import com.finogeeks.finclip.R
import com.finogeeks.finclip.base.BaseActivity
import com.finogeeks.finclip.business.common.model.URLBean
import com.finogeeks.finclip.utils.ActivitySkip

class WebViewActivity : BaseActivity() {
    private lateinit var webView: WebView

    override fun getLayoutId() = R.layout.activity_web_view

    override fun configView(savedInstanceState: Bundle?) {
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
    }

    override fun configData() {
        super.configData()
        val urlBean = ActivitySkip.getIntentData(this, URLBean::class.java)
        webView.loadUrl(urlBean?.url ?:"")

    }
    override fun configEvent() {
        super.configEvent()

    }
}