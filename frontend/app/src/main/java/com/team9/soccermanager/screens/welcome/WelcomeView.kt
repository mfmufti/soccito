package com.team9.soccermanager.screens.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*

/* Composable function for the welcome screen, login + resigter buttons */

@Composable
fun WelcomeView(
    switchToLogin: () -> Unit,
    switchToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Soccito!",
            fontSize = 26.sp
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = switchToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = switchToRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}