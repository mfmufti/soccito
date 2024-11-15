package com.team9.soccermanager

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.team9.soccermanager.model.Account
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.screens.chatselect.ChatSelectView
import com.team9.soccermanager.screens.coachhome.CoachHomeScreenView
import com.team9.soccermanager.screens.coachhome.forms.CoachHomeScreenFormsView
import com.team9.soccermanager.ui.theme.SoccerManagerTheme
import com.team9.soccermanager.screens.login.LoginView
import com.team9.soccermanager.screens.register.RegisterView
import com.team9.soccermanager.screens.home.HomeView
import com.team9.soccermanager.screens.newadmin.NewAdminView
import com.team9.soccermanager.screens.newcoach.NewCoachView
import com.team9.soccermanager.screens.newplayer.NewPlayerView
import com.team9.soccermanager.screens.chat.ChatView
import com.team9.soccermanager.screens.playergameschedule.PlayerGameScheduleView
import com.team9.soccermanager.screens.typeselect.TypeSelectView
import com.team9.soccermanager.screens.welcome.WelcomeView
import com.team9.soccermanager.screens.playerhome.PlayerHomeScreenView
import com.team9.soccermanager.screens.playerroster.PlayerRosterView
import com.team9.soccermanager.screens.rankings.RankingsView
import com.team9.soccermanager.screens.loadscreen.LoadView
import com.team9.soccermanager.screens.playerspecificgame.PlayerSpecificGameView
import kotlinx.serialization.Serializable

@Serializable object LoadScreen
@Serializable object WelcomeScreen
@Serializable object LoginScreen
@Serializable data class RegisterScreen(var type: String)
@Serializable object TypeSelectScreen
@Serializable object NewAdminScreen
@Serializable object NewCoachScreen
@Serializable object NewPlayerScreen
@Serializable object HomeScreen
@Serializable object PlayerHomeScreen
@Serializable object CoachHomeScreen
@Serializable object CoachHomeScreenForms
@Serializable object LeagueStandingsScreen
@Serializable object PlayerGameScheduleScreen
@Serializable object PlayerRosterScreen
@Serializable data class ChatScreen(var chatID: String, var fullname: String)
@Serializable object ChatSelectScreen
@Serializable object PlayerSpecificGameScreen

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContent {
            SoccerManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    App()
                }
            }
        }
    }
}

class Navigator(val navController: NavHostController) {
    fun switch(dest: Any) {
        navController.navigate(route = dest)
    }
    fun popSwitch(dest: Any, top: Any) {
        navController.navigate(dest) {
            popUpTo(top) { inclusive = false }
        }
    }
    fun pop() {
        navController.popBackStack()
    }
    fun clearSwitch(dest: Any) {
        navController.navigate(dest) {
            popUpTo(0) { inclusive = true }
        }
    }
}

