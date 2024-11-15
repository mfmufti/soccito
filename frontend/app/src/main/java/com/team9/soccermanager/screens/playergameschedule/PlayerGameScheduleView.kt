package com.team9.soccermanager.screens.playergameschedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

@Composable
fun PlayerGameScheduleView(
    viewModel: PlayerGameScheduleViewModel = PlayerGameScheduleViewModel(),
    switchToWelcome: () -> Unit,
    goToSpecificGame: () -> Unit,
    goToHome: () -> Unit,
    goToRoster: () -> Unit,
    goToChatSelect: () -> Unit
    ) {
        var teamName by remember { mutableStateOf("") }
        viewModel.getTeamName { teamName = it }
    var isMapLoaded by remember { mutableStateOf(false) }

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
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )) {
                    Text(text = "Sign Out")
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 0.dp, 8.dp, 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = goToHome) {
                    Icon(contentDescription = "Home", imageVector = Icons.Filled.Home)
                }
                IconButton(onClick = {}) {
                    Icon(contentDescription = "Schedule", imageVector = Icons.Filled.DateRange, tint = MaterialTheme.colorScheme.surfaceTint)
                }
                IconButton(onClick = goToRoster) {
                    Icon(contentDescription = "Roster", imageVector = Icons.Filled.Person)
                }
                IconButton(onClick = goToChatSelect) {
                    Icon(contentDescription = "Chat", imageVector = Icons.Filled.Forum)
                }
            }
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    data class Game(val home: String, val away: String)

                    var data = listOf(
                        Game("Leverkusen", "Bayern"),
                        Game("Chelsea", "Leverkusen"),
                        Game("Leverkusen", "Berlin"),
                        Game("Leverkusen", "Dortmund"),
                        Game("PSG", "Leverkusen"),
                        Game("Leverkusen", "PSG"),
                        Game("Leverkusen", "Liepzig"),
                        Game("Leverkusen", "Augsburg")
                    )

                    for (game in data) {
                        Button(
                            onClick = { goToSpecificGame() },
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = game.home + " vs. " + game.away,
                                    fontSize = 24.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    )
}

