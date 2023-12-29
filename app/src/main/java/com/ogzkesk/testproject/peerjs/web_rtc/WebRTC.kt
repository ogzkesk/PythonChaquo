package com.ogzkesk.testproject.peerjs.web_rtc

import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import kotlin.Throws

// TODO callTypes
// TODO onClick smallVideoScreen -> fullScreen
// TODO deepLinks

class WebRTC private constructor(
    val webView: WebView,
    val activity: AppCompatActivity,
    val callType: CallType, // later
    private var isVideoEnabled: Boolean,
    private var isAudioEnabled: Boolean,
) {

    // change
    private var roomType: RoomType? = null
    private var roomId: String? = null

    init {
        WebRTCUtils.initWebViewSettings(webView, ::onPageFinished)
    }

    @Throws(PermissionException::class)
    fun createRoom() {
        if (!WebRTCUtils.checkPermissions(activity)) {
            throw PermissionException()
        }

        roomId = WebRTCUtils.generateRoomId()
        roomType = RoomType.CREATE
        webView.loadUrl(WebRTCUtils.FILE_PATH)
    }

    @Throws(PermissionException::class)
    fun joinRoom(id: String) {
        if (!WebRTCUtils.checkPermissions(activity)) {
            throw PermissionException()
        }

        roomId = id
        roomType = RoomType.JOIN
        webView.loadUrl(WebRTCUtils.FILE_PATH)
    }

    private fun onPageFinished() {
        WebRTCUtils.initializePeer(
            roomId,
            roomType,
            webView,
            isAudioEnabled,
            isVideoEnabled
        )
    }

    fun toggleAudio() {
        isAudioEnabled = !isAudioEnabled
        WebRTCUtils.toggleAudio(webView, isAudioEnabled)
    }

    fun toggleVideo() {
        isVideoEnabled = !isVideoEnabled
        WebRTCUtils.toggleVideo(webView, isVideoEnabled)
    }

    fun isVideoEnabled(): Boolean {
        return this.isVideoEnabled
    }

    fun isAudioEnabled(): Boolean {
        return this.isAudioEnabled
    }

    fun getRoomId(): String? {
        return this.roomId
    }

    fun isPermissionsGranted(): Boolean {
        return WebRTCUtils.checkPermissions(activity)
    }

    fun release() {
        WebRTCUtils.clear(webView)
    }

    class Builder(private val activity: AppCompatActivity) {

        private lateinit var webView: WebView
        private lateinit var callType: CallType
        private var isAudioEnabled: Boolean = true
        private var isVideoEnabled: Boolean = true

        fun setWebView(webView: WebView) = apply {
            this.webView = webView
        }

        fun setCallType(callType: CallType) = apply {
            this.callType = callType
        }

        fun requestPermissions() = apply {
            WebRTCUtils.initPermissions(activity)
        }

        fun setAudioDisabled() = apply {
            isAudioEnabled = false
        }

        fun setVideoDisabled() = apply {
            isVideoEnabled = false
        }

        fun build(): WebRTC {

            return WebRTC(
                webView = webView,
                activity = activity,
                isAudioEnabled = isAudioEnabled,
                isVideoEnabled = isVideoEnabled,
                callType = callType
            )
        }
    }
}