package com.team9.soccermanager.model.accessor

import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.firestore
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.NameError
import com.team9.soccermanager.model.PwdError

/*
 `UserAccessor`, provides data access methods for users in the application.
 It implements the `UserDao` interface and interacts with Firebase Authentication and Firestore
 to manage user data, including password updates and username updates.
 */

object UserAccessor : UserDao {
    override fun updateUserPwd(currPwd: String, newPwd: String, onError: (PwdError) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val email = user.email ?: onError(PwdError.NO_EMAIL)
            val credential = EmailAuthProvider.getCredential(email as String, currPwd)
            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        user.updatePassword(newPwd)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    onError(PwdError.NONE)
                                } else {
                                    val exception1 = updateTask.exception
                                    if (exception1 is FirebaseAuthWeakPasswordException) {
                                        onError(PwdError.WEAK)
                                    } else {
                                        onError(PwdError.UNKNOWN)
                                    }
                                }
                            }
                    } else {
                        val exception2 = reauthTask.exception
                        if (exception2 is FirebaseAuthInvalidCredentialsException) {
                            onError(PwdError.INCORRECT)
                        } else {
                            onError(PwdError.UNKNOWN)
                        }
                    }
                }
        } else {
            onError(PwdError.UNKNOWN)
        }
    }

    override fun updateUserName(newName: String, onError: (NameError) -> Unit) {
        val db = Firebase.firestore
        if (GS.user == null) {
            onError(NameError.UNKNOWN)
        } else {
            db.collection("users").document(GS.user!!.id)
                .update("fullname", newName)
                .addOnSuccessListener {
                    onError(NameError.NONE)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    onError(NameError.UNKNOWN)
                }
        }
    }
}