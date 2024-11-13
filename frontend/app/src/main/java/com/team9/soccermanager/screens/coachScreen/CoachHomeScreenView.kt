package com.team9.soccermanager.screens.coachScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import com.team9.soccermanager.model.Account
import com.team9.soccermanager.model.Announcement
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team
import java.text.DateFormat.getDateTimeInstance
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun CoachHomeScreenView(
    viewModel: CoachHomeScreenViewModel = CoachHomeScreenViewModel(),
    switchToWelcome: () -> Unit,
    goToLeagueStandings: () -> Unit,
    goToSchedule: () -> Unit,
    goToRoster: () -> Unit,
    goToChat: () -> Unit
) {
    var teamName by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var announcements by remember { mutableStateOf<List<Announcement>?>(null) }
    var announcementContent by remember { mutableStateOf("") }
    var showAnnouncementForm by remember { mutableStateOf(false) }

    viewModel.getTeam {
        announcements = it.announcements.toList()
    }
    viewModel.getFullName { fullname = it }

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
                IconButton(onClick = {}) {
                    Icon(contentDescription = "Home", imageVector = Icons.Filled.Home, tint = Color.Blue)
                }
                IconButton(onClick = goToSchedule) {
                    Icon(contentDescription = "Schedule", imageVector = Icons.Filled.DateRange)
                }
                IconButton(onClick = goToRoster) {
                    Icon(contentDescription = "Roster", imageVector = Icons.Filled.Person)
                }
                IconButton(onClick = goToChat) {
                    Icon(contentDescription = "Chat", imageVector = Icons.AutoMirrored.Default.Send)
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
                    .height(200.dp),
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
                                { Text("~ ${announcement.authorName} | ${getDateTimeInstance().format(Date(announcement.datePosted))}") }
                            )
                        }
                    }
                    SmallFloatingActionButton(
                        onClick = { showAnnouncementForm = true },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // Align it to the bottom-end (right bottom corner)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Filled.Add, "Small floating action button.")
                    }
                }
            }

            if(showAnnouncementForm) {
                AlertDialog(
                    title = {
                        Text(text = "Create Announcement")
                    },
                    text = {
                        TextField(
                            value = announcementContent,
                            onValueChange = { announcementContent = it },
                            label = { Text("Enter text") },
                            maxLines = 2,
                            textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(20.dp)
                        )
                    },
                    onDismissRequest = {
                        showAnnouncementForm = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // TODO add announcment
                                viewModel.addAnnouncement(announcementContent) {
                                    // then reset text
                                    viewModel.getTeam { announcements = it.announcements.toList() }
                                    showAnnouncementForm = false
                                    announcementContent = ""
                                }
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showAnnouncementForm = false
                                announcementContent = ""
                            }
                        ) {
                            Text("Dismiss")
                        }
                    }
                )
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
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("View Forms")
            }

        }
    }
}