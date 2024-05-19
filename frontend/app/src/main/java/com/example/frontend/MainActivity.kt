package com.example.frontend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.frontend.ui.screens.home.ExploreRoute
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontend.navigation.AppNavGraph
import com.example.frontend.navigation.Route
import com.example.frontend.ui.component.NavBar
import com.example.frontend.ui.screens.subscription.SubscriptionRoute
import com.example.frontend.ui.theme.FrontendTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrontendTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotiMeApp()
                }
            }
        }
    }
}

@Composable
fun NotiMeApp() {
    val navController = rememberNavController()
    val currentPage by navController.currentScreenAsState()
    val currentRoute by navController.currentRouteAsState()

    val listRoutes = listOf(
        Route.Home.route,
        Route.Explore.route,
        Route.BookmarkList.route,
        Route.Following.route
    )

    Scaffold (
        bottomBar = {
            Log.d("AppNavGraph", "currentRoute: $currentRoute")
            if (currentRoute in listRoutes) {
                NavBar(
                    currentRoute = currentRoute!!,
                    navigateToBottomBarRoute = {
                        route -> navController.navigate(route.route)
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
             AppNavGraph(navController = navController)
        }

    }
}

@Stable
@Composable
private fun NavController.currentScreenAsState(): MutableState<String> {
    val selectedItem = remember { mutableStateOf("") }
    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            Log.d("Navigation", "Destination changed to: ${destination.route}")
            selectedItem.value = destination.route.toString()
        }
        addOnDestinationChangedListener(listener)
        onDispose { removeOnDestinationChangedListener(listener) }
    }
    return selectedItem
}

@Stable
@Composable
private fun NavController.currentRouteAsState(): State<String?> {
    val selectedItem = remember { mutableStateOf<String?>(null) }
    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            Log.d("Navigation", "Destination: ${destination.route}")
            selectedItem.value = destination.route
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }
    return selectedItem
}