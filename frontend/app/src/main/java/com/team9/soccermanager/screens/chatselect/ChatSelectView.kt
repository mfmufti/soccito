package com.team9.soccermanager.screens.chatselect

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.model.MenuScreens
import com.team9.soccermanager.ui.composable.BarsWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSelectView(
    viewModel: ChatSelectViewModel = remember { ChatSelectViewModel() },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
    switchMenuScreen: (MenuScreens) -> Unit,
    goToChat: (String, String) -> Unit,
) {
    var teamName by remember { mutableStateOf("") }
    viewModel.getTeamName { teamName = it }

    val chats = remember { viewModel.getChats() }
    val loading by remember { viewModel.isLoading() }
    val error by remember { viewModel.isError() }
    val errorLoadingChat by remember { viewModel.isErrorLoadingChat() }

    BarsWrapper(
        title = "Your Chats",
        activeScreen = MainScreens.CHAT,
        signOut = { viewModel.signOut(); switchToWelcome() },
        switchMainScreen = switchMainScreen,
        switchMenuScreen = switchMenuScreen
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
                if (error) {
                    Text(
                        text = "There was an error loading the chat list. Please check your network connection.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
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
                title = { Text(
                    text = "Error loading chat",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                ) },
                text = { Text(
                    text = "There was an error loading that chat. Please check your connection.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                ) },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetErrorLoadingChat() }) { Text("Ok") }
                },
            )
        }
    }
}