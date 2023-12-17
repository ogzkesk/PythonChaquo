package com.ogzkesk.testproject.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
) : AppState {
    return remember(navController) {
        AppState(navController)
    }
}

fun NavBackStackEntry.isLifecycleResumed(): Boolean {
    return lifecycle.currentState == Lifecycle.State.RESUMED
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.navGraphViewModel(
    navController: NavController,
): T {
    val parentRoute = destination.parent?.route ?: return viewModel()
    val parentEntry  = remember(this){
        navController.getBackStackEntry(parentRoute)
    }
    return viewModel(parentEntry)
}