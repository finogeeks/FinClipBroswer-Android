package com.finogeeks.finclip.utils.extensions

import android.view.View

inline fun View.onClick(delayMillis: Long = 500L, crossinline onClick: (v: View) -> Unit) {
    this.setOnClickListener {
        this.isClickable = false
        onClick(it)
        this.postDelayed({
            this.isClickable = true
        }, delayMillis)
    }
}