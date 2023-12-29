package com.ogzkesk.testproject.peerjs.web_rtc

import android.webkit.WebView
import android.webkit.WebViewClient

class MWebViewClient(private val onPageFinished: () -> Unit) : WebViewClient() {



    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        onPageFinished.invoke()
    }


    companion object{
        private const val TAG = "MWebViewClient"
    }
}