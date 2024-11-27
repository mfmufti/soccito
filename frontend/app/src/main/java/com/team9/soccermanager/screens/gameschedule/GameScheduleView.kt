package com.team9.soccermanager.screens.gameschedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.model.accessor.Game
import com.team9.soccermanager.ui.composable.BarsWrapper
import java.text.DateFormat.getDateTimeInstance
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScheduleView(
    viewModel: GameScheduleViewModel = remember { GameScheduleViewModel() },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
    goToSpecificGame: (Int) -> Unit,
    goToGameAdd: () -> Unit,
) {
    val completedGames = remember { viewModel.getCompletedGames() }
    val upcomingGames = remember { viewModel.getUpcomingGames() }
    val error by remember { viewModel.getError() }
    val loading by remember { viewModel.getLoading() }
    var tabIndex by remember { mutableIntStateOf(0) }
    val titles = listOf("Upcoming", "Completed")

    BarsWrapper(
        title = "Game Schedule",
        activeScreen = MainScreens.SCHEDULE,
        signOut = { viewModel.signOut(); switchToWelcome() },
        switchMainScreen = switchMainScreen,
    ) { paddingValues ->
        Box(
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
                SecondaryTabRow(selectedTabIndex = tabIndex) {
                    titles.forEachIndexed { index, title ->
                        Tab(selected = index == tabIndex, onClick = { tabIndex = index }) {
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
                    // Delete when more games are added to database...
//                    data class Game(val home: String, val away: String)
//
//                    val data = listOf(
//                        Game("Leverkusen", "Bayern"),
//                        Game("Chelsea", "Leverkusen"),
//                        Game("Leverkusen", "Berlin"),
//                        Game("Leverkusen", "Dortmund"),
//                        Game("PSG", "Leverkusen"),
//                        Game("Leverkusen", "PSG"),
//                        Game("Leverkusen", "Liepzig"),
//                        Game("Leverkusen", "Augsburg")
//                    )
                    if (error.isNotEmpty()) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    } else if (loading) {
                        Box(modifier = Modifier.padding(20.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(60.dp))
                        }
                    } else {
                        val games = if (tabIndex == 0) upcomingGames else completedGames
                        if (games.isEmpty()) {
                            Text(
                                text = "No games to display",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                            )
                        } else {
                            GameList(games, goToSpecificGame)
                        }
                    }
                }
            }

            if (GS.user!!.type == "admin") {
                SmallFloatingActionButton(
                    onClick = goToGameAdd,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Align it to the bottom-end (right bottom corner)
                        .padding(8.dp)
                        .size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add a game",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GameList(games: List<Game>, goToSpecificGame: (Int) -> Unit) {
    for (game in games) {
        Button(
            onClick = { goToSpecificGame(game.id) },
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .height(100.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = game.team1Name + " vs. " + game.team2Name,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = SimpleDateFormat("MMM d, y 'at' hh:mm a zzz", Locale.US).format(game.timestamp.toDate()),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}