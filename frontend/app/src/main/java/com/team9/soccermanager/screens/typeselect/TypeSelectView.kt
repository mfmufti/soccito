package com.team9.soccermanager.screens.typeselect

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable
fun TypeSelectView(
    switchBack: () -> Unit,
    switchToPlayer: () -> Unit,
    switchToCoach: () -> Unit,
    switchToAdmin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
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