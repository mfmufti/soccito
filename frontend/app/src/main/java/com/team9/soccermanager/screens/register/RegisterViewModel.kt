package com.team9.soccermanager.screens.register

import android.widget.Toast
import com.team9.soccermanager.MainActivity
import com.team9.soccermanager.model.Account

class RegisterViewModel {
    private var account = Account()

    fun handleRegister(username: String, email: String, password: String, switchToWelcome: () -> Unit) {
        // Replace with comprehensive form validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return
        }
        account.createAccount(username, email, password) { success ->
            if (success) {
                Toast.makeText(
                    MainActivity.context,
                    "Account successfully created",
                    Toast.LENGTH_SHORT,
                ).show()
                switchToWelcome()
            } else {
                Toast.makeText(
                    MainActivity.context,
                    "Account creation failed",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}