package com.team9.soccermanager.screens.gameedit

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team9.soccermanager.model.GameStatus
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.model.MenuScreens
import com.team9.soccermanager.ui.composable.BarsWrapper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameEditView(
    gameId: Int,
    newGame: Boolean,
    viewModel: GameEditViewModel = remember { GameEditViewModel(gameId, newGame) },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
    switchMenuScreen: (MenuScreens) -> Unit
) {
    val loading by remember { viewModel.getLoading() }
    val errorLoading by remember { viewModel.getErrorLoading() }

    BarsWrapper(
        title = if (newGame) "New Game" else "Editing Game",
        activeScreen = MainScreens.SCHEDULE,
        signOut = { viewModel.signOut(); switchToWelcome() },
        switchMainScreen = switchMainScreen,
        switchMenuScreen = switchMenuScreen,
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
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (errorLoading.isNotEmpty()) {
                    Text(
                        text = errorLoading,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (loading) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(60.dp))
                    }
                } else {
                    GameEditForms(viewModel, { switchMainScreen(MainScreens.BACK) })
                }
            }
        }
    }
}

@Composable
fun GameEditForms(viewModel: GameEditViewModel, goBack: () -> Unit) {
    var team1Score by remember { viewModel.getTeam1Score() }
    var team2Score by remember { viewModel.getTeam2Score() }
    var date by remember { viewModel.getDate() }
    var hours by remember { viewModel.getHours() }
    var minutes by remember { viewModel.getMinutes() }
    val teams = remember { viewModel.getTeams() }
    var statusSelect by remember { viewModel.getStatusSelect() }
    var team1Select by remember { viewModel.getTeam1Select() }
    var team2Select by remember { viewModel.getTeam2Select() }
    var showDateSelect by remember { viewModel.getShowDateSelect() }
    var showTimeSelect by remember { viewModel.getShowTimeSelect() }
    val errorSaving by remember { viewModel.getErrorSaving() }
    val errorTitle by remember { viewModel.getErrorTitle() }

    DropDown("Game status", statusSelect, { statusSelect = it }, GameStatus.entries.map { DisplayableStatus(it) })
    Spacer(modifier = Modifier.height(10.dp))

    DropDown("Team 1", team1Select, { team1Select = it }, teams.filter { it.id.isEmpty() || it.id != team2Select.id })
    Spacer(modifier = Modifier.height(10.dp))

    DropDown("Team 2", team2Select, { team2Select = it }, teams.filter { it.id.isEmpty() || it.id != team1Select.id })
    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = team1Score,
        onValueChange = { team1Score = it },
        label = { Text("Team 1 Score") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = team2Score,
        onValueChange = { team2Score = it },
        label = { Text("Team 2 Score") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = if (date == null) "" else SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(date!!),
        onValueChange = { },
        label = { Text("Date") },
        placeholder = { Text("Date") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(date) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showDateSelect = true
                    }
                }
            }
    )
    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = if (hours == null) "" else formatTime(hours!!, minutes!!),
        onValueChange = { },
        label = { Text("Time") },
        placeholder = { Text("Time") },
        trailingIcon = {
            Icon(Icons.Default.AccessTime, contentDescription = "Select time")
        },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(hours, minutes) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent =
                        waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showTimeSelect = true
                    }
                }
            }
    )
    Spacer(modifier = Modifier.height(10.dp))

    Button(
        onClick = { viewModel.writeGame(goBack) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Submit")
    }

    if (showDateSelect) {
        DatePickerModal(
            onDateSelected = { date = if (it == null) null else Date(it) },
            onDismiss = { showDateSelect = false }
        )
    }

    if (showTimeSelect) {
        TimePickerModal(
            onTimeSelected = { hour, minute -> hours = hour; minutes = minute },
            onDismiss = { showTimeSelect = false },
        )
    }

    if (errorSaving.isNotEmpty()) {
        AlertDialog(
            title = {
                Text(
                    text = errorTitle,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            },
            text = {
                Text(
                    text = errorSaving,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            onDismissRequest = { viewModel.resetError() },
            confirmButton = {
                TextButton(onClick = { viewModel.resetError() }) {
                    Text("Ok")
                }
            }
        )
    }
}

private fun formatTime(hours: Int, minutes: Int): String {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, hours)
    cal.set(Calendar.MINUTE, minutes)
    return SimpleDateFormat("hh:mm a zzz", Locale.getDefault()).format(cal.time)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        modifier = Modifier.padding(16.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerModal(
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(timePickerState.hour, timePickerState.minute)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        text = {
            TimePicker(
                state = timePickerState,
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropDown(label: String, selected: T, setSelected: (T) -> Unit, options: List<T>) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            value = selected.toString(),
            onValueChange = {},
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString(), style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        setSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}