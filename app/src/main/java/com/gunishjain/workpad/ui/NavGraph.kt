package com.gunishjain.workpad.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gunishjain.workpad.ui.auth.AuthNavigationEvent
import com.gunishjain.workpad.ui.auth.login.LoginScreen
import com.gunishjain.workpad.ui.auth.signup.SignupScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = LoginRoute
    ) {

        composable<LoginRoute> {
            LoginScreen(
                onNavigate = {
                    when(it) {
                        AuthNavigationEvent.NavigateToSignUp -> navController.navigate(SignUpRoute)
                        AuthNavigationEvent.NavigateBack -> navController.popBackStack()
                        AuthNavigationEvent.NavigateToHome -> TODO()
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
                        AuthNavigationEvent.NavigateToHome -> TODO()
                        AuthNavigationEvent.NavigateToLogin -> navController.navigate(LoginRoute)
                    }
                }
            )
        }

    }

}