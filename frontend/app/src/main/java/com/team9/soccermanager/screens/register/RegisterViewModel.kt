package com.team9.soccermanager.screens.register

import com.team9.soccermanager.model.Account
import com.team9.soccermanager.model.RegisterError

class RegisterViewModel {

    fun handleRegister(username: String, email: String, password: String, success: () -> Unit, failure: (String) -> Unit) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            failure("Please fill all fields.")
            return
        }
        Account.createAccount(username, email, password) { status ->
            if (status == RegisterError.NONE) {
                success()
            } else {
                failure(when (status) {
                    RegisterError.BAD_EMAIL -> "Bad email provided."
                    RegisterError.USER_EXISTS -> "A user with this email already exists."
                    RegisterError.WEAK_PASSWORD -> "The password provided is weak."
                    else -> "Unknown error occurred."
                })
            }
        }
    }
}