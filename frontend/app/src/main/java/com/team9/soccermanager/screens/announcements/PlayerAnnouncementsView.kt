package com.team9.soccermanager.screens.announcements

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team9.soccermanager.model.Announcement
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.model.MenuScreens
import com.team9.soccermanager.ui.composable.BarsWrapper
import java.text.DateFormat.getDateTimeInstance

@Composable
fun PlayerAnnouncementsView(
    title: String,
    viewModel: PlayerAnnouncementsViewModel = remember { PlayerAnnouncementsViewModel() },
    switchToWelcome: () -> Unit,
    switchMenuScreen: (MenuScreens) -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
) {

    BarsWrapper(
        title = title,
        activeScreen = MainScreens.HOME,
        allowBack = true,
        switchMainScreen = switchMainScreen,
        switchMenuScreen = switchMenuScreen,
        signOut = { viewModel.signOut(); switchToWelcome() },
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            AnnouncementList(viewModel.announcements.value?.reversed() ?: emptyList())
        }
    }
}

@Composable
fun AnnouncementList(announcements: List<Announcement>) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp),
    ) {
        if (announcements.isEmpty()) {
            Box(
                modifier = Modifier.padding(16.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No announcements currently...", textAlign = TextAlign.Center, fontStyle = FontStyle.Italic)
            }
        }

        for (announcement in announcements) {
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
                            text = if (announcement.authorName.length <= 20) {
                                    announcement.authorName
                                } else {
                                    announcement.authorName.take(20) + "..."
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = announcement.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = getDateTimeInstance().format(announcement.datePosted),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}