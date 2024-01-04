package com.ogzkesk.testproject.peerjs.user

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MGoogleSignIn(private val activity: ComponentActivity) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var onSuccess: (suspend (FirebaseUser?) -> Unit)? = null
    private var onError: ((String?) -> Unit)? = null
    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    private val signInListener = fun(result: ActivityResult) {

        activity.lifecycleScope.launch {
            activity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                try {

                    val user = GoogleSignIn.getSignedInAccountFromIntent(result.data).await()
                    val credential = GoogleAuthProvider.getCredential(user.idToken, null)
                    val authResult = auth.signInWithCredential(credential).await()
                    onSuccess?.invoke(authResult.user)

                } catch (e: Exception) {
                    println("signInListener exception :: ${e.message}")
                    onError?.invoke(e.message)
                    e.printStackTrace()
                }
            }
        }
    }

    fun onSuccess(block: suspend (FirebaseUser?) -> Unit) = apply {
        this.onSuccess = block
    }

    fun onError(block: (String?) -> Unit) = apply {
        this.onError = block
    }

    fun init() = apply {
        resultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            signInListener
        )
    }

    fun launch() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("408547432219-tstk4si4q0r694vvuip0ttmhg9kl1v96.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .requestId()
            .build()

        val signInIntent = GoogleSignIn.getClient(activity, gso).signInIntent
        resultLauncher?.launch(signInIntent)
    }
}