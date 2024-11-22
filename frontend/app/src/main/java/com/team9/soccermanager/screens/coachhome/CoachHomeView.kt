package com.team9.soccermanager.screens.coachhome

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.team9.soccermanager.model.Announcement
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.ui.composable.BarsWrapper
import java.text.DateFormat.getDateTimeInstance
import java.util.Date

@Composable
fun CoachHomeView(
    viewModel: CoachHomeViewModel = remember { CoachHomeViewModel() },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
    goToLeagueStandings: () -> Unit,
    goToForms: () -> Unit
) {
    var teamName by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var announcements by remember { mutableStateOf<List<Announcement>?>(null) }
    var announcementContent by remember { mutableStateOf("") }
    var showAnnouncementForm by remember { mutableStateOf(false) }
    var joinCode by remember { mutableStateOf("") }

    viewModel.getTeam {
        announcements = it.announcements.toList()
    }
    viewModel.getFullName { fullname = it }
    viewModel.getJoinCode { joinCode = it }

    BarsWrapper(
        title = "Home",
        activeScreen = MainScreens.HOME,
        signOut = { viewModel.signOut(); switchToWelcome() },
        switchMainScreen = switchMainScreen,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
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
                    if (announcements == null) Text( text = "", style = TextStyle(fontSize = 16.sp))
                    else LazyColumn(Modifier.fillMaxWidth()) {
                        items(announcements!!.reversed()) {
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

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = "Upcoming Game shown here")

            Spacer(modifier = Modifier.height(30.dp))

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
                onClick = goToForms,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("View Forms")
            }

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