package com.team9.soccermanager.screens.newcoach

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*

@Composable
fun NewCoachView(
    switchToHome: () -> Unit,
    viewModel: NewCoachViewModel = NewCoachViewModel()
) {
    var league by remember { mutableStateOf("") }
    var team by remember { mutableStateOf("") }

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
            Text(text = "Welcome Coach!", fontSize = 30.sp)
            Text(text = "Create a Team", fontSize = 30.sp)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = league,
                onValueChange = { league = it },
                label = { Text("League Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = team,
                onValueChange = { team = it },
                label = { Text("Team Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.createTeam(league, team, onSuccess = switchToHome)},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create")
            }
        }
    }
}