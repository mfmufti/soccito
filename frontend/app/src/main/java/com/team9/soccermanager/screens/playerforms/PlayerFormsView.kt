package com.team9.soccermanager.screens.playerforms

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.model.MenuScreens
import com.team9.soccermanager.ui.composable.BarsWrapper
import com.team9.soccermanager.ui.theme.success
import java.text.DateFormat.getDateTimeInstance
import kotlin.math.floor

@SuppressLint("DefaultLocale")
@Composable
fun PlayerFormsView(
    viewModel: PlayerFormsViewModel = remember { PlayerFormsViewModel() },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
    switchMenuScreen: (MenuScreens) -> Unit
) {
    val formUploads = remember { viewModel.getFormUploads() }
    val loading by remember { viewModel.getLoading() }
    val error by remember { viewModel.getError() }
    val uploading by remember { viewModel.getUploading()}
    val progress by remember { viewModel.getProgress() }
    var uploadError by remember { viewModel.getUploadError() }

    BarsWrapper(
        title = "Upload a Form",
        activeScreen = MainScreens.HOME,
        signOut = { viewModel.signOut(); switchToWelcome() },
        allowBack = true,
        switchMainScreen = switchMainScreen,
        switchMenuScreen = switchMenuScreen
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
            } else if (formUploads.isEmpty()) {
                Text(
                    text = "No form dropboxes exist",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                FormList(formUploads, { uri, contentResolver, id -> viewModel.uploadForm(uri, contentResolver, id) })

                if (uploading) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text(
                            text = "Uploading (${floor(progress * 100).toInt()}%)...",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        ) },
                        text = { Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                progress = { progress.toFloat() }
                            )
                        } },
                        confirmButton = {
                            TextButton(onClick = { viewModel.cancelUpload() }) { Text("Cancel") }
                        },
                    )
                }

                if (uploadError.isNotEmpty()) {
                    AlertDialog(
                        title = { Text(
                            text = "Error Uploading Form",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        ) },
                        text = { Text(
                            text = uploadError,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        ) },
                        onDismissRequest = { uploadError = "" },
                        confirmButton = {
                            TextButton(onClick = { uploadError = "" }) {
                                Text("Ok")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FormList(formUploads: List<SimpleFormUpload>, uploadForm: (Uri, ContentResolver, Int) -> Unit) {
    val ctx = LocalContext.current
    val contentResolver = ctx.contentResolver
    var curId by remember { mutableIntStateOf(0) }
    val pickFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // The user selected a file, you can now open it or read from it
            uploadForm(it, contentResolver, curId)
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        for (formUpload in formUploads) {
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
                        text = formUpload.name,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (formUpload.uploaded) {
                        Text(
                            text = getDateTimeInstance().format(formUpload.timestamp.toDate()),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Download submission",
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.clickable {
                                // Handle the link click action, e.g., opening the URL.
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formUpload.link))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                ctx.startActivity(intent)
                            }
                        )
                    } else {
                        Text(
                            text = "Not yet submitted",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            curId = formUpload.formId; pickFileLauncher.launch("application/*")
                        },
                        colors = ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Text(if (formUpload.uploaded) "Reupload" else "Upload")
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}