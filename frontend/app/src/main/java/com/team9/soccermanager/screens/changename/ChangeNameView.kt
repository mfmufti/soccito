package com.team9.soccermanager.screens.changename

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.twotone.ViewQuilt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.ui.theme.success
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeNameView(
    switchBack: () ->  Unit,
    viewModel: ChangeNameViewModel = remember { ChangeNameViewModel() }
) {

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var error = remember { viewModel.getError() }

    if (GS.user != null) {

        var currName by remember { mutableStateOf(GS.user!!.fullname) }
        var isEditing by remember { mutableStateOf(false) }
        var tempName by remember { mutableStateOf(GS.user!!.fullname) }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 50.dp),
                        containerColor = Color(76, 130, 56),
                        contentColor = Color.White,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = data.visuals.message,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            },
            topBar = {
                TopAppBar(
                    title = { Text("Edit Fullname", fontSize = 20.sp) },
                    navigationIcon = {
                        IconButton(onClick = switchBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back",
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column (
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (error.value.isNotEmpty()) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error Icon",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error.value,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                OutlinedTextField(
                    enabled = isEditing,
                    value = if (isEditing) tempName else currName,
                    onValueChange = { tempName = it },
                    label = { Text("Edit fullname") },
                    singleLine = true,
                    trailingIcon = {
                        if (isEditing) {
                            Row (
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ){
                                IconButton(
                                    enabled = tempName.isNotBlank(),
                                    onClick = {
                                        currName = tempName
                                        isEditing = false
                                        viewModel.handleNameChange(tempName) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(message = "Fullname updated!", duration = SnackbarDuration.Short)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Confirm Edit",
                                        tint = MaterialTheme.colorScheme.success
                                    )
                                }
                                IconButton(onClick = {
                                    tempName = currName
                                    isEditing = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Cancel,
                                        contentDescription = "Cancel Edit",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        } else {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = "Edit Text",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Failed to load user fullname", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        }
    }
}