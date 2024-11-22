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
import androidx.compose.ui.unit.*
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.ui.composable.BarsWrapper

@Composable
fun PlayerRosterView(
    viewModel: PlayerRosterViewModel = PlayerRosterViewModel(),
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
) {
    var teamName by remember { mutableStateOf("") }
    viewModel.getTeamName { teamName = it }

    val availabilityList = remember { viewModel.getPlayerAvailabilityList() }
    val loading by remember { viewModel.isLoading() }

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
                if (loading) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(60.dp))
                    }
                } else {
                    for (index in 0..<availabilityList.size) {
                        val playeravail = availabilityList[index]
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
                                    text = playeravail.name,
                                    fontSize = 30.sp
                                )
                                Text(
                                    text = playeravail.availability,
                                    fontSize = 12.sp
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