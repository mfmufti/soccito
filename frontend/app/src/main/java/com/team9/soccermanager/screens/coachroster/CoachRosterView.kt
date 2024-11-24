package com.team9.soccermanager.screens.coachroster

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Paint.Align
import android.widget.RadioButton
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team9.soccermanager.model.AvailView
import com.team9.soccermanager.model.Availability
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.model.PlrAvail
import com.team9.soccermanager.ui.composable.BarsWrapper

@Composable
fun CoachRosterView(
    viewModel: CoachRosterViewModel = remember { CoachRosterViewModel() },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit
) {

    val availList = remember { viewModel.getAvailList() }
    val loading = remember { viewModel.getLoading() }
    val error = remember { viewModel.getError() }
    val editing = remember { mutableStateOf(false) }
    var selectedPlr = remember { AvailView() }

    BarsWrapper(
        title = "Roster",
        activeScreen = MainScreens.ROSTER,
        signOut = { viewModel.signOut(); switchToWelcome() },
        switchMainScreen = switchMainScreen,
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (editing.value) {
                    ShowEditingDialog(selectedPlr, { editing.value = false }, handleSubmit = {
                        id, newAvail, reason, onError, onSuccess -> viewModel.handleAvailUpdate(id, newAvail, reason, onError, onSuccess)
                    } )
                }

                if (error.value.isNotEmpty()) {
                    Text(
                        text = error.value,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (loading.value) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(60.dp))
                    }
                } else {
                    if (availList.size == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 150.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No players in the team currently...",
                                fontStyle = FontStyle.Italic
                            )
                        }

                    }
                    for (index in 0..<availList.size) {
                        val playeravail = availList[index]
                        Box(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                                .background(MaterialTheme.colorScheme.inverseOnSurface)
                                .padding(15.dp)
                                .fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = playeravail.playerName,
                                    fontSize = 25.sp
                                )
                                Spacer(modifier = Modifier.height(11.dp))
                                Text(
                                    text = playeravail.playerAvail.avail.toString(),
                                    fontSize = 14.sp
                                )
                                if (playeravail.playerAvail.reason.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = playeravail.playerAvail.reason,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        selectedPlr = playeravail
                                        editing.value = true
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit availability"
                                    )
                                }
                            }

                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ShowEditingDialog(currPlr: AvailView, dismiss: () -> Unit, handleSubmit: (String, Boolean, String, (String) -> Unit, () -> Unit) -> Unit) {

    val available = remember { mutableStateOf(currPlr.playerAvail.avail == Availability.AVAILABLE) }
    val reason = remember { mutableStateOf(currPlr.playerAvail.reason) }
    val editError = remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = dismiss,
    ) {
        Card(
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth(0.95f)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
            ) {
                Column(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Availability for ${currPlr.playerName}",
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(25.dp))
                Row (
                    modifier = Modifier.clickable(onClick = { available.value = true; editError.value = "" })
                ){
                    RadioButton(
                        modifier = Modifier.size(15.dp),
                        selected = available.value,
                        onClick = { available.value = true; editError.value = "" }
                    )
                    Text(
                        text = "Available",
                        modifier = Modifier.padding(start = 10.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    modifier = Modifier.clickable(onClick = { available.value = false })
                ){
                    RadioButton(
                        modifier = Modifier.size(15.dp),
                        selected = !available.value,
                        onClick = { available.value = false }
                    )
                    Text(
                        text = "Unavailable",
                        modifier = Modifier.padding(start = 10.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (!available.value) {
                    TextField(
                        value = reason.value,
                        onValueChange = { reason.value = it },
                        label = { Text("Enter unavailability reason") },
                        maxLines = 4,
                        textStyle = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                if (editError.value.isNotEmpty()) {
                    Text(text = editError.value, color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(15.dp))

                Row {
                    Button(
                        onClick = { handleSubmit(currPlr.playerId, available.value, reason.value, { editError.value = it }, dismiss) }
                    ) {
                        Text(text = "Submit")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Button(
                        onClick = dismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                    ) {
                        Text(text = "Cancel")
                    }
                }

            }
        }
    }
}