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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.team9.soccermanager.model.Announcement
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.model.MenuScreens
import com.team9.soccermanager.ui.composable.BarsWrapper
import java.text.DateFormat.getDateTimeInstance
import java.util.Date

@Composable
fun CoachHomeView(
    viewModel: CoachHomeViewModel = remember { CoachHomeViewModel() },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
    switchMenuScreen: (MenuScreens) -> Unit,
    goToLeagueStandings: () -> Unit,
    goToForms: () -> Unit,
    goToAnnouncements: () -> Unit
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

    BarsWrapper(
        title = "Home",
        activeScreen = MainScreens.HOME,
        signOut = { viewModel.signOut(); switchToWelcome() },
        switchMainScreen = switchMainScreen,
        switchMenuScreen = switchMenuScreen
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Hello $fullname", style = TextStyle(fontSize = 30.sp))

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
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if(announcements == null || announcements!!.isEmpty()
                    ) Text( text = "", style = TextStyle(fontSize = 16.sp))
                    else Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxSize()
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 12.dp),
                    ) {
                        val announcement = announcements!!.reversed().first()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = announcement.authorName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = getDateTimeInstance().format(announcement.datePosted),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = announcement.content,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    TextButton(
                        onClick = { goToAnnouncements() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Blue
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(top = 7.dp, end = 2.dp)
                    ) {
                        Text(
                            text = "View More"
                        )
                    }
                }
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
        }
    }
}