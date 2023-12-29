package com.ogzkesk.testproject.peerjs.web_rtc

import android.webkit.PermissionRequest
import android.webkit.WebChromeClient

class MWebChromeClient : WebChromeClient() {

    override fun onPermissionRequest(request: PermissionRequest?) {
        request?.grant(request.resources)
    }
}