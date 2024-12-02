package com.team9.soccermanager.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.team9.soccermanager.PlayerAnnouncementsScreen
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.model.MenuScreens
import kotlinx.coroutines.launch

/* This file contains the BarsWrapper composable functions for UI elements used in the
 application. This composable is used throughout the application to provide common elements including
  navigation bars, navigation drawers, and notification buttons. */

@Composable
fun BarsWrapper(
    title: String = "",
    activeScreen: MainScreens,
    signOut: () -> Unit,
    allowBack: Boolean = false,
    switchMainScreen: (MainScreens) -> Unit,
    switchMenuScreen: (MenuScreens) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val showDialog = remember { mutableStateOf(false) }

    Scaffold (
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar (
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
        }
    ) { padding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        ModalDrawerSheet(
                            modifier = Modifier.fillMaxWidth(0.65f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .aspectRatio(1f)
                                        .padding(10.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "User icon",
                                        modifier = Modifier.padding(15.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(13.dp))
                                Text(text = "Welcome", fontWeight = FontWeight(255))
                                Text(
                                    text = GS.user!!.fullname,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                                HorizontalDivider(thickness = 2.dp)
                                Spacer(modifier = Modifier.height(15.dp))

                                DrawerMenuItem(Icons.Default.AccountBox, "Profile") { switchMenuScreen(MenuScreens.PROFILE) }
                                DrawerMenuItem(Icons.Default.ManageAccounts, "Account") { switchMenuScreen(MenuScreens.ACCOUNT) }

                                Spacer(modifier = Modifier.height(13.dp))
                                HorizontalDivider(thickness = 2.dp)
                                Spacer(modifier = Modifier.fillMaxHeight(0.1f))

                                if (GS.user!!.type != "player") {
                                    val clipboard: ClipboardManager = LocalClipboardManager.current
                                    Button(
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(248, 190, 92), contentColor = Color.Black),
                                        onClick = {
                                            clipboard.setText(AnnotatedString(if (GS.user!!.type == "coach") GS.user!!.teamID else GS.user!!.leagueID))
                                            scope.launch {
                                                snackbarHostState.showSnackbar(message = "Code copied to clipboard!", duration = SnackbarDuration.Short)
                                            }
                                        },
                                        modifier = Modifier
                                            .padding(start = 16.dp, end = 16.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(text = if (GS.user!!.type == "admin") "Get league code" else "Get team code")
                                    }
                                }

                                Button(
                                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                                    onClick = { showDialog.value = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(201, 98, 98), contentColor = Color.White)
                                ) {
                                    Row {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Logout,
                                            contentDescription = "Sign out"
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = "Sign Out")
                                    }
                                }

                            }
                        }
                    }
                },
                content = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Scaffold(
                            modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
                            topBar = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (showDialog.value) {
                                        ShowLogOutWarning(signOut = signOut, dismiss = { showDialog.value = false })
                                    }

                                    if (allowBack) {
                                        IconButton(onClick = { switchMainScreen(MainScreens.BACK) }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "back",
                                            )
                                        }
                                    }
                                    Text(text = title)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (GS.user!!.type != "admin") { AnnouncementNotificationsButton(onClick = {}) }
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    if (drawerState.isClosed) drawerState.open()
                                                    else drawerState.close()
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.Menu, contentDescription = "Open Menu")
                                        }
                                    }
                                }
                            },
                            bottomBar = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp, 0.dp, 8.dp, 0.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { switchMainScreen(MainScreens.HOME) }) {
                                        Icon(
                                            contentDescription = "Home",
                                            imageVector = Icons.Filled.Home,
                                            tint = getTint(MainScreens.HOME, activeScreen)
                                        )
                                    }
                                    IconButton(onClick = { switchMainScreen(MainScreens.SCHEDULE) }) {
                                        Icon(
                                            contentDescription = "Schedule",
                                            imageVector = Icons.Filled.DateRange,
                                            tint = getTint(MainScreens.SCHEDULE, activeScreen)
                                        )
                                    }
                                    if (GS.user!!.type != "admin") {
                                        IconButton(onClick = { switchMainScreen(MainScreens.ROSTER) }) {
                                            Icon(
                                                contentDescription = "Roster",
                                                imageVector = Icons.Filled.Person,
                                                tint = getTint(MainScreens.ROSTER, activeScreen)
                                            )
                                        }
                                    }
                                    IconButton(onClick = { switchMainScreen(MainScreens.CHAT) }) {
                                        Icon(
                                            contentDescription = "Chat",
                                            imageVector = Icons.Filled.Forum,
                                            tint = getTint(MainScreens.CHAT, activeScreen)
                                        )
                                    }
                                }
                            },
                            content = content
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun getTint(curScreen: MainScreens, activeScreen: MainScreens): Color {
    return if (curScreen == activeScreen) {
        MaterialTheme.colorScheme.surfaceTint
    } else {
        LocalContentColor.current
    }
}

@Composable
private fun DrawerMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowLogOutWarning(signOut: () -> Unit, dismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = dismiss, // Closes the dialog when clicked outside
        confirmButton = {
            TextButton(onClick = { dismiss(); signOut() }) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text("No")
            }
        },
        title = { Text(text = "Confirm Sign-Out") },
        text = { Text("Are you sure you want to exit Soccito?") }
    )
}

@Composable
fun AnnouncementNotificationsButton(
    onClick: () -> Unit
) {

    val notificationState = GS.notificationState.collectAsState()

    IconButton(onClick = {
        if (GS.user != null && GS.user!!.type == "player") {
            GS.nav?.switch(PlayerAnnouncementsScreen)
        }
    } ) {
        BadgedBox(
            badge = {
                if (notificationState.value) {
                    Badge(
                        contentColor = Color.White,
                        modifier = Modifier.size(8.dp)
                    ) {}
                }
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
            )
        }
    }
}