package com.team9.soccermanager.screens.formspecific

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.google.firebase.Timestamp
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.model.MenuScreens
import com.team9.soccermanager.ui.composable.BarsWrapper
import java.text.DateFormat.getDateTimeInstance
import java.util.Date

/*
 Composable function for the form-specific screen.
  It displays a list of form uploads for a specific form.
 */

@Composable
fun FormSpecificView(
    id: Int,
    title: String,
    viewModel: FormSpecificViewModel = remember { FormSpecificViewModel(id) },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
    switchMenuScreen: (MenuScreens) -> Unit
) {
    val uploads = remember { viewModel.getUploads() }
    val loading by remember { viewModel.getLoading() }
    val error by remember { viewModel.getError() }

    BarsWrapper(
        title = title,
        activeScreen = MainScreens.HOME,
        allowBack = true,
        switchMainScreen = switchMainScreen,
        switchMenuScreen = switchMenuScreen,
        signOut = { viewModel.signOut(); switchToWelcome() },
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else if (loading) {
                Box(modifier = Modifier.padding(20.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(60.dp))
                }
            } else if (uploads.isEmpty()) {
                Text(
                    text = "Upload list is empty",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                UploadList(uploads)
            }
        }
    }
}

@Composable
private fun UploadList(uploads: List<Upload>) {
    val ctx = LocalContext.current

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp),
    ) {
        for (upload in uploads) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = upload.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (upload.uploaded) {
                        Text(
                            text = getDateTimeInstance().format(upload.time.toDate()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Download",
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.clickable {
                                // Handle the link click action, e.g., opening the URL.
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upload.link))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                ctx.startActivity(intent)
                            }
                        )
                    } else {
                        Text(
                            text = "Not yet uploaded",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}