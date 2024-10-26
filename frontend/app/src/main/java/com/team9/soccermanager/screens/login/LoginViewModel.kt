package com.team9.soccermanager.screens.login

import com.team9.soccermanager.model.Account
import com.team9.soccermanager.model.LoginError

class LoginViewModel {
    private var account = Account()

    fun handleLogin(email: String, password: String, success: () -> Unit, failure: (String) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            failure("Please provide an email and password.")
            return
        }
        account.signIn(email, password) { status ->
            if (status == LoginError.NONE) {
                success()
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