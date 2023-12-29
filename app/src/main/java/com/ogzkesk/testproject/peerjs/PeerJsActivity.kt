package com.ogzkesk.testproject.peerjs

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.ogzkesk.testproject.R
import com.ogzkesk.testproject.databinding.ActivityPeerJsBinding
import com.ogzkesk.testproject.peerjs.web_rtc.CallType
import com.ogzkesk.testproject.peerjs.web_rtc.WebRTC

class PeerJsActivity : AppCompatActivity() {

    private lateinit var clipboardManager: ClipboardManager
    private lateinit var binding: ActivityPeerJsBinding
    private lateinit var webRTC: WebRTC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeerJsBinding.inflate(layoutInflater)
        clipboardManager = getSystemService(ClipboardManager::class.java)
        setContentView(binding.root)
        initWebRTC()
        initUI()
    }


    private fun initUI() = with(binding) {
        layoutCall.run {

            btnCopy.setOnClickListener {
                val clip = ClipData.newPlainText("room-id",webRTC.getRoomId())
                clipboardManager.setPrimaryClip(clip)
            }

            btnFinishCall.setOnClickListener {
                closeCallLayout()
//                webRTC.release()
            }

            btnToggleAudio.setOnClickListener {
                toggleMicDrawable(!webRTC.isAudioEnabled())
                webRTC.toggleAudio()
            }

            btnToggleVideo.setOnClickListener {
                toggleVideoDrawable(!webRTC.isVideoEnabled())
                webRTC.toggleVideo()
            }
        }

        layoutRoom.run {
            btnJoin.setOnClickListener {
                webRTC.joinRoom(etRoomNo.text.toString())
                openCallLayout()
            }

            btnCreateVideoCall.setOnClickListener {
                webRTC.createRoom()
                openCallLayout()
            }

            btnCreatePhoneCall.setOnClickListener {
                // passive
            }
        }
    }

    private fun initWebRTC() {
        webRTC = WebRTC.Builder(this)
            .setWebView(binding.layoutCall.webView)
            .setCallType(CallType.VIDEO)
            .requestPermissions()
            .build()
    }

    private fun openCallLayout() {
        binding.layoutCall.root.isVisible = true
        binding.layoutRoom.root.isVisible = false
        binding.layoutCall.tvRoomId.text = getString(R.string.roomId,webRTC.getRoomId())
    }

    private fun closeCallLayout() {
        binding.layoutCall.root.isVisible = false
        binding.layoutRoom.root.isVisible = true
    }

    private fun toggleMicDrawable(b: Boolean){
        binding.layoutCall.btnToggleAudio.run {
            if(b) {
                (this as MaterialButton).setIconResource(R.drawable.ic_mic_on)
            } else {
                (this as MaterialButton).setIconResource(R.drawable.ic_mic_off)
            }
        }
    }

    private fun toggleVideoDrawable(b: Boolean){
        binding.layoutCall.btnToggleVideo.run {
            if(b) {
                (this as MaterialButton).setIconResource(R.drawable.ic_video)
            } else {
                (this as MaterialButton).setIconResource(R.drawable.ic_video_off)
            }
        }
    }
}