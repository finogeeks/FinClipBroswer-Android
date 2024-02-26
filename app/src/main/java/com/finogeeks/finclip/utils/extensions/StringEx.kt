package com.finogeeks.finclip.utils.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.ImageUtils
import com.finogeeks.finclip.utils.DES
import java.net.URL


fun String.md5(): String {
    return EncryptUtils.encryptMD5ToString(this)
}

fun String.isURL(): Boolean {
    return try {
        URL(this)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun String?.notNull(defaultVal: String = ""): String {
    if (this == null) {
        return defaultVal
    }
    return this
}


fun String.passwordEncrypt(): String {
    return this.md5().lowercase()
}

fun String.passwordEncryptV1(): String {
    val md5Psd = this.md5().lowercase()
    val start =
        EncryptUtils.encryptSHA256ToString(EncryptUtils.encryptSHA256ToString(this).lowercase())
            .lowercase()
    val end =
        EncryptUtils.encryptSHA256ToString(EncryptUtils.encryptSHA256ToString(md5Psd).lowercase())
            .lowercase()
    return start + "_" + end
}

fun String.accountEncrypt(): String {
    if(isEmpty()){
        return ""
    }
    return DES.encrypt(this, "w\$D5%8x@")
}

fun String.accountDecrypt(): String {
    if(isEmpty()){
        return ""
    }
    return DES.decrypt(this,
        "w\$D5%8x@")
}


fun String.imageBase64ToBitmap(): Bitmap {
    val base64 = this.substring(this.indexOf(",") + 1)
    return ImageUtils.bytes2Bitmap(EncodeUtils.base64Decode(base64))
}

