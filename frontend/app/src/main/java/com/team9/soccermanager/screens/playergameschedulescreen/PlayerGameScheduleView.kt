package com.team9.soccermanager.screens.playergameschedulescreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*

@Composable
fun PlayerGameScheduleView(
    viewModel: PlayerGameScheduleViewModel = PlayerGameScheduleViewModel(),
    switchToWelcome: () -> Unit,
    goToHome: () -> Unit,
    goToRoster: () -> Unit,
    goToChat: () -> Unit
) {
    var teamName by remember { mutableStateOf("") }
    viewModel.getTeamName { teamName = it }

    Scaffold (
        topBar =  {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = teamName)
                Button(
                    onClick = { viewModel.signOut(); switchToWelcome() },
                    modifier = Modifier.size(100.dp, 36.dp),
                    contentPadding = PaddingValues(3.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )) {
                    Text(text = "Sign Out")
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = goToHome) {
                    Icon(contentDescription = "Home", imageVector = Icons.Filled.Home)
                }
                IconButton(onClick = {}) {
                    Icon(contentDescription = "Schedule", imageVector = Icons.Filled.DateRange, tint = Color.Blue)
                }
                IconButton(onClick = goToRoster) {
                    Icon(contentDescription = "Roster", imageVector = Icons.Filled.Person)
                }
                IconButton(onClick = goToChat) {
                    Icon(contentDescription = "Chat", imageVector = Icons.AutoMirrored.Default.Send)
                }
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Player Game Schedule Screen")
            }
        }
    )
}