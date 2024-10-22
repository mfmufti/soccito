package com.team9.soccermanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team9.soccermanager.ui.theme.SoccerManagerTheme
import com.team9.soccermanager.screens.login.LoginView
import com.team9.soccermanager.screens.register.RegisterView
import com.team9.soccermanager.screens.welcome.WelcomeView
import kotlinx.serialization.Serializable

@Serializable object LoginScreen
@Serializable object RegisterScreen
@Serializable object WelcomeScreen

class MainActivity : ComponentActivity() {
    // Temporary measures
    @SuppressLint("StaticFieldLeak")
    companion object {
        var context : Context? = null
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = baseContext
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

    override fun onDestroy() {
        super.onDestroy()
        context = null
    }
}

@Composable
fun App(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = LoginScreen) {
        composable<LoginScreen> {
            LoginView(
                switchToRegister = { navController.navigate(route = RegisterScreen) },
                switchToWelcome = { navController.navigate(route = WelcomeScreen) }
            )
        }
        composable<RegisterScreen> {
            RegisterView(
                switchToLogin = { navController.navigate(route = LoginScreen) },
                switchToWelcome = { navController.navigate(route = WelcomeScreen) }
            )
        }
        composable<WelcomeScreen> {
            WelcomeView()
        }
    }
}