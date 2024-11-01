package com.team9.soccermanager.screens.newplayer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*

@Composable
fun NewPlayerView(
    switchToHome: () -> Unit,
    viewModel: NewPlayerViewModel = NewPlayerViewModel()
) {
    var team by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Scaffold (
        modifier = Modifier
            .padding(16.dp)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome Player!", fontSize = 30.sp)
            Text(text = "Join a Team", fontSize = 30.sp)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = team,
                onValueChange = { team = it },
                label = { Text("Team Name") },
                modifier = Modifier.fillMaxWidth()
            )

            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.joinTeam(team, onSuccess = switchToHome, onError = { error = it }) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Join")
            }
        }
    }
}