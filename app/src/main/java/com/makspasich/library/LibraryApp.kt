package com.makspasich.library

import android.content.res.Resources
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.makspasich.library.model.service.auth.GoogleAuthUiClient
import com.makspasich.library.screens.detail.DetailProductScreen
import com.makspasich.library.screens.edit.EditProductScreen
import com.makspasich.library.screens.login.LoginScreen
import com.makspasich.library.screens.products.ProductsScreen
import com.makspasich.library.ui.theme.LibraryTheme
import kotlinx.coroutines.CoroutineScope

@Composable
fun LibraryApp(googleAuthUiClient: GoogleAuthUiClient) {
    LibraryTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()
            NavHost(
                navController = appState.navController,
                startDestination = NavigationRoute.LoginScreen.route,
                modifier = Modifier.fillMaxSize()
            ) {
                appGraph(appState, googleAuthUiClient)
            }
        }
    }
}

@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController = rememberNavController(),
//    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(snackbarHostState, navController, /*snackbarManager,*/ resources, coroutineScope) {
        LibraryAppState(
            snackbarHostState,
            navController,
//            snackbarManager,
            resources,
            coroutineScope
        )
    }

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}


fun NavGraphBuilder.appGraph(
    appState: LibraryAppState,
    googleAuthUiClient: GoogleAuthUiClient
) {
    composable(NavigationRoute.LoginScreen.route) {
        LoginScreen(
            googleAuthUiClient = googleAuthUiClient,
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
            signOutClick = signOut(googleAuthUiClient, appState)
        )
    }

    composable(NavigationRoute.Products.route) {
        ProductsScreen(
            onProductItemClick = { productId -> appState.navigate(productId) },
            signOutClick = signOut(googleAuthUiClient, appState),
            barcodeResult = { productId ->
                appState.navigate(NavigationRoute.ProductDetails.routeWithArgs(productId))
            },
        )
    }

    composable(
        route = NavigationRoute.ProductDetails.route,
        arguments = listOf(
            navArgument(PRODUCT_ID) { type = NavType.StringType }
        )
    ) {
        DetailProductScreen(
            editProduct = { productId ->
                appState.navigate(NavigationRoute.ProductEdit.routeWithArgs(productId))
            }
        )
    }

    composable(
        route = NavigationRoute.ProductEdit.route,
        arguments = listOf(
            navArgument(PRODUCT_ID) { type = NavType.StringType }
        )
    ) {
        EditProductScreen(
            popUp = { appState.popUp() }
        )
    }
}

@Composable
private fun signOut(
    googleAuthUiClient: GoogleAuthUiClient,
    appState: LibraryAppState
): () -> Unit = {
    googleAuthUiClient.signOut {
        appState.clearAndNavigate(NavigationRoute.LoginScreen.route)
    }
}
