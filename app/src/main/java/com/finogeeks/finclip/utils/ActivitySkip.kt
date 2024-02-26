package com.finogeeks.finclip.utils

import com.finogeeks.finclip.utils.JsonUtils.toJson
import com.finogeeks.finclip.utils.JsonUtils.fromJson
import android.content.Intent
import android.app.Activity
import android.content.Context

object ActivitySkip {
    const val INTENT_EXTRA_DEFAULT_KEY = "INTENT_EXTRA_KEY"
    fun putDataToIntent(data: Any): Intent {
        return putDataToIntent(INTENT_EXTRA_DEFAULT_KEY, data)
    }

    private fun putDataToIntent(extraKey: String, data: Any): Intent {
        val intent = Intent()
        val result = toJson(data)
        intent.putExtra(extraKey, result)
        return intent
    }

    private fun <T> getIntentData(intent: Intent, extraKey: String, clazz: Class<T>): T? {
        val result = intent.getStringExtra(extraKey) ?:return null
        return fromJson(result, clazz)
    }

    fun <T> getIntentData(intent: Intent, clazz: Class<T>): T? {
        return getIntentData(intent, INTENT_EXTRA_DEFAULT_KEY, clazz)
    }

    fun <T> getIntentData(current: Activity, clazz: Class<T>): T? {
        return getIntentData(current, INTENT_EXTRA_DEFAULT_KEY, clazz)
    }

    private fun <T> getIntentData(current: Activity, extraKey: String?, clazz: Class<T>): T? {
        val intent = current.intent
        val result = intent.getStringExtra(extraKey) ?:return null
        return fromJson(result, clazz)
    }

    fun startActivity(from: Context, to: Class<out Activity>) {
        val intent = Intent(from, to)
        from.startActivity(intent)
    }

    fun startActivityWithData(from: Context, to: Class<out Activity>, data: Any) {
        startActivityWithData(from, to, INTENT_EXTRA_DEFAULT_KEY, data)
    }

    private fun startActivityWithData(
        from: Context,
        to: Class<out Activity>,
        extraKey: String,
        data: Any,
    ) {
        val intent = Intent(from, to)
        val result = toJson(data)
        intent.putExtra(extraKey, result)
        from.startActivity(intent)
    }

    fun startActivityAndFinish(from: Activity, to: Class<out Activity>) {
        startActivity(from, to)
        from.finish()
    }

    fun startActivityWithDataAndFinish(from: Activity, to: Class<out Activity>, data: Any) {
        startActivityWithData(from, to, data)
        from.finish()
    }
}