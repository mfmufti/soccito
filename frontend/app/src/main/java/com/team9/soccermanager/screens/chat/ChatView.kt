package com.team9.soccermanager.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.team9.soccermanager.model.accessor.Message

@Composable
fun ChatView(
    chatID: String,
    fullname: String,
    viewModel: ChatViewModel = ChatViewModel(chatID),
    switchToWelcome: () -> Unit,
    switchBack: () -> Unit,
    goToHome: () -> Unit,
    goToRoster: () -> Unit,
    goToSchedule: () -> Unit
) {
//    var teamName by remember { mutableStateOf("") }
//    viewModel.getTeamName { teamName = it }

    val messages = remember { viewModel.getMessages() }
    val loading by remember { viewModel.isLoading() }

    Scaffold (
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = switchBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back",
                    )
                }
                Text(text = fullname)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (loading) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(60.dp))
                    }
                } else {
                    Chat(
                        paddingValues = paddingValues,
                        messages = messages,
                        viewModel = viewModel
                    )
                }
            }
        }
    )
}

@Composable
fun Chat(paddingValues: PaddingValues, messages: MutableList<Message>, viewModel: ChatViewModel) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.size != 0) {
            listState.scrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(paddingValues)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 0.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState,
            reverseLayout = true
        ) {
            items(messages.size) { index ->
                val m = messages[messages.size - index - 1]
                val arrangement = if (m.right) Arrangement.End else Arrangement.Start
                val color = if (m.right) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.inverseOnSurface

                Row(
                    horizontalArrangement = arrangement,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(color)
                            .fillMaxWidth(0.8f)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = m.text,
                            fontSize = 16.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 6.dp, 10.dp, 6.dp)
                .heightIn(42.dp, 74.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1.0f),
                verticalAlignment = Alignment.CenterVertically,
                //                        horizontalArrangement = Arrangement.SpaceBetween
            ) {
                var newText by remember { mutableStateOf("") }

                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .heightIn(36.dp, 68.dp)
                        .background(MaterialTheme.colorScheme.inverseOnSurface)
                        .padding(10.dp),
                ) {
                    BasicTextField(
                        value = newText,
                        onValueChange = { newText = it },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth(),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = {
                        if (newText.isNotEmpty()) {
                            viewModel.sendMessage(newText)
                            newText = ""
                        }
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.inversePrimary, CircleShape)
                        .height(36.dp)
                        .width(36.dp)
                ) {
                    Icon(
                        contentDescription = "Chat",
                        imageVector = Icons.AutoMirrored.Filled.Send,
                    )
                }
            }
        }
    }
}

