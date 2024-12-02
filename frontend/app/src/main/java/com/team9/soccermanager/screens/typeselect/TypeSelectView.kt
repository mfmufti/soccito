package com.team9.soccermanager.screens.typeselect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*

/* Composable function for user type selection screen (Player, Coach, Admin */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeSelectView(
    switchBack: () -> Unit,
    switchToPlayer: () -> Unit,
    switchToCoach: () -> Unit,
    switchToAdmin: () -> Unit
) {
    Scaffold(
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
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Text(text = "Hiya!", fontSize = 30.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Are you a:", fontSize = 30.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = switchToPlayer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Player")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = switchToCoach,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Coach")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = switchToAdmin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Admin")
            }
        }
    }
}