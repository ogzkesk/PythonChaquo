package com.ogzkesk.testproject.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.ogzkesk.testproject.compose.Second
import com.ogzkesk.testproject.compose.Starter
import com.ogzkesk.testproject.compose.Third

@Composable
fun AppNavHost() {
    val appState = rememberAppState()

    NavHost(
        navController = appState.navController,
        startDestination = "graph"
    ) {
        navigation("starter",route = "graph"){

            composable("starter") { entry ->
                Starter(
                    viewModel = appState.getCreateViewModel(entry = entry),
                    onNavigateSecond = appState::navigateToSecond
                )
            }

            composable("second") { entry ->
                Second(
                    viewModel = appState.getCreateViewModel(entry = entry),
                    onPopBackstack = appState::popBackstack,
                    onNavigateToThird = appState::navigateToThird
                )
            }

            composable("third"){ entry ->
                Third(
                    viewModel = appState.getCreateViewModel(entry = entry),
                    onPopBackstack = appState::popBackstack
                )
            }
        }
    }
}