package com.team9.soccermanager.screens.login

import com.team9.soccermanager.model.Account
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.LoginError

class LoginViewModel {

    fun handleLogin(email: String, password: String, success: (String) -> Unit, failure: (String) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            failure("Please provide an email and password.")
            return
        }
        Account.signIn(email, password) { status ->
            if (status == LoginError.NONE) {
                if(GS.user == null) {
                    failure("Authentication successful, but user was not created.")
                } else {
                    success(GS.user!!.type)
                }
            } else {
                failure(when (status) {
                    LoginError.NOT_EXIST -> "No user with this email exists."
                    LoginError.BAD_CREDENTIALS -> "Invalid username or password."
                    else -> "Unknown error occurred."
                })
            }
        }
    }
}