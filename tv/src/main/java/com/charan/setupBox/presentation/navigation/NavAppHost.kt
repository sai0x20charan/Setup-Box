package com.charan.setupBox.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.charan.setupBox.presentation.home.HomeScreen
import com.charan.setupBox.presentation.login.otp.OTPScreen
import com.charan.setupBox.presentation.login.login.LoginScreen
import com.charan.setupBox.presentation.settings.SettingsScreen

@Composable
fun NavAppHost(
    navHostController: NavHostController,
    isLoggedIn : Boolean
){
    NavHost(
        navController = navHostController,
        startDestination = if(isLoggedIn)HomeScreenNav else LoginScreenNav) {
        composable<HomeScreenNav> {
            HomeScreen(
                navigateToLoginScreen = {
                    navHostController.navigate(LoginScreenNav) {
                        popUpTo(HomeScreenNav) { inclusive = true }
                    }
                }

            )
        }
        composable<LoginScreenNav> { backStackEntry ->

            LoginScreen(

                navigateToOTPScreen = { email ->
                    navHostController.navigate(OTPScreenNav(email))
                }
            )
        }
        composable<OTPScreenNav> { backStackEntry ->

            OTPScreen(

                navigateToHomeScreen = {
                    navHostController.navigate(HomeScreenNav) {
                        popUpTo(LoginScreenNav) { inclusive = true }
                    }
                }
            )
        }
        composable<SettingsScreenNav> {
            SettingsScreen(navHostController = navHostController)
        }

    }
}
