package com.dune.appcache

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dune.appcache.data.SettingsRepository
import com.dune.appcache.ui.screens.AboutScreen
import com.dune.appcache.ui.screens.DataUsageScreen
import com.dune.appcache.ui.screens.HomeScreen
import com.dune.appcache.ui.screens.SettingsScreen
import com.dune.appcache.ui.theme.AccentGreen
import com.dune.appcache.ui.theme.AppBackground
import com.dune.appcache.ui.theme.AppCacheTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsRepository = SettingsRepository(applicationContext)

        setContent {
            val settings by settingsRepository.settings.collectAsState(initial = com.dune.appcache.data.AppSettings())
            val accentColor = if (settings.accentColorArgb != 0) Color(settings.accentColorArgb) else AccentGreen

            AppCacheTheme(accentColor = accentColor) {
                Surface(
                    modifier = Modifier.fillMaxSize().background(AppBackground),
                    color = AppBackground,
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onOpenSettings = { navController.navigate("settings") },
                                onOpenDataUsage = {
                                    navController.navigate("data") {
                                        popUpTo("home")
                                        launchSingleTop = true
                                    }
                                },
                            )
                        }
                        composable("data") {
                            DataUsageScreen(
                                onOpenSettings = { navController.navigate("settings") },
                                onOpenCache = {
                                    navController.navigate("home") {
                                        popUpTo("home")
                                        launchSingleTop = true
                                    }
                                },
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                onOpenAbout = { navController.navigate("about") },
                            )
                        }
                        composable("about") {
                            AboutScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
