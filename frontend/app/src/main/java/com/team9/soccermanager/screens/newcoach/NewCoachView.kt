package com.team9.soccermanager.screens.newcoach

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*

/*
 Composable function for the new coach screen.
 It displays a form for entering a league code and team name to create a new team.
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCoachView(
    switchToRegister: (String, String) -> Unit,
    switchBack: () -> Unit,
    viewModel: NewCoachViewModel = remember { NewCoachViewModel() }
) {
    var league by remember { viewModel.getLeagueCode() }
    var team by remember { viewModel.getTeam() }
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
            Text(text = "Welcome Coach!", fontSize = 30.sp)
            Text(text = "Create a Team", fontSize = 30.sp)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = league,
                onValueChange = { league = it },
                label = { Text("League Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = team,
                onValueChange = { team = it },
                label = { Text("Team Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.checkTeam(switchToRegister) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create")
            }
        }
    }
}