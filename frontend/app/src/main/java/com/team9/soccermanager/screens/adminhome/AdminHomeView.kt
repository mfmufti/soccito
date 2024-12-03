package com.team9.soccermanager.screens.adminhome

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
import com.team9.soccermanager.model.MenuScreens
import com.team9.soccermanager.screens.gameschedule.GameScheduleView
import com.team9.soccermanager.ui.composable.BarsWrapper
import java.text.DateFormat.getDateTimeInstance
import java.util.Date

@Composable
fun AdminHomeView(
    viewModel: AdminHomeViewModel = remember { AdminHomeViewModel() },
    switchToWelcome: () -> Unit,
    switchMainScreen: (MainScreens) -> Unit,
    switchMenuScreen: (MenuScreens) -> Unit,
    goToLeagueStandings: () -> Unit,
    goToSpecificGame: (Int) -> Unit,
) {
    var fullname by remember { mutableStateOf("") }

    viewModel.getFullName { fullname = it }

    BarsWrapper(
        title = "Home",
        activeScreen = MainScreens.HOME,
        signOut = { viewModel.signOut(); switchToWelcome() },
        switchMainScreen = switchMainScreen,
        switchMenuScreen = switchMenuScreen
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Hello $fullname", style = TextStyle(fontSize = 30.sp), textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp))
            Spacer(modifier = Modifier.height(15.dp))

            Box(modifier = Modifier.padding(16.dp)) {
                GameScheduleView(goToSpecificGame = goToSpecificGame, singleGame = true)
            }

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

            Spacer(modifier = Modifier.height(5.dp))

        }
    }
}