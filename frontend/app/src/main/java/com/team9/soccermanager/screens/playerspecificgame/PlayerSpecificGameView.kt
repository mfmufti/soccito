package com.team9.soccermanager.screens.playerspecificgame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun PlayerSpecificGameView(
    viewModel: PlayerSpecificGameViewModel = PlayerSpecificGameViewModel(),
    switchToWelcome: () -> Unit,
    goToHome: () -> Unit,
    goToSchedule: () -> Unit,
    goToRoster: () -> Unit,
    goToChat: () -> Unit
) {
    var teamName by remember { mutableStateOf("") }
    viewModel.getTeamName { teamName = it }
    var isMapLoaded by remember { mutableStateOf(false) }

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
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = goToHome) {
                    Icon(contentDescription = "Home", imageVector = Icons.Filled.Home)
                }
                IconButton(onClick = goToSchedule) {
                    Icon(contentDescription = "Schedule", imageVector = Icons.Filled.DateRange)
                }
                IconButton(onClick = goToRoster) {
                    Icon(contentDescription = "Roster", imageVector = Icons.Filled.Person)
                }
                IconButton(onClick = goToChat) {
                    Icon(contentDescription = "Chat", imageVector = Icons.Filled.Forum)
                }
            }
        },
        content = { paddingValues ->
            data class Place(
                val name: String,
                val location: LatLng
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding()
            ) {
                val places = listOf(
                    // Your list of Place objects
                    Place("Leverkusen", LatLng(51.0459, 7.0192)),
                    Place("Chelsea", LatLng(51.4869, 0.1700)),
                    // ... more places
                )

                // Add GoogleMap here
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onMapLoaded = { isMapLoaded = true }
                ) {
                    places.forEach { place ->
                        Marker(
                            state = MarkerState(position = place.location),
                            title = place.name,
                            snippet = "Marker for ${place.name}"
                        )

                        // ...
                    }

                }
            }
        }
    )
}
