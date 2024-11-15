package com.team9.soccermanager.screens.rankings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingsView(
    switchBack: () -> Unit,
    viewModel: RankingsViewModel = RankingsViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.getScreenData()
    }

    var currViewModel by remember { mutableStateOf(viewModel) }

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
                },
                actions = {Text(text = currViewModel.leagueName + " Standings", fontSize = 20.sp, fontWeight = FontWeight.Bold)}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text("Pos", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.White)
                Text("Team", modifier = Modifier.weight(2f), textAlign = TextAlign.Center, color = Color.White)
                Text("GP", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.White)
                Text("W", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.White)
                Text("L", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.White)
                Text("D", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.White)
                Text("Pts", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.White)
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(currViewModel.teamsList.size) { index ->
                    val team = currViewModel.teamsList[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .height(45.dp)
                            .background(
                                color = if (team?.id == currViewModel.teamId) Color.Yellow else Color.Transparent
                            )
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text((index + 1).toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text(team?.name ?: "", modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
                        Text(team?.gamesPlayed.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text(team?.wins.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text(team?.losses.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text(team?.draws.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text(team?.points.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}