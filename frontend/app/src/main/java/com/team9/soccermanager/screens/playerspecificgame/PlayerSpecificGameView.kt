package com.team9.soccermanager.screens.playerspecificgame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.ui.composable.BarsWrapper

@Composable
fun PlayerSpecificGameView(
    viewModel: PlayerSpecificGameViewModel = PlayerSpecificGameViewModel(),
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
) {
    var teamName by remember { mutableStateOf("") }
    viewModel.getTeamName { teamName = it }
    var isMapLoaded by remember { mutableStateOf(false) }

    BarsWrapper(
        title = "X vs. Y?",
        activeScreen = MainScreens.SCHEDULE,
        signOut = { viewModel.signOut(); switchToWelcome() },
        switchMainScreen = switchMainScreen,
        allowBack = true,
    ) { paddingValues ->
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
            val gameLocation = Place("Leverkusen", LatLng(51.0459, 7.0192))

            // Add GoogleMap here
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxHeight()
                    .padding(0.dp, 20.dp, 0.dp, 0.dp),
                cameraPositionState = rememberCameraPositionState { // Update camera position
                    position = CameraPosition.fromLatLngZoom(gameLocation.location, 10f)
                },
                onMapLoaded = { isMapLoaded = true }
            ) {

                Marker(
                    state = MarkerState(position = gameLocation.location),
                    title = gameLocation.name,
                    snippet = "Marker for ${gameLocation.name}"
                )
                // ..

            }
        }
    }
}
