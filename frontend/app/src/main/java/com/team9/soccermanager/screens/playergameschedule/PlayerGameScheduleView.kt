package com.team9.soccermanager.screens.playergameschedule

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
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
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.ui.composable.BarsWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerGameScheduleView(
    viewModel: PlayerGameScheduleViewModel = PlayerGameScheduleViewModel(),
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
    goToSpecificGame: () -> Unit,
) {
//    var teamName by remember { mutableStateOf("") }
//    viewModel.getTeamName { teamName = it }

    BarsWrapper(
        title = "Game Schedule",
        activeScreen = MainScreens.SCHEDULE,
        signOut = { viewModel.signOut(); switchToWelcome() },
        switchMainScreen = switchMainScreen,
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var state by remember { mutableIntStateOf(0) }
                val titles = listOf("Upcoming", "Completed")

                SecondaryTabRow(selectedTabIndex = state) {
                    titles.forEachIndexed { index, title ->
                        Tab(selected = index == state, onClick = { state = index }) {
                            Column(
                                Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    data class Game(val home: String, val away: String)

                    val data = listOf(
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
    }
}

