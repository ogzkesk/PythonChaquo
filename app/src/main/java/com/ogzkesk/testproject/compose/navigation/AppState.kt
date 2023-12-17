package com.ogzkesk.testproject.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.ogzkesk.testproject.compose.GraphViewModel

@Stable
class AppState(val navController: NavHostController) {

    @Composable
    fun getCreateViewModel(entry: NavBackStackEntry): GraphViewModel {
        return entry.navGraphViewModel(navController = navController)
    }

    fun popBackstack(){
        if(checkIsLifecycleResumed()){
            navController.popBackStack()
        }
    }

    fun navigateToSecond(){
        if(checkIsLifecycleResumed()){
            navController.navigate("second")
        }
    }


    fun navigateToThird() {
        if(checkIsLifecycleResumed()){
            navController.navigate("third")
        }
    }

    private fun checkIsLifecycleResumed(): Boolean {
        return checkNotNull(navController.currentBackStackEntry)
            .isLifecycleResumed()
    }

}