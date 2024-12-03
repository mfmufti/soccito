package com.team9.soccermanager.screens.rankings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.team9.soccermanager.model.GS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingsView(
    switchBack: () -> Unit,
    viewModel: RankingsViewModel = remember { RankingsViewModel() }
) {
    val teams =  remember { viewModel.getList() }
    val loading = remember { viewModel.getLoading() }

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
                actions = {
                    Text(
                        text =
                        if (GS.user?.leagueName!!.length > 17) {
                            GS.user?.leagueName!!.take(17) + "...Standings"
                        } else {
                            GS.user?.leagueName + " Standings"
                        }, fontSize = 20.sp, fontWeight = FontWeight.Bold)}
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

            Spacer(modifier = Modifier.height(10.dp))

            if (loading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.width(90.dp).padding(top = 100.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            } else {
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                ) {
                    LazyColumn {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .height(40.dp)
                                    .background(MaterialTheme.colorScheme.primary),
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Pos", modifier = Modifier.width(70.dp), textAlign = TextAlign.Center, color = Color.White)
                                Text("Team", modifier = Modifier.width(180.dp), textAlign = TextAlign.Center, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("Pts", modifier = Modifier.width(70.dp), textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("GP", modifier = Modifier.width(70.dp), textAlign = TextAlign.Center, color = Color.White)
                                Text("W", modifier = Modifier.width(70.dp), textAlign = TextAlign.Center, color = Color.White)
                                Text("L", modifier = Modifier.width(70.dp), textAlign = TextAlign.Center, color = Color.White)
                                Text("D", modifier = Modifier.width(70.dp), textAlign = TextAlign.Center, color = Color.White)
                            }
                        }

                        // Team rows
                        items(teams.size) { index ->
                            val team = teams[index]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.Gray)
                                    .height(40.dp)
                                    .background(
                                        color = if (GS.user?.teamID == team.id) MaterialTheme.colorScheme.onSecondary else Color.Transparent
                                )
                                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = (index + 1).toString(), modifier = Modifier.width(70.dp), textAlign = TextAlign.Center)
                                Text(text = team.teamName, modifier = Modifier.width(180.dp), textAlign = TextAlign.Center)
                                Text(text = team.pts.toString(), modifier = Modifier.width(70.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                Text(text = team.gp.toString(), modifier = Modifier.width(70.dp), textAlign = TextAlign.Center)
                                Text(text = team.wins.toString(), modifier = Modifier.width(70.dp), textAlign = TextAlign.Center)
                                Text(text = team.losses.toString(), modifier = Modifier.width(70.dp), textAlign = TextAlign.Center)
                                Text(text = team.draws.toString(), modifier = Modifier.width(70.dp), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}