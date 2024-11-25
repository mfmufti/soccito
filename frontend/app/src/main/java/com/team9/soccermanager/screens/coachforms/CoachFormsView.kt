package com.team9.soccermanager.screens.coachforms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team9.soccermanager.model.Form
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.ui.composable.BarsWrapper

@Composable
fun CoachFormsView(
    viewModel: CoachFormsViewModel = remember { CoachFormsViewModel() },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
    goToSpecificForm: (Int, String) -> Unit,
) {
    val forms = remember { viewModel.getForms() }
    val loading by remember { viewModel.getLoading() }
    val error by remember { viewModel.getError() }
    var addingNew by remember { viewModel.getAddingNew() }
    var newName by remember { viewModel.getNewName() }
    var errorAdding by remember { viewModel.getErrorAdding() }

    BarsWrapper(
        title = "Form Dropboxes",
        activeScreen = MainScreens.HOME,
        signOut = { viewModel.signOut(); switchToWelcome() },
        allowBack = true,
        switchMainScreen = switchMainScreen,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (loading) {
                Box(modifier = Modifier.padding(20.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(60.dp))
                }
            } else if (forms.isEmpty()) {
                Text(
                    text = "No form dropboxes exist",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )
            } else {
                FormList(goToSpecificForm, forms)

                SmallFloatingActionButton(
                    onClick = { addingNew = true },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Align it to the bottom-end (right bottom corner)
                        .padding(8.dp)
                        .size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add a dropbox",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            if (addingNew) {
                AlertDialog(
                    title = { Text(
                        text = "Create Dropbox",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    ) },
                    text = { TextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Dropbox name") },
                        maxLines = 1,
                        modifier = Modifier.padding(20.dp)
                    ) },
                    onDismissRequest = { newName = ""; addingNew = false },
                    confirmButton = {
                        TextButton(onClick = { viewModel.addDropBox() }) {
                            Text("Create")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { newName = ""; addingNew = false }) {
                            Text("Dismiss")
                        }
                    }
                )
            }

            if (errorAdding.isNotEmpty()) {
                AlertDialog(
                    title = { Text(
                        text = "Error Creating Dropbox",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    ) },
                    text = { Text(
                        text = errorAdding,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    ) },
                    onDismissRequest = { errorAdding = "" },
                    confirmButton = {
                        TextButton(onClick = { errorAdding = "" }) {
                            Text("Ok")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FormList(goToSpecificForm: (Int, String) -> Unit, forms: List<Form>) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        for (form in forms) {
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = form.name,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { goToSpecificForm(form.id, form.name) },
                        colors = ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Text("View Uploads")
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}