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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.team9.soccermanager.model.Account
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.screens.adminhome.AdminHomeView
import com.team9.soccermanager.screens.chatselect.ChatSelectView
import com.team9.soccermanager.screens.coachhome.CoachHomeView
import com.team9.soccermanager.screens.coachhome.forms.CoachHomeFormsView
import com.team9.soccermanager.ui.theme.SoccerManagerTheme
import com.team9.soccermanager.screens.login.LoginView
import com.team9.soccermanager.screens.register.RegisterView
import com.team9.soccermanager.screens.home.HomeView
import com.team9.soccermanager.screens.newadmin.NewAdminView
import com.team9.soccermanager.screens.newcoach.NewCoachView
import com.team9.soccermanager.screens.newplayer.NewPlayerView
import com.team9.soccermanager.screens.chat.ChatView
import com.team9.soccermanager.screens.chat.ChatViewModel
import com.team9.soccermanager.screens.coachroster.CoachRosterView
import com.team9.soccermanager.screens.gameschedule.GameScheduleView
import com.team9.soccermanager.screens.typeselect.TypeSelectView
import com.team9.soccermanager.screens.welcome.WelcomeView
import com.team9.soccermanager.screens.playerhome.PlayerHomeView
import com.team9.soccermanager.screens.playerroster.PlayerRosterView
import com.team9.soccermanager.screens.rankings.RankingsView
import com.team9.soccermanager.screens.loadscreen.LoadView
import com.team9.soccermanager.screens.playerspecificgame.PlayerSpecificGameView
import kotlinx.serialization.Serializable

@Serializable object LoadScreen
@Serializable object WelcomeScreen
@Serializable object LoginScreen
@Serializable data class RegisterScreen(
    var type: String,
    var leagueName: String = "",
    var leagueCode: String = "",
    var teamName: String = "",
    var teamCode: String = ""
)
@Serializable object TypeSelectScreen
@Serializable object NewAdminScreen
@Serializable object NewCoachScreen
@Serializable object NewPlayerScreen
@Serializable object HomeScreen
@Serializable object RosterScreen
@Serializable object PlayerHomeScreen
@Serializable object CoachHomeScreen
@Serializable object AdminHomeScreen
@Serializable object CoachHomeScreenForms
@Serializable object LeagueStandingsScreen
@Serializable object PlayerGameScheduleScreen
@Serializable object PlayerRosterScreen
@Serializable data class ChatScreen(var chatID: String, var fullname: String)
@Serializable object ChatSelectScreen
@Serializable object CoachRosterScreen
@Serializable data class PlayerSpecificGameScreen(val index: Int)

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

    val RosterScreen: () -> Any = {
        if (GS.user!!.type == "player") {
            PlayerRosterScreen
        } else {
            CoachRosterScreen
        }
    }

    val HomeScreen: () -> Any = {
        if (GS.user!!.type == "coach") {
            CoachHomeScreen
        } else if (GS.user!!.type == "player") {
            PlayerHomeScreen
        } else {
            AdminHomeScreen
        }
    }

    val switchMainScreen = { newScreen: MainScreens ->
        if (newScreen == MainScreens.BACK) {
            nav.pop()
        } else {
            nav.clearSwitch(when (newScreen) {
                MainScreens.ROSTER -> RosterScreen()
                MainScreens.CHAT -> ChatSelectScreen
                MainScreens.SCHEDULE -> PlayerGameScheduleScreen
                else -> HomeScreen()
            })
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
            )
        }
        composable<RegisterScreen> { backStackEntry ->
            val data: RegisterScreen = backStackEntry.toRoute()
            RegisterView(
                type = data.type,
                other = mapOf(
                    "leagueName" to data.leagueName,
                    "leagueCode" to data.leagueCode,
                    "teamName" to data.teamName,
                    "teamCode" to data.teamCode,
                ),
                switchBack = { nav.pop() },
                switchToLogin = { nav.popSwitch(LoginScreen, WelcomeScreen) },
                switchToHome = { nav.clearSwitch(HomeScreen()) }
            )
        }
        composable<TypeSelectScreen> {
            TypeSelectView(
                switchBack = { nav.pop() },
                switchToPlayer = { nav.switch(NewPlayerScreen) },
                switchToAdmin = { nav.switch(NewAdminScreen) },
                switchToCoach = { nav.switch(NewCoachScreen) }
            )
        }
        composable<NewAdminScreen> {
            NewAdminView(
                switchToRegister = { leagueName ->
                    nav.switch(RegisterScreen("admin", leagueName = leagueName))
                }, switchBack = { nav.pop() }
            )
        }
        composable<NewCoachScreen> {
            NewCoachView(
                switchToRegister = { leagueCode, teamName ->
                    nav.switch(RegisterScreen("coach", leagueCode = leagueCode, teamName = teamName))
                }, switchBack = { nav.pop() }
            )
        }
        composable<NewPlayerScreen> {
            NewPlayerView(
                switchToRegister = { teamCode ->
                    nav.switch(RegisterScreen("player", teamCode = teamCode))
                }, switchBack = { nav.pop() }
            )
        }
        composable<HomeScreen> {
            HomeView(
                switchToWelcome = { nav.clearSwitch(HomeScreen()) }
            )
        }
        composable<PlayerHomeScreen> {
            PlayerHomeView(
                viewModel = viewModel(),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                goToLeagueStandings = { nav.switch(LeagueStandingsScreen) },
            )
        }
        composable<CoachHomeScreen> {
            CoachHomeView(
                viewModel = viewModel(),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                goToLeagueStandings = { nav.switch(LeagueStandingsScreen) },
                goToForms = { nav.switch(CoachHomeScreenForms) }
            )
        }
        composable<CoachHomeScreenForms> {
            CoachHomeFormsView(
                switchBack = { nav.pop() }
            )
        }
        composable<AdminHomeScreen> {
            AdminHomeView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                goToLeagueStandings = { nav.switch(LeagueStandingsScreen) },
            )
        }
        composable<PlayerGameScheduleScreen> {
            GameScheduleView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                goToSpecificGame = { index -> nav.switch(PlayerSpecificGameScreen(index)) },
            )
        }
        composable<PlayerSpecificGameScreen> { backStackEntry ->
            val data: PlayerSpecificGameScreen = backStackEntry.toRoute()
            PlayerSpecificGameView(
                gameIndex = data.index,
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
            )
        }
        composable<PlayerRosterScreen> {
            PlayerRosterView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
            )
        }
        composable<CoachRosterScreen> {
            CoachRosterView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen
            )
        }
        composable<ChatScreen> { backStackEntry ->
            val data: ChatScreen = backStackEntry.toRoute()
            ChatView(
                viewModel = viewModel(factory = viewModelFactory { initializer { ChatViewModel(data.chatID) } }),
                chatID = data.chatID,
                fullname = data.fullname,
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
            )
        }
        composable<LeagueStandingsScreen> {
            RankingsView(
                switchBack = { nav.pop() }
            )
        }
        composable<ChatSelectScreen> {
            ChatSelectView(
                viewModel = viewModel(),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                goToChat = { chatID, fullname -> nav.switch(ChatScreen(chatID, fullname))},
            )
        }
    }
}