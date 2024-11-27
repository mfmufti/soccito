package com.team9.soccermanager.screens.announcements

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.team9.soccermanager.model.Announcement
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.ui.composable.BarsWrapper
import java.text.DateFormat.getDateTimeInstance

@Composable
fun PlayerAnnouncementsView(
    title: String,
    viewModel: PlayerAnnouncementsViewModel = remember { PlayerAnnouncementsViewModel() },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
) {

    BarsWrapper(
        title = title,
        activeScreen = MainScreens.HOME,
        allowBack = true,
        switchMainScreen = switchMainScreen,
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
        for (announcement in announcements) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = announcement.authorName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = getDateTimeInstance().format(announcement.datePosted),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = announcement.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}