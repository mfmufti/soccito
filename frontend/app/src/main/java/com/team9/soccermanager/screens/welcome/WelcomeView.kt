package com.team9.soccermanager.screens.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*

@Composable
fun WelcomeView() {
    var username by remember{ mutableStateOf("") }
    val viewModel = WelcomeViewModel()

    viewModel.getUserName { username = it }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Heya $username")
    }
}