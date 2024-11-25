package com.team9.soccermanager.screens.playerroster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.ui.composable.BarsWrapper

@Composable
fun PlayerRosterView(
    viewModel: PlayerRosterViewModel = remember { PlayerRosterViewModel() },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
) {

    val availList = remember { viewModel.getAvailList() }
    val loading = remember { viewModel.getLoading() }
    val error = remember { viewModel.getError() }

    BarsWrapper(
        title = "Roster",
        activeScreen = MainScreens.ROSTER,
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
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (error.value.isNotEmpty()) {
                    Text(
                        text = error.value,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (loading.value) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(60.dp))
                    }
                } else {
                    if (availList.size == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 150.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No players in the team currently...",
                                fontStyle = FontStyle.Italic
                            )
                        }

                    }
                    for (index in 0..<availList.size) {
                        val playeravail = availList[index]
                        Box(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                                .background(if (playeravail.playerId != GS.user!!.id) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.secondaryContainer)
                                .padding(15.dp)
                                .fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = playeravail.playerName,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(11.dp))
                                Text(
                                    text = playeravail.playerAvail.avail.toString(),
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic
                                )
                                if (playeravail.playerAvail.reason.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = playeravail.playerAvail.reason,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}