package com.team9.soccermanager.screens.newplayer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*

/*
 Composable function for the new player screen.
 It displays a form (textbox) for entering a team code and joining a team.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPlayerView(
    switchToRegister: (String) -> Unit,
    switchBack: () -> Unit,
    viewModel: NewPlayerViewModel = remember { NewPlayerViewModel() }
) {
    var team by remember { viewModel.getTeamCode() }
    val error by remember { viewModel.getError() }

    Scaffold (
        modifier = Modifier.padding(16.dp),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = switchBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back",
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome Player!", fontSize = 30.sp)
            Text(text = "Join a Team", fontSize = 30.sp)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = team,
                onValueChange = { team = it },
                label = { Text("Team Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.checkTeamCode(switchToRegister) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Join")
            }
        }
    }
}