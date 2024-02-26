package com.finogeeks.finclip.http.model.request

import androidx.annotation.Keep

// @param accountTraceId Account binding id, optional according to the situation
// @param type Get the type of CAPTCHA used, register: the CAPTCHA used when registering, modify: the CAPTCHA used when modifying, forget: Forget the password, one-click-verify-code: one-click login
// @param phone Mobile phone number, which needs to be encrypted through des
// @param areaCode International Area Code
// @param captchaId id of the CAPTCHA
// @param captchaStr The content of the CAPtCHA
@Keep
class OrganVerifyCodeReq {
    companion object{
        const val TypeRegister = "register"
        const val TypeModify = "modify"
        const val TypeForget = "forget"
        const val TypeOneClickVerifyCode = "one-click-verify-code"
    }
    var accountTraceId: String? = null
    var type: String? = null
    var phone: String? = null
    var areaCode: String? = null
    var captchaId: String? = null
    var captchaStr: String? = null
}