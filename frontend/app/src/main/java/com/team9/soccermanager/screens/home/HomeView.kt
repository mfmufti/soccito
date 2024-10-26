package com.team9.soccermanager.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*

@Composable
fun HomeView() {
    var username by remember{ mutableStateOf("") }
    val viewModel = HomeViewModel()

    viewModel.getUserName { username = it }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Heya $username")
    }
}