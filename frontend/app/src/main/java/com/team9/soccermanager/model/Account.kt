package com.team9.soccermanager.model

import android.util.Log
import androidx.compose.runtime.Composable
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Account {
    private val TAG = "Model"
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUserName(then: (String) -> Unit) {
        var name: String
        db.collection("users")
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

    fun createAccount(username: String, email: String, password: String, then: (Boolean) -> Unit = {}) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully created account")
                    val userProfile = hashMapOf(
                        "username" to username,
                        "email" to email
                    )
                    db.collection("users").document(auth.currentUser?.uid!!)
                        .set(userProfile)
                } else {
                    Log.w(TAG, "Failed to create account", task.exception)
                }
                then(task.isSuccessful)
            }
    }

    fun signIn(email: String, password: String, then: (Boolean) -> Unit = {}) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    //val user = auth.currentUser
                    // notify of success
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    // notify of failure
                }
                then(task.isSuccessful)
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