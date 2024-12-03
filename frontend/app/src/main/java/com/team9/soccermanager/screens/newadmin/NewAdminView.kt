package com.team9.soccermanager.screens.newadmin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

/*
 Composable function for the new admin screen .
 It displays a form for entering a league name to create a new league.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAdminView(
    switchToRegister: (String) -> Unit,
    switchBack: () -> Unit,
    viewModel: NewAdminViewModel = remember { NewAdminViewModel() }
) {
    var league by remember { viewModel.getLeague() }
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
            Text(text = "Welcome Admin!", fontSize = 30.sp)
            Text(text = "Create a League", fontSize = 30.sp)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = league,
                onValueChange = { league = it },
                label = { Text("League Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(error, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.checkLeague(switchToRegister) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create")
            }
        }
    }
}