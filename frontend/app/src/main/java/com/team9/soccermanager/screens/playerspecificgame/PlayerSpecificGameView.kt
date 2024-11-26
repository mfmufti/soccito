package com.team9.soccermanager.screens.playerspecificgame


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.firebase.Timestamp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.ui.composable.BarsWrapper
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun PlayerSpecificGameView(
    gameIndex: Int,
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
) {
    // Get context outside remember
    val context = LocalContext.current
    val viewModel: PlayerSpecificGameViewModel = remember(gameIndex) {
        PlayerSpecificGameViewModel(gameIndex, context)
    }

    var teamName by remember { mutableStateOf("") }
    var isMapLoaded by remember { mutableStateOf(false) }
    val game = remember { viewModel.getGame() }
    val location by viewModel.locationState.collectAsState()

    viewModel.getTeamName { teamName = it }

    BarsWrapper(
        title = "Game Details",
        activeScreen = MainScreens.SCHEDULE,
        signOut = { viewModel.signOut(); switchToWelcome() },
        switchMainScreen = switchMainScreen,
        allowBack = true,
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    location?.let { gameLocation ->
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxSize()
                                .fillMaxHeight()
                                .padding(0.dp, 20.dp, 0.dp, 0.dp),
                            cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(gameLocation, 10f)
                            },
                            onMapLoaded = { isMapLoaded = true },
                            uiSettings = MapUiSettings(
                                zoomControlsEnabled = true,           // Hide the +/- zoom buttons
                                compassEnabled = true,                // Hide the compass
                                myLocationButtonEnabled = true,       // Hide the "my location" button
                                mapToolbarEnabled = true,            // Disable the toolbar again (redundant but thorough)
                                rotationGesturesEnabled = false,      // Disable rotation again (redundant but thorough)
                                scrollGesturesEnabled = false,        // Disable scrolling again (redundant but thorough)
                                tiltGesturesEnabled = false,          // Disable tilt again (redundant but thorough)
                                zoomGesturesEnabled = true           // Disable zoom again (redundant but thorough)
                            )

                        ) {
                            Marker(
                                state = MarkerState(position = gameLocation),
                                title = game.address,
                                snippet = "Game Location"
                            )
                        }
                        if (!isMapLoaded) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { },
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Game Score",
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp
                        )
                        Text(
                            text = game.team1Name + " "  + game.score + " " + game.team2Name,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        if (game.timestamp >= Timestamp.now()) {
                        Text(
                            text = SimpleDateFormat(
                                "MMM d, y 'at' hh:mm a zzz",
                                Locale.US
                            ).format(game.timestamp.toDate())
                        )
                    }

                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
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
                        if (game.team1CoachsNotes.isNotEmpty() && (GS.user!!.teamID == game.team1ID)) {
                            Text(text = game.team1CoachsNotes, style = TextStyle(fontSize = 16.sp))
                        } else if (game.team2CoachsNotes.isNotEmpty() && (GS.user!!.teamID == game.team2ID)) {
                            Text(text = game.team2CoachsNotes, style = TextStyle(fontSize = 16.sp))
                        } else {
                                Text(
                                    text = "No Coachs Notes Yet",
                                    style = TextStyle(fontSize = 32.sp),
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }




                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Match: ${game.team1Name} vs. ${game.team2Name}")
                }
            }
        }
    }
}
