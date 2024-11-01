package com.team9.soccermanager.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

@Composable
fun HomeView(
    switchToWelcome: () -> Unit,
    viewModel: HomeViewModel = HomeViewModel()
) {
    var username by remember { mutableStateOf("") }
    var teamStandings = remember { viewModel.getTeamStandings() }

    viewModel.getUserName { username = it }

    Scaffold(
        modifier = Modifier.padding(16.dp),
        topBar = {
            Text(
                text = "Welcome $username",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    ) { padding -> Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10))
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "League Standings:",
                        fontSize = 25.sp
                    )

                    for (i in 0..teamStandings.size - 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${i + 1}. ${teamStandings[i]}",
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upload a Form")
            }

            Button(
                onClick = { viewModel.signOut(); switchToWelcome() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }
        }
    }
}