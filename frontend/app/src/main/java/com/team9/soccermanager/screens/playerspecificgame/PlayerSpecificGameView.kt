package com.team9.soccermanager.screens.playerspecificgame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun PlayerSpecificGameView(
    viewModel: PlayerSpecificGameViewModel = PlayerSpecificGameViewModel(),
    switchToWelcome: () -> Unit,
    goToHome: () -> Unit,
    goToSchedule: () -> Unit,
    goToRoster: () -> Unit,
    goToChatScreen: () -> Unit
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
                IconButton(onClick = goToChatScreen) {
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
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {
                val gamelocation = Place("Leverkusen", LatLng(51.0459, 7.0192))


                // Add GoogleMap here
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxHeight()
                        .padding(0.dp, 20.dp, 0.dp, 0.dp),
                    cameraPositionState = rememberCameraPositionState { // Update camera position
                        position = CameraPosition.fromLatLngZoom(gamelocation.location, 10f)
                    },
                    onMapLoaded = { isMapLoaded = true }
                ) {

                    Marker(
                        state = MarkerState(position = gamelocation.location),
                        title = gamelocation.name,
                        snippet = "Marker for ${gamelocation.name}"
                    )
                    // ..

                }
            }
        }
    )
}
