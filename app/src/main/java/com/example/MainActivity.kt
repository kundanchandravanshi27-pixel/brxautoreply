package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.BizDarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BizRynoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BizRynoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BizDarkBackground
                ) {
                    BizRynoNavHost(viewModel)
                }
            }
        }
    }
}

@Composable
fun BizRynoNavHost(viewModel: BizRynoViewModel) {
    val navController = rememberNavController()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val startDest = if (isLoggedIn) "home" else "auth"

    NavHost(navController = navController, startDestination = startDest) {
        composable("auth") {
            AuthScreen(viewModel) {
                navController.navigate("permissions") {
                    popUpTo("auth") { inclusive = true }
                }
            }
        }
        composable("permissions") {
            PermissionWizardScreen {
                navController.navigate("home") {
                    popUpTo("permissions") { inclusive = true }
                }
            }
        }
        composable("home") {
            HomeScreen(viewModel) { route -> navController.navigate(route) }
        }
        composable("business_prompt") {
            BusinessPromptScreen(viewModel) { navController.popBackStack() }
        }
        composable("playground") {
            PlaygroundScreen(viewModel) { navController.popBackStack() }
        }
        composable("conversations") {
            ConversationsScreen(viewModel) { navController.popBackStack() }
        }
        composable("rules") {
            RulesScreen(viewModel) { navController.popBackStack() }
        }
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
