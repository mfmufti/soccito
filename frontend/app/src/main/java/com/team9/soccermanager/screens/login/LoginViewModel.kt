package com.team9.soccermanager.screens.login

import android.widget.Toast
import com.team9.soccermanager.MainActivity
import com.team9.soccermanager.model.Account

class LoginViewModel {
    private var account = Account()

    fun handleLogin(email: String, password: String, switchToWelcome: () -> Unit) {// Replace with meaningful form validation
        // Replace with comprehensive form validation
        if (email.isEmpty() || password.isEmpty()) {
            return
        }
        account.signIn(email, password) { success ->
            if (success) {
                Toast.makeText(
                    MainActivity.context,
                    "Authentication successful",
                    Toast.LENGTH_SHORT,
                ).show()
                switchToWelcome()
            } else {
                Toast.makeText(
                    MainActivity.context,
                    "Authentication failed",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}