@Composable
fun App(navController: NavHostController = rememberNavController()) {

    //start = LeagueStandingsScreen // For debug purposes
    // First check if authenticated user is player, coach, admin, and guide them
    // to the appropriate screen. If its player, go to playerHomeScreen upon login and register

    val nav = remember(navController) { Navigator(navController) }
    var start by remember { mutableStateOf<Any>(LoadScreen) }

    if (Account.isLoggedIn()) {
        Account.setupGS {
            start = when(GS.user?.type) {
                "admin" -> PlayerHomeScreen
                "player" -> PlayerHomeScreen
                else -> CoachHomeScreen
            }
        }
    } else {
        start = WelcomeScreen
    }

    val HomeScreen: () -> Any = {
        if (GS.user!!.type == "coach") {
            CoachHomeScreen
        } else {
            PlayerHomeScreen
        }
    }

    NavHost(navController = navController, startDestination = start) {
        composable<LoadScreen> {
            LoadView()
        }
        composable<WelcomeScreen> {
            WelcomeView(
                switchToLogin = { nav.switch(LoginScreen) },
                switchToRegister = { nav.switch(TypeSelectScreen) }
            )
        }
        composable<LoginScreen> {
            LoginView(
                switchBack = { nav.pop() },
                switchToRegister = { nav.popSwitch(TypeSelectScreen, WelcomeScreen) },
                switchToSpecific = { nav.clearSwitch(HomeScreen()) },
                // For testing player screen, uncomment below line (and comment above line) and login as player:
                // email: pt1@test.com, pwd: abc123
                // switchToHome = { nav.clearSwitch(PlayerHomeScreen) }
            )
        }
        composable<RegisterScreen> { backStackEntry ->
            val data: RegisterScreen = backStackEntry.toRoute()
            RegisterView(
                type = data.type,
                switchBack = { nav.pop() },
                switchToLogin = { nav.popSwitch(LoginScreen, WelcomeScreen) },
                switchToSpecific = {
                    if (it == "player") {
                        nav.switch(NewPlayerScreen)
                    } else if (it == "admin") {
                        nav.switch(NewAdminScreen)
                    } else {
                        nav.switch(NewCoachScreen)
                    }
                }
            )
        }
        composable<TypeSelectScreen> {
            TypeSelectView(
                switchBack = { nav.pop() },
                switchToPlayer = { nav.switch(RegisterScreen("player")) },
                switchToCoach = { nav.switch(RegisterScreen("coach")) },
                switchToAdmin = { nav.switch(RegisterScreen("admin")) }
            )
        }
        composable<NewAdminScreen> {
            NewAdminView(
                switchToHome = { nav.clearSwitch(HomeScreen()) },
                switchBack = { nav.pop() }
            )
        }
        composable<NewCoachScreen> {
            NewCoachView(
                switchToHome = { nav.clearSwitch(HomeScreen()) },
                switchBack = { nav.pop() }
            )
        }
        composable<NewPlayerScreen> {
            NewPlayerView(
                switchToHome = { nav.clearSwitch(HomeScreen()) },
                switchBack = { nav.pop() }
            )
        }
        composable<HomeScreen> {
            HomeView(
                switchToWelcome = { nav.clearSwitch(HomeScreen()) }
            )
        }
        composable<PlayerHomeScreen> {
            PlayerHomeScreenView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                goToLeagueStandings = { nav.switch(LeagueStandingsScreen) },
                goToSchedule = { nav.clearSwitch(PlayerGameScheduleScreen) },
                goToRoster = { nav.clearSwitch(PlayerRosterScreen) },
                goToChatSelect = { nav.clearSwitch(ChatSelectScreen) }
            )
        }
        composable<CoachHomeScreen> {
            CoachHomeScreenView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                goToLeagueStandings = { nav.switch(LeagueStandingsScreen) },
                goToSchedule = { nav.clearSwitch(PlayerGameScheduleScreen) },
                goToRoster = { nav.clearSwitch(PlayerRosterScreen) },
                goToChatSelect = { nav.clearSwitch(ChatSelectScreen) },
                goToForms = { nav.switch(CoachHomeScreenForms) }
            )
        }
        composable<CoachHomeScreenForms> {
            CoachHomeScreenFormsView(
                switchBack = { nav.pop() }
            )
        }
        composable<PlayerGameScheduleScreen> {
            PlayerGameScheduleView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                goToSpecificGame = { nav.clearSwitch(PlayerSpecificGameScreen) },
                goToHome = { nav.clearSwitch(HomeScreen()) },
                goToRoster = { nav.clearSwitch(PlayerRosterScreen) },
                goToChatSelect = { nav.clearSwitch(ChatSelectScreen) }
            )
        }
        composable<PlayerSpecificGameScreen> {
            PlayerSpecificGameView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                goToHome = { nav.clearSwitch(HomeScreen()) },
                goToSchedule = { nav.clearSwitch(PlayerGameScheduleScreen) },
                goToRoster = { nav.clearSwitch(PlayerRosterScreen) },
                goToChat = { nav.clearSwitch(ChatScreen) }
            )
        }
        composable<PlayerRosterScreen> {
            PlayerRosterView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                goToHome = { nav.clearSwitch(HomeScreen()) },
                goToSchedule = { nav.clearSwitch(PlayerGameScheduleScreen) },
                goToChatSelect = { nav.clearSwitch(ChatSelectScreen) }
            )
        }
        composable<ChatScreen> { backStackEntry ->
            val data: ChatScreen = backStackEntry.toRoute()
            ChatView(
                chatID = data.chatID,
                fullname = data.fullname,
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchBack = { nav.pop() },
                goToHome = { nav.clearSwitch(HomeScreen()) },
                goToSchedule = { nav.clearSwitch(PlayerGameScheduleScreen) },
                goToRoster = { nav.clearSwitch(PlayerRosterScreen) },
            )
        }
        composable<LeagueStandingsScreen> {
            RankingsView(
                switchBack = { nav.pop() }
            )
        }
        composable<ChatSelectScreen> {
            ChatSelectView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                goToChat = { chatID, fullname -> nav.switch(ChatScreen(chatID, fullname))},
                goToHome = { nav.clearSwitch(HomeScreen()) },
                goToSchedule = { nav.clearSwitch(PlayerGameScheduleScreen) },
                goToRoster = { nav.clearSwitch(PlayerRosterScreen) },
            )
        }
    }
}