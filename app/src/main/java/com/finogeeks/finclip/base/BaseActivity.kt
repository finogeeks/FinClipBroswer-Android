package com.finogeeks.finclip.base

import android.os.Bundle
import android.view.LayoutInflater

import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ColorUtils
import com.finogeeks.finclip.R

abstract class BaseActivity : AppCompatActivity() {

    abstract fun getLayoutId(): Int

    open fun isStatusBarLightMode() = true

    open fun configStatusBarColor() = R.color.white

    open fun isFullscreen() = false


    override fun onCreate(savedInstanceState: Bundle?) {
        beforeSetView()
        super.onCreate(savedInstanceState)
        val contentView = LayoutInflater.from(this).inflate(getLayoutId(), null)
        setContentView(contentView)
        BarUtils.setStatusBarLightMode(this, isStatusBarLightMode())
        BarUtils.setStatusBarColor(this, ColorUtils.getColor(configStatusBarColor()))
        if (!isFullscreen()) {
            BarUtils.addMarginTopEqualStatusBarHeight(contentView)
        }
        configView(savedInstanceState)
        configData()
        configEvent()


    }

    open fun beforeSetView() {}
    abstract fun configView(savedInstanceState: Bundle?)
    open fun configData() {}
    open fun configEvent() {}

}