package com.ogzkesk.testproject.peerjs

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.ogzkesk.testproject.R
import com.ogzkesk.testproject.databinding.ActivityPeerJsBinding
import com.ogzkesk.testproject.peerjs.user.User
import com.ogzkesk.testproject.peerjs.user.UserRepository
import com.ogzkesk.testproject.peerjs.web_rtc.CallType
import com.ogzkesk.testproject.peerjs.web_rtc.WebRTC
import com.ogzkesk.testproject.showToast
import kotlinx.coroutines.launch

class PeerJsActivity : AppCompatActivity() {

    private lateinit var clipboardManager: ClipboardManager
    private lateinit var binding: ActivityPeerJsBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var userRepository: UserRepository
    private lateinit var webRTC: WebRTC
    private lateinit var auth: FirebaseAuth
    private var userInCall: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPeerJsBinding.inflate(layoutInflater)
        clipboardManager = getSystemService(ClipboardManager::class.java)
        auth = FirebaseAuth.getInstance()
        userAdapter = UserAdapter()
        userRepository = UserRepository(this, lifecycleScope)

        setContentView(binding.root)
        initWebRTC()
        initUI()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    observeUsers()
                }
                launch {
                    observeCall()
                }
            }
        }
    }

//    private fun initUsers(){
//        userRepository.getAllUsers(lifecycleScope){
//            println("initUsers() :: $it")
//            userAdapter.submitList(it)
//        }
//    }

    private fun observeCall() {
        userRepository.observeCalls { call ->
            AlertDialog.Builder(this@PeerJsActivity)
                .setTitle("Incoming Call from ${call.from}")
                .setNegativeButton("Reject") { v, _ -> v.dismiss() }
                .setPositiveButton("Join") { v, _ ->
                    try {
                        v.dismiss()
                        webRTC.joinRoom(call.id)
                        openCallLayout()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .create()
                .show()
        }
    }


    private fun observeUsers() {
        userRepository.observeUsers { users, error ->
            println("Activity users :: $users")
            userAdapter.submitList(users)
            if (error != null) {
                showToast(error)
            }
        }
    }


    private fun initUI() = with(binding) {

        layoutLogin.run {
            root.isVisible = auth.currentUser == null
            userRepository.initGoogleSigning()

            btnSignin.setOnClickListener {
                lifecycleScope.launch {

                    userRepository.login { error ->
                        if (error != null) {
                            println("login() error: $error")
                            showToast(error)
                            return@login
                        }

                        openUsersLayout()
                    }
                }
            }
        }

        layoutUsers.run {
            root.isVisible = auth.currentUser != null
            rvUsers.adapter = userAdapter
            rvUsers.layoutManager = LinearLayoutManager(this@PeerJsActivity)
            rvUsers.setHasFixedSize(true)
            userAdapter.setOnClickListener(::startCall)
//            initUsers()
        }


        layoutCall.run {

            btnCopy.setOnClickListener {
                val clip = ClipData.newPlainText("room-id", webRTC.getRoomId())
                clipboardManager.setPrimaryClip(clip)
            }

            btnFinishCall.setOnClickListener {
                closeCallLayout()
                removeCall()
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
    }


    private fun startCall(remoteUser: User) = lifecycleScope.launch {
        if (auth.currentUser?.uid != null) {

            userInCall = remoteUser
            webRTC.createRoom()
            val currentRoomId = webRTC.getRoomId()
            if (currentRoomId == null) {
                showToast("RoomId not initialized")
                return@launch
            }

            userRepository.createCall(
                to = remoteUser.id,
                roomId = currentRoomId
            )

            openCallLayout()
        }
    }

    private fun removeCall() = lifecycleScope.launch {
        val currentRoomId = webRTC.getRoomId()
        if (currentRoomId == null) {
            showToast("No roomId for close call")
            return@launch
        }
        userRepository.closeCall(currentRoomId)
        webRTC.exitRoom()
    }

    private fun initWebRTC() {
        webRTC = WebRTC.Builder(this)
            .setWebView(binding.layoutCall.webView)
            .setCallType(CallType.VIDEO)
            .setVideoDisabled()
            .requestPermissions()
            .build()
    }

    private fun openCallLayout() {
        binding.layoutCall.root.isVisible = true
        binding.layoutUsers.root.isVisible = false
        binding.layoutLogin.root.isVisible = false
        binding.layoutCall.tvRoomId.text = getString(R.string.roomId, webRTC.getRoomId())
    }

    private fun closeCallLayout() {
        binding.layoutCall.root.isVisible = false
        binding.layoutUsers.root.isVisible = true
    }

    private fun openUsersLayout() {
        println("openUsersLayout()")
        binding.layoutUsers.root.isVisible = true
        binding.layoutLogin.root.isVisible = false
        binding.layoutCall.root.isVisible = false
    }

    private fun toggleMicDrawable(b: Boolean) {
        binding.layoutCall.btnToggleAudio.run {
            if (b) {
                (this as MaterialButton).setIconResource(R.drawable.ic_mic_on)
            } else {
                (this as MaterialButton).setIconResource(R.drawable.ic_mic_off)
            }
        }
    }

    private fun toggleVideoDrawable(b: Boolean) {
        binding.layoutCall.btnToggleVideo.run {
            if (b) {
                (this as MaterialButton).setIconResource(R.drawable.ic_video)
            } else {
                (this as MaterialButton).setIconResource(R.drawable.ic_video_off)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.layoutCall.root.isVisible) {
            closeCallLayout()
        } else {
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
        userRepository.removeListeners()
        FirebaseAuth.getInstance().signOut()
        super.onDestroy()
    }
}