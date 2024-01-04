package com.ogzkesk.testproject.peerjs.user

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val activity: AppCompatActivity,
    private val scope: CoroutineScope,
) {

    private lateinit var googleSigning: MGoogleSignIn
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private var userCollectionListener: ListenerRegistration? = null
    private var callCollectionListener: ListenerRegistration? = null

    private val userCollection: CollectionReference
        get() = firestore.collection("users")
    private val callCollection: CollectionReference
        get() = firestore.collection("calls")

    fun initGoogleSigning() {
        googleSigning = MGoogleSignIn(activity).init()
    }


    suspend fun login(onComplete: (error: String?) -> Unit) {
        googleSigning.onError(onComplete)
            .onSuccess { user ->

                if (user == null) {
                    onComplete("AuthUser == null")
                    return@onSuccess
                }

                if (setUserOnFirestore(user)) {
                    println("setUserOnFirestore() true ???????")
                    onComplete(null)
                }
            }
            .launch()
    }

    private suspend fun setUserOnFirestore(firebaseUser: FirebaseUser): Boolean {

        println("setUserOnFirestore() currentUser :: $firebaseUser")

        val user = User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            imgUrl = firebaseUser.photoUrl.toString(),
            displayName = firebaseUser.displayName ?: "",
            followerIds = emptyList(),
            follows = emptyList(),
        )

        return if (checkUserExists(user).not()) {
            println("checkUserExists() == false")
            val state = setNewUser(user)
            println("checkUserExists() == $state")
            state
        } else {
            println("checkUserExists() == true")
            true
        }
    }


    private suspend fun setNewUser(user: User): Boolean {
        return try {
            if(user.id == null) {
                false
            } else {
                userCollection.document(user.id).set(user).await()
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun checkUserExists(user: User): Boolean {
        println("checkUserExists")
        return try {

            if(user.id == null) {
                return false
            }

            val storedUser = userCollection.document(user.id)
                .get()
                .await()
                .toObject(User::class.java)

            storedUser != null

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    fun getAllUsers(scope: CoroutineScope,onResult: (List<User>) -> Unit){
        try {
            scope.launch {
                val users = userCollection.get().await().toObjects(User::class.java)
                onResult(users)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getUserById(id: String): User? {
        return try {
            userCollection.document(id).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createCall(to: String, roomId: String) {
        try {
            val from = auth.currentUser!!.uid
            val call = Call(from = from, to = to, id = roomId)
            callCollection.document(roomId).set(call).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun closeCall(roomId: String) {
        try {
            callCollection.document(roomId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun observeUsers(onComplete: (users: List<User>, error: String?) -> Unit) {
        userCollectionListener = userCollection.addSnapshotListener { value, error ->
            println("observeUsers().addSnapshotListener")
            if (error != null) {
                onComplete(emptyList(), error.message)
                return@addSnapshotListener
            }

            if (value != null && value.isEmpty.not()) {
                value.forEach {
                    println("#value :::::: $it")
                }
                val users = value.map { it.toObject(User::class.java) }
                val item = value.map { it.get("deneme") }
                println("users :: $users")
                println("item ::::::::: $item")
                onComplete(users, null)
            }
        }
    }


    fun observeCalls(onIncomingCall: (Call) -> Unit) {
        val id = auth.currentUser?.uid ?: return

        callCollectionListener = callCollection.whereEqualTo("to", id).addSnapshotListener { value, error ->
            println("observeCalls().addSnapshotListener")
            if (error != null) return@addSnapshotListener
            if (value == null) return@addSnapshotListener
            value.map { it.toObject(Call::class.java) }.firstOrNull()?.let {
                onIncomingCall(it)
            }
        }
    }

    fun removeListeners(){
        userCollectionListener?.remove()
        callCollectionListener?.remove()
        userCollectionListener = null
        callCollectionListener = null
    }
}