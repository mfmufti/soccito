package com.team9.soccermanager.screens.playerhome

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
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
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.ui.composable.BarsWrapper
import java.text.DateFormat.getDateTimeInstance
import java.util.Date

@Composable
fun PlayerHomeView(
    viewModel: PlayerHomeViewModel = remember { PlayerHomeViewModel() },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
    goToLeagueStandings: () -> Unit,
) {
    var teamName by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var joinCode by remember { mutableStateOf("") }

    val ctx = LocalContext.current
    val contentResolver = ctx.contentResolver
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                // The user selected a file, you can now open it or read from it
                val mimeType = contentResolver.getType(uri)
                if (mimeType == "application/pdf" || mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {
                    // The file is a PDF or DOCX, proceed with the upload
                    viewModel.uploadForm(it, contentResolver)
                } else {
                    Toast.makeText(ctx, "Invalid Form Type (must be PDF or DOCX).", Toast.LENGTH_SHORT).show()
                }

            }
        }
    )

    viewModel.getTeam {
        viewModel.announcements.value = it.announcements.toList()
    }

    //viewModel.getTeamName { teamName = it }
    viewModel.getFullName { fullname = it }

    viewModel.getJoinCode { joinCode = it }

    BarsWrapper(
        title = "Home",
        activeScreen = MainScreens.HOME,
        signOut = { viewModel.signOut(); switchToWelcome() },
        switchMainScreen = switchMainScreen,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Welcome $fullname", style = TextStyle(fontSize = 30.sp))
            Text(text = "You are a ${GS.user!!.type}", style = TextStyle(fontSize = 30.sp)) // test type

            Spacer(modifier = Modifier.height(15.dp))

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
                    if(viewModel.announcements.value == null) Text( text = "", style = TextStyle(fontSize = 16.sp))
                    else LazyColumn(Modifier.fillMaxWidth()) {
                        items(viewModel.announcements.value!!.reversed()) {
                                announcement -> ListItem(
                            headlineContent = { Text(announcement.content) },
                            supportingContent =
                            { Text("~ ${announcement.authorName} | ${
                                getDateTimeInstance().format(
                                    Date(announcement.datePosted)
                                )}") }
                        )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = "Upcoming Game shown here")

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = goToLeagueStandings,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(100.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        contentDescription = "Standings",
                        imageVector = Icons.AutoMirrored.Filled.List,
                        modifier = Modifier.size(90.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "League Standings",
                        fontSize = 24.sp,
                    )
                }
            }

            Button(
                onClick = { pickFileLauncher.launch("application/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Upload a Form")
            }

            Spacer(modifier = Modifier.height(5.dp))

            if (joinCode.isNotEmpty()) {
                SelectionContainer {
                    Text(
                        text = joinCode,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}