package com.team9.soccermanager.screens.chatselect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.team9.soccermanager.model.GS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSelectView(
    viewModel: ChatSelectViewModel = remember { ChatSelectViewModel() },
    switchToWelcome: () -> Unit,
    goToChat: (String, String) -> Unit,
    goToHome: () -> Unit,
    goToSchedule: () -> Unit,
    goToRoster: () -> Unit
) {
    var teamName by remember { mutableStateOf("") }
    viewModel.getTeamName { teamName = it }

    val chats = remember { viewModel.getChats() }
    val loading by remember { viewModel.isLoading() }
    val error by remember { viewModel.isError() }
    val errorLoadingChat by remember { viewModel.isErrorLoadingChat() }

//    DisposableEffect() {
//        onDispose {
//            println("ok at least it should happen here, right?")
//        }
//    }

    Scaffold (
        topBar =  {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = teamName)
                Button(
                    onClick = { viewModel.signOut(); switchToWelcome() },
                    modifier = Modifier.size(100.dp, 36.dp),
                    contentPadding = PaddingValues(3.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )) {
                    Text(text = "Sign Out")
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 0.dp, 8.dp, 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = goToHome) {
                    Icon(contentDescription = "Home", imageVector = Icons.Filled.Home)
                }
                IconButton(onClick = goToSchedule) {
                    Icon(contentDescription = "Schedule", imageVector = Icons.Filled.DateRange)
                }
                IconButton(onClick = goToRoster) {
                    Icon(contentDescription = "Roster", imageVector = Icons.Filled.Person)
                }
                IconButton(onClick = {}) {
                    Icon(contentDescription = "Chat", imageVector = Icons.Filled.Forum, tint = MaterialTheme.colorScheme.surfaceTint)
                }
            }
        },
        content = { paddingValues ->
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
                    if (error) {
                        Text(
                            text = "There was an error loading the chat list. Please check your network connection.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    } else if (loading) {
                        Box(modifier = Modifier.padding(20.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(60.dp))
                        }
                    } else if (chats.isEmpty()) {
                        Text(
                            text = "No users to chat with",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        for (index in 0..<chats.size) {
                            val chat = chats[index]
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = { viewModel.switchToChat(index, goToChat) },
                                shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = chat.name,
                                        fontSize = 30.sp
                                    )
                                    Text(
                                        text = chat.type,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            if (errorLoadingChat) {
                AlertDialog(
                    onDismissRequest = { viewModel.resetErrorLoadingChat() },
                    title = { Text(text = "Error loading chat") },
                    text = { Text(text = "There was an error loading that chat. Please check your connection.") },
                    confirmButton = {
                        TextButton(onClick = { viewModel.resetErrorLoadingChat() }) { Text("Ok") }
                    },
                )
            }
        }
    )
}