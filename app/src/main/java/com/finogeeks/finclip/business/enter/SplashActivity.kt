package com.finogeeks.finclip.business.enter

import android.os.Bundle
import com.finogeeks.finclip.R
import com.finogeeks.finclip.base.BaseActivity
import com.finogeeks.finclip.business.home.MainActivity
import com.finogeeks.finclip.http.SingleObserverImpl
import com.finogeeks.finclip.utils.ActivitySkip
import com.finogeeks.finclip.utils.extensions.autoDispose
import com.finogeeks.finclip.utils.extensions.io2Main
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun isFullscreen() = true

    override fun configView(savedInstanceState: Bundle?) {

        Single.timer(2000, TimeUnit.MILLISECONDS)
            .io2Main()
            .autoDispose(this)
            .subscribe(object : SingleObserverImpl<Long>() {
                override fun onSuccess(t: Long) {
                    super.onSuccess(t)
                    ActivitySkip.startActivityAndFinish(this@SplashActivity,
                        MainActivity::class.java)
                }
            })
    }
}