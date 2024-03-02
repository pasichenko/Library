package com.makspasich.library

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope

@Stable
class LibraryAppState(
    val snackbarHostState: SnackbarHostState,
    val navController: NavHostController,
//    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {
//    init {
//        coroutineScope.launch {
//            snackbarManager.snackbarMessages.filterNotNull().collect { snackbarMessage ->
//                val text = snackbarMessage.toMessage(resources)
//                snackbarHostState.showSnackbar(text)
//                snackbarManager.clearSnackbarState()
//            }
//        }
//    }

    fun popUp() {
        navController.popBackStack()
    }

    fun navigate(route: String) {
        navController.navigate(route) { launchSingleTop = true }
    }

    fun navigateAndPopUp(route: String, popUp: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }
    }

    fun clearAndNavigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }
}