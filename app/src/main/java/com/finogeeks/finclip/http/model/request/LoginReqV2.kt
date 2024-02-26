package com.finogeeks.finclip.http.model.request

import androidx.annotation.Keep

// Login request body V2 for one-click login
//
// @param carrier Type of mobile phone service provider -- operator, mobile: mobile, unicom: unicom, telecom: telecom
// @param from Source：app、fide、web
// @param type Types：oneClick: Phone one-click login (if the phone number does not have automatic registration)，oneClick-verifyCode: One-click login using mobile number + verification code (if the mobile number does not have automatic registration)
// @param account Account number - Mobile phone number needs des encryption
// @param verifyCode Verification code
// @param oneClickToken Mobile verification token for app login
// @param areaCode International area code
@Keep
class LoginReqV2 {
    companion object {
        const val TYPE_ONE_CLICK = "oneClick"
        const val TYPE_ONE_CLICK_VERIFY_CODE = "oneClick-verifyCode"
        const val FROM_APP = "app"
        const val FROM_FIDE = "fide"
        const val FROM_WEB = "web"
    }

    var carrier: String? = null
    var from: String? = null
    var type: String? = null
    var account: String? = null
    var verifyCode: String? = null
    var oneClickToken: String? = null
    var areaCode: String? = null
}