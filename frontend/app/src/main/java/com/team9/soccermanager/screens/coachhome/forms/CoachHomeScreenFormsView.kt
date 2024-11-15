package com.team9.soccermanager.screens.coachhome.forms

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
import androidx.compose.ui.unit.*
import com.team9.soccermanager.model.FormFile
import java.text.DateFormat.getDateTimeInstance
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachHomeScreenFormsView(
    viewModel: CoachHomeScreenFormsViewModel = CoachHomeScreenFormsViewModel(),
    switchBack: () -> Unit
) {

    val ctx = LocalContext.current

    var uploads by remember { mutableStateOf<List<FormFile>>(listOf()) }
    viewModel.getTeam {
        uploads = it.forms.toList()
    }
    Scaffold (
        modifier = Modifier.padding(16.dp),
        topBar = {
            TopAppBar(
                title = {},
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
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(uploads) { upload ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = upload.fileName, style = MaterialTheme.typography.titleMedium)
                        Text(text = getDateTimeInstance().format(Date(upload.datePosted)), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text(
                            text = "Download",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Blue),
                            modifier = Modifier.clickable {
                                // Handle the link click action, e.g., opening the URL.
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upload.downloadUrl))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                ctx.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}