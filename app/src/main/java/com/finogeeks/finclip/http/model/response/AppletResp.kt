package com.finogeeks.finclip.http.model.response

import androidx.annotation.Keep
import com.finogeeks.finclip.http.model.AppletInfo
@Keep
class AppletResp {
    var hot: List<AppletInfo> = emptyList()
    var other: List<AppletInfo> = emptyList()
}