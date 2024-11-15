package com.team9.soccermanager.screens.playerroster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*

@Composable
fun PlayerRosterView(
    viewModel: PlayerRosterViewModel = PlayerRosterViewModel(),
    switchToWelcome: () -> Unit,
    goToHome: () -> Unit,
    goToSchedule: () -> Unit,
    goToChatSelect: () -> Unit
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
                IconButton(onClick = goToSchedule) {
                    Icon(contentDescription = "Schedule", imageVector = Icons.Filled.DateRange)
                }
                IconButton(onClick = {}) {
                    Icon(contentDescription = "Roster", imageVector = Icons.Filled.Person, tint = MaterialTheme.colorScheme.surfaceTint)
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
                    data class Player(val name: String, val email: String)

                    var data = listOf(
                        Player("First", "first@firsty.com"),
                        Player("First", "first@firsty.com"),
                        Player("First", "first@firsty.com"),
                        Player("First", "first@firsty.com"),
                        Player("First", "first@firsty.com"),
                        Player("First", "first@firsty.com"),
                        Player("First", "first@firsty.com"),
                        Player("First", "first@firsty.com"),
                        Player("First", "first@firsty.com"),
                        Player("First", "first@firsty.com"),
                        Player("Another", "justanother@x.com")
                    )

                    for (player in data) {
                        Box(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                                .background(MaterialTheme.colorScheme.inverseOnSurface)
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = player.name,
                                    fontSize = 30.sp
                                )
                                Text(
                                    text = player.email,
                                    fontSize = 12.sp
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