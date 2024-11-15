package com.team9.soccermanager.screens.playerHomeScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Announcement
import java.text.DateFormat.getDateTimeInstance
import java.util.Date

@Composable
fun PlayerHomeScreenView(
    viewModel: PlayerHomeScreenViewModel = PlayerHomeScreenViewModel(),
    switchToWelcome: () -> Unit,
    goToLeagueStandings: () -> Unit,
    goToSchedule: () -> Unit,
    goToRoster: () -> Unit,
    goToChatSelect: () -> Unit
) {
    var teamName by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var announcements by remember { mutableStateOf<List<Announcement>?>(null) }
    var joinCode by remember { mutableStateOf("") }

    viewModel.getTeam {
        announcements = it.announcements.toList()
    }

    //viewModel.getTeamName { teamName = it }
    viewModel.getFullName { fullname = it }

    viewModel.getJoinCode { joinCode = it }

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
                IconButton(onClick = {}) {
                    Icon(contentDescription = "Home", imageVector = Icons.Filled.Home, tint = Color.Blue)
                }
                IconButton(onClick = goToSchedule) {
                    Icon(contentDescription = "Schedule", imageVector = Icons.Filled.DateRange)
                }
                IconButton(onClick = goToRoster) {
                    Icon(contentDescription = "Roster", imageVector = Icons.Filled.Person)
                }
                IconButton(onClick = goToChatSelect) {
                    Icon(contentDescription = "Chat", imageVector = Icons.Filled.Forum)
                }
            }

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Welcome $fullname", style = TextStyle(fontSize = 30.sp))
            Text(text = "You are a ${GS.user!!.type}", style = TextStyle(fontSize = 30.sp)) // test type

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedCard(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if(announcements == null) Text( text = "Loading...", style = TextStyle(fontSize = 16.sp))
                    else LazyColumn(Modifier.fillMaxWidth()) {
                        items(announcements!!) {
                                announcement -> ListItem(
                            headlineContent = { Text(announcement.content) },
                            supportingContent =
                            { Text("~ ${announcement.authorName} | ${
                                getDateTimeInstance().format(
                                    Date(announcement.datePosted)
                                )}") }
                        )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(55.dp))

            Text(text = "Upcoming Game shown here")

            Spacer(modifier = Modifier.height(55.dp))

            Button(
                onClick = goToLeagueStandings,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(100.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        contentDescription = "Standings",
                        imageVector = Icons.AutoMirrored.Filled.List,
                        modifier = Modifier.size(90.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "League Standings",
                        fontSize = 24.sp,
                    )
                }
            }

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Upload a Form")
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (joinCode.isNotEmpty()) {
                SelectionContainer {
                    Text(
                        text = joinCode,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}