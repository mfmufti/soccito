package com.team9.soccermanager.screens.newadmin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*

@Composable
fun NewAdminView(
    switchToHome: () -> Unit,
    viewModel: NewAdminViewModel = NewAdminViewModel()
) {
    var league by remember { mutableStateOf("") }

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
            Text(text = "Welcome Admin!", fontSize = 30.sp)
            Text(text = "Create a League", fontSize = 30.sp)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = league,
                onValueChange = { league = it },
                label = { Text("League Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.createLeague(league, onSuccess = switchToHome) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create")
            }
        }
    }
}