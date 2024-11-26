package com.team9.soccermanager.screens.playerspecificgame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.model.Winner
import com.team9.soccermanager.ui.composable.BarsWrapper
import com.team9.soccermanager.ui.theme.success
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PlayerSpecificGameView(
    gameId: Int,
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
) {
    // Get context outside remember
    val context = LocalContext.current
    val viewModel: PlayerSpecificGameViewModel = remember(gameId) {
        PlayerSpecificGameViewModel(gameId, context)
    }

    var teamName by remember { mutableStateOf("") }
    var isMapLoaded by remember { mutableStateOf(false) }
    val game = remember { viewModel.getGame() }
    val location by viewModel.locationState.collectAsState()
    var editing by remember { viewModel.getEditing() }
    val coachsNotes by remember { viewModel.getCoachsNotes() }
    var coachsNotesEditing by remember { viewModel.getCoachsNotesEditing() }
    var errorEditing by remember { viewModel.getErrorEditing() }

    viewModel.getTeamName { teamName = it }

    BarsWrapper(
        title = if (game.winner == Winner.UNKNOWN) "Game Planned" else "Game Completed",
        activeScreen = MainScreens.SCHEDULE,
        signOut = { viewModel.signOut(); switchToWelcome() },
        switchMainScreen = switchMainScreen,
        allowBack = true,
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding(),
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
                        .padding(5.dp)
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.inverseOnSurface),
                    contentAlignment = Alignment.Center,
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
                            text = "${game.team1Name} ${game.team1Score} - ${game.team2Score} ${game.team2Name}",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = SimpleDateFormat("MMM d, y 'at' hh:mm a zzz", Locale.US).format(game.timestamp.toDate())
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

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

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = game.address,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                )

                if (game.winner != Winner.UNKNOWN || GS.user!!.type == "admin") {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text("Coach's notes:")
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedCard(
                        modifier = Modifier
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
                            if (editing) {
                                BasicTextField(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    value = coachsNotesEditing,
                                    onValueChange = { coachsNotesEditing = it },
                                    textStyle = TextStyle(
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                                    maxLines = 5
                                )
                                Row(modifier = Modifier.align(Alignment.BottomEnd)) {
                                    Icon(
                                        imageVector = Icons.Filled.Cancel,
                                        contentDescription = "Cancel editing",
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clickable { viewModel.cancelNotesEditing() },
                                        MaterialTheme.colorScheme.error,
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Finish editing",
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clickable { viewModel.updateCoachsNotes() },
                                        tint = MaterialTheme.colorScheme.success,
                                    )
                                }
                            } else {
                                if (coachsNotes.isNotEmpty()) {
                                    Text(
                                        text = coachsNotes,
                                        style = TextStyle(fontSize = 16.sp)
                                    )
                                } else {
                                    Text(
                                        text = "No Coach's Notes Yet",
                                        style = TextStyle(fontSize = 32.sp),
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                if (GS.user!!.type == "coach") {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit Coach's Notes",
                                        modifier = Modifier
                                            .size(30.dp)
                                            .align(Alignment.BottomEnd)
                                            .clickable { editing = true },
                                    )
                                }
                            }
                        }
                    }
                }

                if (errorEditing.isNotEmpty()) {
                    AlertDialog(
                        title = { Text(
                            text = "Error Saving Changes",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        ) },
                        text = { Text(
                            text = errorEditing,
                            modifier = Modifier.fillMaxWidth(),
                        ) },
                        onDismissRequest = { errorEditing = "" },
                        confirmButton = {
                            TextButton(onClick = { errorEditing = "" }) {
                                Text("Ok")
                            }
                        }
                    )
                }
            }
        }
    }
}
