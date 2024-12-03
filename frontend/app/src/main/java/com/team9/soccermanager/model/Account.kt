package com.team9.soccermanager.model

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

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
                    userProfile.id = auth.currentUser?.uid!!
                    Firebase.firestore.collection("users").document(auth.currentUser?.uid!!)
                        .set(userProfile).addOnSuccessListener {
                            if (type == "player") {
                                Firebase.firestore.collection("users").document(auth.currentUser?.uid!!)
                                    .update("playerAvail", PlrAvail()).addOnSuccessListener {
                                        Firebase.firestore.collection("users").document(auth.currentUser?.uid!!).get().addOnSuccessListener {
                                            GS.user = it.toObject(User::class.java)
                                            //GS.user!!.id = it.id
                                            then(RegisterError.NONE)
                                        }.addOnFailureListener({
                                            then(RegisterError.UNKNOWN)
                                        })
                                    }
                            } else {
                                Firebase.firestore.collection("users").document(auth.currentUser?.uid!!).get().addOnSuccessListener {
                                    GS.user = it.toObject(User::class.java)
                                    //GS.user!!.id = it.id
                                    then(RegisterError.NONE)
                                }.addOnFailureListener({
                                    then(RegisterError.UNKNOWN)
                                })
                            }
                        }
                        .addOnFailureListener({
                            then(RegisterError.UNKNOWN)
                            })
                } else {
                    Log.w(TAG, "Failed to create account", task.exception)
                    then(when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> RegisterError.WEAK_PASSWORD
                        is FirebaseAuthInvalidCredentialsException -> RegisterError.BAD_EMAIL
                        is FirebaseAuthUserCollisionException -> RegisterError.USER_EXISTS
                        is FirebaseNetworkException -> RegisterError.NETWORK
                        else -> RegisterError.UNKNOWN
                    })
                }
            }
    }

    fun joinTeam(teamId: String) {
        Firebase.firestore.collection("teams").document(teamId).get().addOnSuccessListener {
            GS.user?.teamID = teamId
            GS.user?.teamName = it.data?.get("name").toString()
            GS.user?.teamName?.let { it1 -> updateRemoteUser("teamName", it1) }
            updateRemoteUser("teamID", teamId)
        }
    }

    fun joinLeague(leagueId: String) {
        Firebase.firestore.collection("leagues").document(leagueId).get().addOnSuccessListener {
            GS.user?.leagueID = leagueId
            GS.user?.leagueName = it.data?.get("name").toString()
            GS.user?.leagueName?.let { it1 -> updateRemoteUser("leagueName", it1) }
            updateRemoteUser("leagueID", leagueId)
        }
    }

    fun setupGS(then: () -> Unit) {
        Firebase.firestore.collection("users").document(auth.currentUser?.uid!!).get()
            .addOnSuccessListener {
                GS.user = it.toObject(User::class.java)
                GS.user?.id = it.id
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
                        if (GS.user == null) {
                            signOut()
                            then(LoginError.BROKEN_ACCOUNT)
                        } else {
                            GS.user!!.id = it.id
                            then(LoginError.NONE)
                        }
                    }.addOnFailureListener({
                        signOut()
                        then(LoginError.UNKNOWN)
                    })
                } else {
                    Log.w(TAG, "Failed to sign in", task.exception)
                    then(when (task.exception) {
                        is FirebaseAuthInvalidUserException -> LoginError.NOT_EXIST
                        is FirebaseAuthInvalidCredentialsException -> LoginError.BAD_CREDENTIALS
                        is FirebaseNetworkException -> LoginError.NETWORK
                        else -> LoginError.UNKNOWN
                    })
                }
            }
    }

    private fun clearFCMToken() {
        if (GS.user != null) {
            Firebase.firestore.collection("users").document(GS.user!!.id).update("notificationToken", null)
                .addOnSuccessListener {
                    Firebase.auth.signOut()
                    // successful
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Firebase.auth.signOut()
                    // failure
                }
        }

    }

    fun signOut() {
        clearFCMToken()
    }

    fun updateViewAnnouncement() {
        if (GS.user != null) {
            GS.user!!.lastAnnouncementViewTime = System.currentTimeMillis()
            updateRemoteUser("lastAnnouncementViewTime", GS.user!!.lastAnnouncementViewTime)
        }
    }

    private fun updateRemoteUser(field: String, content: Any) {
        Firebase.firestore.collection("users").document(auth.currentUser?.uid!!).update(field, content)
    }

    fun setupNotifications() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            // Log token
            Log.d(TAG, "NOTIFICATION TOKEN: $token")

            GS.user?.notificationToken = token
            updateRemoteUser("notificationToken", token)

        })
    }
}