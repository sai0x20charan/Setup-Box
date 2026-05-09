package com.charan.setupBox.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.charan.setupBox.presentation.addChannel.AddChannelScreen
import com.charan.setupBox.presentation.home.HomeScreen
import com.charan.setupBox.presentation.login.LoginScreen
import com.charan.setupBox.presentation.settings.aboutapp.AboutAppScreen
import com.charan.setupBox.presentation.settings.aboutapp.LicenseScreen
import com.charan.setupBox.presentation.settings.account.AccountScreen
import com.charan.setupBox.presentation.settings.settings.SettingsScreen

@Composable
fun NavigationAppHost(
    navHostController: NavHostController,
    sharedURL : String?,
    isLoggedIn : Boolean?,
) {

    LaunchedEffect(sharedURL) {
        if(!sharedURL.isNullOrBlank()){
            navHostController.navigate(AddNewChannelScreenNav(id = null , sharedURL))
        }
    }
    NavHost(
        navController = navHostController,

        startDestination =if(isLoggedIn==false) LoginScreenNav else HomeScreenNav(sharedURL),
        enterTransition = {
            fadeIn() + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                initialOffset = { 100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )
        },
        exitTransition = {
            fadeOut() + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                targetOffset = { -100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )
        },
        popEnterTransition = {
            fadeIn() + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                initialOffset = { -100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )
        },
        popExitTransition = {
            fadeOut() + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                targetOffset = { 100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )
        },
    ){
        composable<HomeScreenNav> {
            HomeScreen(
                navigateToSettings = { navHostController.navigate(SettingsScreenNav) },
                navigateToAddChannel = { id, channelLink ->
                    navHostController.navigate(
                        AddNewChannelScreenNav(
                            id = id,
                            channelLink = ""
                        )
                    )

                },
            )
        }
        composable<AddNewChannelScreenNav>(
        ) {
            AddChannelScreen(
                navigateBack = {
                    navHostController.popBackStack()
                }
            )
        }
        composable<LoginScreenNav> {
            LoginScreen(
                navigateToHomeScreen = {
                    navHostController.navigate(HomeScreenNav(sharedURL))
                }

            )
        }

        composable<SettingsScreenNav> {
            SettingsScreen(
                navigateToAccountScreen = {
                    navHostController.navigate(AccountScreenNav)
                },
                navigateToAboutAppScreen = {
                    navHostController.navigate(AboutAppNav)
                },
                navigateBack = {
                    navHostController.popBackStack()
                },
                viewModel = hiltViewModel(it)
            )
        }
        composable<AccountScreenNav>(){
            val settingsEntry = remember(it) {
                navHostController.getBackStackEntry(SettingsScreenNav)
            }

            AccountScreen(
                viewModel = hiltViewModel(settingsEntry),
                navigateToBack = {
                    navHostController.popBackStack()
                }

            )
        }
        composable<AboutAppNav> {

            AboutAppScreen(navHostController = navHostController)
        }
        composable<LicenseScreenNav> {
            LicenseScreen(navHostController = navHostController)
        }

    }
}
