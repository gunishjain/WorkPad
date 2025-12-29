package com.gunishjain.workpad.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gunishjain.workpad.ui.auth.AuthNavigationEvent
import com.gunishjain.workpad.ui.auth.login.LoginScreen
import com.gunishjain.workpad.ui.auth.signup.SignupScreen
import com.gunishjain.workpad.ui.home.HomeNavigationEvent
import com.gunishjain.workpad.ui.home.HomeScreen
import com.gunishjain.workpad.ui.notes.CreateNoteEvent
import com.gunishjain.workpad.ui.notes.CreateNoteScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {

        composable<LoginRoute> {
            LoginScreen(
                onNavigate = {
                    when(it) {
                        AuthNavigationEvent.NavigateToSignUp -> navController.navigate(SignUpRoute)
                        AuthNavigationEvent.NavigateBack -> navController.popBackStack()
                        AuthNavigationEvent.NavigateToHome -> navController.navigate(HomeRoute)
                        AuthNavigationEvent.NavigateToLogin -> Unit
                    }
                }
            )
        }

        composable<SignUpRoute> {
            SignupScreen(
                onNavigate = {
                    when(it) {
                        AuthNavigationEvent.NavigateToSignUp -> Unit
                        AuthNavigationEvent.NavigateBack -> navController.popBackStack()
                        AuthNavigationEvent.NavigateToHome -> navController.navigate(HomeRoute)
                        AuthNavigationEvent.NavigateToLogin -> navController.navigate(LoginRoute)
                    }
                }
            )
        }

        composable<HomeRoute> {
            HomeScreen(
                onNavigate = {
                    when(it) {
                        is HomeNavigationEvent.NavigateToCreateNote -> {
                            navController.navigate(CreateNoteRoute(parentId = it.parentId))
                        }
                        is HomeNavigationEvent.NavigateToNote -> TODO()
                        HomeNavigationEvent.NavigateToLogin -> {
                            navController.navigate(LoginRoute) {
                                popUpTo(HomeRoute) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable<CreateNoteRoute> {
            CreateNoteScreen(
                onNavigate = {
                    when(it) {
                        CreateNoteEvent.NavigateBack -> navController.popBackStack()
                    }
                }
            )
        }

    }

}