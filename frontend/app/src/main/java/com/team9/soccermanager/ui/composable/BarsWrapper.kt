package com.team9.soccermanager.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.team9.soccermanager.model.MainScreens

@Composable
fun BarsWrapper(
    title: String = "",
    activeScreen: MainScreens,
    signOut: () -> Unit,
    allowBack: Boolean = false,
    switchMainScreen: (MainScreens) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
        topBar =  {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (allowBack) {
                    IconButton(onClick = { switchMainScreen(MainScreens.BACK) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back",
                        )
                    }
                }
                Text(text = title)
                Button(
                    onClick = signOut,
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
                    .padding(8.dp, 0.dp, 8.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { switchMainScreen(MainScreens.HOME) }) {
                    Icon(contentDescription = "Home", imageVector = Icons.Filled.Home, tint = getTint(MainScreens.HOME, activeScreen))
                }
                IconButton(onClick = { switchMainScreen(MainScreens.SCHEDULE) }) {
                    Icon(contentDescription = "Schedule", imageVector = Icons.Filled.DateRange, tint = getTint(MainScreens.SCHEDULE, activeScreen))
                }
                IconButton(onClick = { switchMainScreen(MainScreens.ROSTER) }) {
                    Icon(contentDescription = "Roster", imageVector = Icons.Filled.Person, tint = getTint(MainScreens.ROSTER, activeScreen))
                }
                IconButton(onClick = { switchMainScreen(MainScreens.CHAT) }) {
                    Icon(contentDescription = "Chat", imageVector = Icons.Filled.Forum, tint = getTint(MainScreens.CHAT, activeScreen))
                }
            }
        },
        content = content
    )
}

@Composable
private fun getTint(curScreen: MainScreens, activeScreen: MainScreens): Color {
    return if (curScreen == activeScreen) {
        MaterialTheme.colorScheme.surfaceTint
    } else {
        LocalContentColor.current
    }
}