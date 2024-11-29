package com.team9.soccermanager.screens.announcements

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team9.soccermanager.model.Announcement
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.model.MenuScreens
import com.team9.soccermanager.ui.composable.BarsWrapper

@Composable
fun CoachAnnouncementsView(
    title: String,
    viewModel: CoachAnnouncementsViewModel = remember { CoachAnnouncementsViewModel() },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
    switchMenuScreen: (MenuScreens) -> Unit
) {

    var announcements by remember { mutableStateOf<List<Announcement>?>(null) }
    var showAnnouncementForm by remember { mutableStateOf(false) }
    var announcementContent by remember { mutableStateOf("") }

    viewModel.getTeam {
        announcements = it.announcements.toList()
    }

    BarsWrapper(
        title = title,
        activeScreen = MainScreens.HOME,
        allowBack = true,
        switchMainScreen = switchMainScreen,
        switchMenuScreen = switchMenuScreen,
        signOut = { viewModel.signOut(); switchToWelcome() },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopStart
        ) {
            AnnouncementList(announcements?.reversed() ?: emptyList())
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
        }
    }
}