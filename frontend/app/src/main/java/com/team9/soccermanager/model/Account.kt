package com.team9.soccermanager.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


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

    fun createAccount(type: String, fullname: String, email: String, password: String, then: (RegisterError) -> Unit = {}) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully created account")
                    var userProfile: User = User()
                    userProfile.fullname = fullname
                    userProfile.email = email
                    userProfile.type = type
                    Firebase.firestore.collection("users").document(auth.currentUser?.uid!!)
                        .set(userProfile)
                    Firebase.firestore.collection("users").document(auth.currentUser?.uid!!).get().addOnSuccessListener {
                        GS.user = it.toObject(User::class.java)
                        then(RegisterError.NONE)
                    }.addOnFailureListener({
                        then(RegisterError.UNKNOWN)
                    })
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

    fun joinTeam(teamId: String) {
        GS.user?.teamID = teamId
        updateRemoteUser()
    }

    fun joinLeague(leagueId: String) {
        GS.user?.leagueID = leagueId
        updateRemoteUser()
    }

    fun setupGS(then: () -> Unit) {
        Firebase.firestore.collection("users").document(auth.currentUser?.uid!!).get()
            .addOnSuccessListener {
                GS.user = it.toObject(User::class.java)
                println(GS.user?.type)
                then()
            }
            .addOnFailureListener{
            error("Failed to fetch user")
            }
    }

    fun signIn(email: String, password: String, then: (LoginError) -> Unit = {}) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Sign in successful")
                    //val user = auth.currentUser
                    Firebase.firestore.collection("users").document(auth.currentUser?.uid!!).get().addOnSuccessListener {
                        GS.user = it.toObject(User::class.java)
                        then(LoginError.NONE)
                    }.addOnFailureListener({
                        then(LoginError.UNKNOWN)
                    })
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

    private fun updateRemoteUser() {
        Firebase.firestore.collection("users").document(auth.currentUser?.uid!!).set(GS.user!!)
    }

    fun sendEmailVerification() {
        /*val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }*/
    }
}