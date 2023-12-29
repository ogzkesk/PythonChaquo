package com.ogzkesk.testproject.peerjs.web_rtc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.UUID

object WebRTCUtils {

    private const val TAG = "WebRTCUtils"
    const val FILE_PATH = "file:android_asset/call.html"
    private val permissions: Array<String> = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO
    )

    fun generateRoomId(): String {
        return UUID.randomUUID().toString()
    }


    fun initPermissions(activity: AppCompatActivity){
        val launcher =  activity
            .registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
//                result ->
//                val condition = !result.any { !it.value }
            }
        launcher.launch(permissions)
    }


    @SuppressLint("SetJavaScriptEnabled")
    fun initWebViewSettings(
        webView: WebView,
        onPageFinished: () -> Unit,
    ) {
        webView.run {
            webChromeClient = MWebChromeClient()
            webViewClient = MWebViewClient(onPageFinished)
            settings.mediaPlaybackRequiresUserGesture = false
            settings.javaScriptEnabled = true
        }
    }


    fun initializePeer(
        roomId: String?,
        roomType: RoomType?,
        webView: WebView,
        isAudioEnabled: Boolean,
        isVideoEnabled: Boolean
    ) {
        println("$TAG initializePeer type: $roomType, id: $roomId")
        if(roomId == null) {
            return
        }

        when (roomType) {
            RoomType.CREATE -> {
                callJsFunction(webView, "javascript:init('$roomId')")
                callJsFunction(webView, "javascript:startCall('$roomId')")
                toggleVideo(webView,isVideoEnabled)
                toggleAudio(webView,isAudioEnabled)
            }

            RoomType.JOIN -> {
                callJsFunction(webView, "javascript:init('${generateRoomId()}')")
                callJsFunction(webView, "javascript:startCall('$roomId')")
                toggleVideo(webView,isVideoEnabled)
                toggleAudio(webView,isAudioEnabled)
            }

            null -> {
            }
        }
    }


    fun toggleAudio(webView: WebView, b: Boolean) {
        callJsFunction(webView, "javascript:toggleAudio('$b')")
    }

    fun toggleVideo(webView: WebView, b: Boolean) {
        callJsFunction(webView, "javascript:toggleVideo('$b')")
    }

    fun checkPermissions(activity: Activity) : Boolean{
        return activity.checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                activity.checkSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED
    }

    private fun callJsFunction(webView: WebView?, function: String) {
        webView?.post { webView.loadUrl(function) }
    }

    fun clear(webView: WebView) {
        webView.apply {
            stopLoading()
            clearHistory()
            clearCache(true)
            clearMatches()
            clearFormData()
            clearSslPreferences()
            removeAllViews()
            destroy()
        }
    }
}