package com.team9.soccermanager.model

import android.util.Log
import androidx.compose.runtime.Composable
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

enum class RegisterError {
    NONE, WEAK_PASSWORD, BAD_EMAIL, USER_EXISTS, UNKNOWN
}

enum class LoginError {
    NONE, NOT_EXIST, BAD_CREDENTIALS, UNKNOWN
}

object Account {
    private val TAG = "Model"
    private var auth: FirebaseAuth = Firebase.auth

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUserName(then: (String) -> Unit) {
        var name: String
        Firebase.firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                print("SuccessListener")
                for (document in result) {
                    if(document.id == auth.currentUser?.uid!!){
                        name = document.data.get("username").toString()
                        then(name)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents", exception)
                then("")
            }
    }

    fun createAccount(username: String, email: String, password: String, then: (RegisterError) -> Unit = {}) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully created account")
                    val userProfile = hashMapOf(
                        "username" to username,
                        "email" to email
                    )
                    Firebase.firestore.collection("users").document(auth.currentUser?.uid!!)
                        .set(userProfile)
                    then(RegisterError.NONE)
                } else {
                    Log.w(TAG, "Failed to create account", task.exception)
                    then(when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> RegisterError.WEAK_PASSWORD
                        is FirebaseAuthInvalidCredentialsException -> RegisterError.BAD_EMAIL
                        is FirebaseAuthUserCollisionException -> RegisterError.USER_EXISTS
                        else -> RegisterError.UNKNOWN
                    })
                }
            }
    }

    fun signIn(email: String, password: String, then: (LoginError) -> Unit = {}) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Sign in successful")
                    //val user = auth.currentUser
                    then(LoginError.NONE)
                } else {
                    Log.w(TAG, "Failed to sign in", task.exception)
                    then(when (task.exception) {
                        is FirebaseAuthInvalidUserException -> LoginError.NOT_EXIST
                        is FirebaseAuthInvalidCredentialsException -> LoginError.BAD_CREDENTIALS
                        else -> LoginError.UNKNOWN
                    })
                }
            }
    }

    fun signOut() {
        Firebase.auth.signOut()
    }

    fun sendEmailVerification() {
        /*val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }*/
    }
}