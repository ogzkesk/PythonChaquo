package com.ogzkesk.testproject.peerjs

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ogzkesk.testproject.peerjs.web_rtc.MWebChromeClient
import com.ogzkesk.testproject.peerjs.web_rtc.MWebViewClient
import com.ogzkesk.testproject.peerjs.web_rtc.RoomType

abstract class RTC(context: Context) : WebView(context) {

    abstract val settings: WebView
    abstract val roomId : String
    abstract val roomType: RoomType

    override fun getWebViewClient(): WebViewClient {
        return super.getWebViewClient()
    }



    init {


    }

}