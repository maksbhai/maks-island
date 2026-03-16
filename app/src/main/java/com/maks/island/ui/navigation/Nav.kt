package com.maks.island.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maks.island.ui.screens.AboutScreen
import com.maks.island.ui.screens.AppFilterScreen
import com.maks.island.ui.screens.HomeScreen
import com.maks.island.ui.screens.OnboardingScreen
import com.maks.island.ui.screens.SettingsScreen
import com.maks.island.ui.screens.SplashScreen
import com.maks.island.viewmodel.IslandViewModel

object Routes {
    const val Splash = "splash"
    const val Onboarding = "onboarding"
    const val Home = "home"
    const val Settings = "settings"
    const val AppFilters = "app_filters"
    const val About = "about"
}

@Composable
fun MaksIslandNavHost(viewModel: IslandViewModel, navController: NavHostController = rememberNavController(), modifier: Modifier = Modifier) {
    val seen by viewModel.onboardingSeen.collectAsStateWithLifecycle()
    NavHost(navController = navController, startDestination = Routes.Splash, modifier = modifier) {
        composable(Routes.Splash) {
            SplashScreen(onDone = { navController.navigate(if (seen) Routes.Home else Routes.Onboarding) { popUpTo(Routes.Splash) { inclusive = true } } })
        }
        composable(Routes.Onboarding) {
            OnboardingScreen(
                onFinish = {
                    viewModel.completeOnboarding()
                    navController.navigate(Routes.Home) { popUpTo(Routes.Onboarding) { inclusive = true } }
                }
            )
        }
        composable(Routes.Home) {
            HomeScreen(viewModel,
                onSettings = { navController.navigate(Routes.Settings) },
                onAbout = { navController.navigate(Routes.About) })
        }
        composable(Routes.Settings) {
            SettingsScreen(viewModel,
                onBack = { navController.popBackStack() },
                onAppFilters = { navController.navigate(Routes.AppFilters) })
        }
        composable(Routes.AppFilters) {
            AppFilterScreen(viewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.About) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
