package com.ogzkesk.testproject.main

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.provider.BlockedNumberContract
import android.provider.Telephony
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.TelephonyManager
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import com.ogzkesk.testproject.R
import com.ogzkesk.testproject.peerjs.PeerJsActivity
import com.ogzkesk.testproject.python.PythonActivity
import com.ogzkesk.testproject.second.SecondActivity


private const val FRAME =
    "<iframe width=\"100%\" height=\"100%\" src=\"https://vixcloud.co/embed/171886?token=863224f149595dcde0fae661712b0baf&amp;title=The+Buccaneers&amp;referer=1&amp;expires=1707510390&amp;description=S1%3AE1+Veleno+americano&amp;nextEpisode=1&amp;b=1\" title=\"\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>"
private const val FRAME2 =
    "<iframe width=\"100%\" height=\"100%\" src=\"https://streamingcommunity.broker/iframe/3367\" title=\"\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>"
private const val YOUTUBE_FRAME =
    "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/V2KCAfHjySQ?si=Y0DqeWQjwHcpAF4T\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>"
private const val CHROME_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

class MainActivity : ComponentActivity() {

    private lateinit var mWebView: WebView
    private val cm = CookieManager.getInstance()

    private var sensorManager: SensorManager? = null
    private var stepSensor : Sensor? = null
    private var initialSteps = 0f

    private val contracts = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it){
//            registerSensorCallback()
        }
    }


    private fun sensorListener() = object : SensorEventListener2 {
        override fun onFlushCompleted(p0: Sensor?) {
            println("onFlushComplated :: $p0")
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                if (event.sensor == stepSensor) {
                    val sensorStepCount = event.values?.firstOrNull()
                    sensorStepCount?.let { count ->
                        initialSteps += count
                    }
                }
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            println("onAccuracyChanged() p0 :: $p0")
            println("onAccuracyChanged() p1 :: $p1")
        }
    }

    private fun onBackPressedCallback() = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (mWebView.canGoBack()) {
                mWebView.goBack()
            }
        }
    }

    private fun registerSensorCallback() {

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if(stepSensor == null) {
            println("stepSensor is NULL")
            return
        }

        sensorManager?.registerListener(
            sensorListener(),
            stepSensor,
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM,
            SensorManager.SENSOR_DELAY_NORMAL,
        )

        println("stepSensor is registered")
    }


    @SuppressLint("SetJavaScriptEnabled", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val anim = ActivityOptionsCompat.makeBasic().toBundle()
        val intent = Intent(this,PeerJsActivity::class.java)
        startActivity(intent, anim)
        finish()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contracts.launch(android.Manifest.permission.ACTIVITY_RECOGNITION)
        }


        mWebView = findViewById(R.id.view)
        cm.acceptCookie()
        cm.setAcceptThirdPartyCookies(mWebView, true)

        mWebView.setLayerType(WebView.LAYER_TYPE_HARDWARE,null)
        mWebView.settings.apply {
            setSupportZoom(true)
            userAgentString = CHROME_AGENT
            useWideViewPort = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            builtInZoomControls = true
            javaScriptEnabled = true
            mediaPlaybackRequiresUserGesture = true
            javaScriptCanOpenWindowsAutomatically = true
            loadsImagesAutomatically = true
            allowFileAccess = true
            displayZoomControls = true
            allowContentAccess = true
            safeBrowsingEnabled = true
        }

        mWebView.webViewClient = object : WebViewClient() {

            private fun containsAnyOfHost(url: String): Boolean {
                return if (!url.contains("streamingcommunity")) {
                    println("containsAnyOfThem -- Does NOT CONTAINS streamingCommunity :: $url")
                    if (!url.contains("vixcloud.co")) {
                        println("containsAnyOfThem -- Does NOT CONTAINS vixCloud :: $url")
                        false
                    } else {
                        true
                    }
                } else {
                    true
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (containsAnyOfHost(url.toString()).not()) {
                    view?.goBack()
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (url != null) {


                    if (url.contains("/watch")) {
//                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
//                            view?.evaluateJavascript(
//                                """
//                                var secondIframe = document.querySelector("#app > div > iframe").contentDocument.querySelector("iframe");
//                                secondIframe.getAttribute("src");
//                            """.trimIndent()
//                            ) { jsResultUrl ->
//                                if (jsResultUrl == "null") return@evaluateJavascript
//                                println("resultUrl :: $jsResultUrl")
//                                val newUrl = jsResultUrl.replace("\"", "")
//                                view.loadUrl(newUrl)
//                            }
//                        }, 1000)
                    }

                    if (url.contains("vixcloud.co/embed")) {
//                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
//                            mWebView.evaluateJavascript("""
//                            window.jwplayer.defaults.autostart = true
//                            window.jwplayer.vid.autoplay = true
//                        """.trimIndent(),){
//                                println("playe basıldı mı ?:: $it")
//                                    mWebView.reload()
//                            }
//                        },2000)
                    }
                }
                super.onPageFinished(view, url)
            }
        }

        // https://vixcloud.co/embed/171886?token=59bcebe04f68749097fb63fcf210d599&title=The+Buccaneers&referer=1&expires=1707648729&description=S1%3AE1+Veleno+americano&nextEpisode=1/
        // https://streamingcommunity.broker/
        mWebView.loadUrl("https://streamingcommunity.broker/")

        onBackPressedDispatcher.addCallback(onBackPressedCallback())
    }


    override fun onDestroy() {
        sensorManager?.unregisterListener(sensorListener())
        super.onDestroy()
    }

}