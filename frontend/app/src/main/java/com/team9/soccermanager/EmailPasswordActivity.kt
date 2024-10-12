package com.team9.soccermanager

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class EmailPasswordActivity : Activity() {


    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    // [END declare_auth]

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]
    }

    //val db = Firebase.firestore

    init {
        auth = Firebase.auth
        db = Firebase.firestore
        //assertEquals(auth.currentUser, null)
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }
    // [END on_start_check_user]

    public fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    public fun getUserName(fcn: (String) -> Unit) {
        var name: String
        println("getUserName")
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                print("SuccessListener")
                for (document in result) {
                    if(document.id == auth.currentUser?.uid!!){
                        name = document.data.get("username").toString()
                        fcn(name)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                fcn("")
            }

    }

    public fun createAccount(baseContext: Context, username: String, email: String, password: String, fcn: (Boolean) -> Unit) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val userProfile = hashMapOf(
                        "username" to username,
                        "email" to email
                    )
                    db.collection("users").document(auth.currentUser?.uid!!)
                        .set(userProfile)
                        .addOnSuccessListener { documentReference ->
                            //    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                            Toast.makeText(baseContext, "DocumentSnapshot added with ID: ${auth.currentUser?.uid!!}", Toast.LENGTH_SHORT).show()
                        }
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
                fcn(task.isSuccessful)
            }
        // [END create_user_with_email]
    }

    public fun signIn(baseContext: Context, email: String, password: String, fcn: (Boolean) -> Unit) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext,
                        "We are In",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
                fcn(task.isSuccessful)
            }
        // [END sign_in_with_email]
    }

    public fun signOut() {
        Firebase.auth.signOut()
    }

    public fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
        // [END send_email_verification]
    }

    private fun updateUI(user: FirebaseUser?) {
    }

    private fun reload() {
    }

    public override fun onDestroy() {
        super.onDestroy()
        signOut()
    }

    companion object {
        var instance : EmailPasswordActivity? = null;

        public fun get() : EmailPasswordActivity {
            if (instance == null) {
                instance = EmailPasswordActivity();
            }
            return instance!!;
        }
        private const val TAG = "EmailPassword"
    }

}
