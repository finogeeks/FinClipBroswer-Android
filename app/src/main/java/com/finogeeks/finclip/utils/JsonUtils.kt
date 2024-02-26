package com.finogeeks.finclip.utils

import com.blankj.utilcode.util.GsonUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONTokener
import org.json.JSONObject
import org.json.JSONException
import java.lang.reflect.Type

object JsonUtils {

    private val gson = Gson()
    @JvmStatic
    fun toJson(obj: Any?): String {
        return GsonUtils.toJson(obj)
    }

    @JvmStatic
    fun <T> fromJson(json: String, classOfT: Class<T>): T? {
        return GsonUtils.fromJson(json,classOfT)
    }

    @JvmStatic
    fun <T> fromJson(json: String, type: Type): T? {
        return GsonUtils.fromJson(json,type)
    }

    /**
     * Determining whether a string is a JsonObject
     *
     * @param value
     * @return
     */
    fun isJsonObject(value: String?): Boolean {
        try {
            val obj = JSONTokener(value).nextValue()
            if (obj is JSONObject) {
                return true
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return false
    }

    fun Any.toMap(): Map<String, String> {
        val json = gson.toJson(this)
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(json, type) ?: emptyMap()
    }
}