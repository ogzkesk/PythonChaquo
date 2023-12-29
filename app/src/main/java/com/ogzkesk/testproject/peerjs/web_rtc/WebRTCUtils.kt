package com.ogzkesk.testproject.peerjs.web_rtc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
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


    fun initPermissions(activity: AppCompatActivity) {
        val launcher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->

            val condition = !result.any { !it.value }
            if (condition) {
                return@registerForActivityResult
            }

            if (activity.shouldShowRequestPermissionRationale(permissions[0]) ||
                activity.shouldShowRequestPermissionRationale(permissions[1])
            ) {
                Snackbar.make(activity.window.decorView, "", Snackbar.LENGTH_LONG)
                    .setAction("OK") { openSettings(activity) }
                    .show()

            }
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
        isVideoEnabled: Boolean,
    ) {

        if (roomId == null) {
            return
        }

        when (roomType) {
            RoomType.CREATE -> {
                callJsFunction(webView, "javascript:init('$roomId')")
                callJsFunction(webView, "javascript:startCall('$roomId')")
                toggleVideo(
                    webView,
                    isVideoEnabled
                ) // TODO toggle yerine initte belirleme olması lazım
                toggleAudio(webView, isAudioEnabled)
            }

            RoomType.JOIN -> {
                callJsFunction(webView, "javascript:init('${generateRoomId()}')")
                callJsFunction(webView, "javascript:startCall('$roomId')")
                toggleVideo(webView, isVideoEnabled)
                toggleAudio(webView, isAudioEnabled)
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

    fun checkPermissions(activity: Activity): Boolean {
        return activity.checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                activity.checkSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED
    }

    private fun openSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        with(intent) {
            data = Uri.fromParts("package", activity.packageName, null)
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }

        activity.startActivity(intent)
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