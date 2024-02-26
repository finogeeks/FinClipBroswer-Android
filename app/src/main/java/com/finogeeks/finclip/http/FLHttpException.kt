package com.finogeeks.finclip.http



const val FC_ORGAN_ACCOUNT_OR_PWD_ERROR = "FC_ORGAN_ACCOUNT_OR_PWD_ERROR" // Wrong account or password
const val FC_OPER_CAPTCHA_EMPTY_ERROR = "FC_OPER_CAPTCHA_EMPTY_ERROR" // Captcha required
const val FC_ORGAN_PHONE_GET_VERIFY_CODE_TOO_MANY = "FC_ORGAN_PHONE_GET_VERIFY_CODE_TOO_MANY" // Please retrieve the CAPTCHA later


class FLHttpException(val errCode: String? = null, val error: String? = null) : Throwable()

