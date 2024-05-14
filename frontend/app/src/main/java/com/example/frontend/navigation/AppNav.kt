package com.example.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.frontend.ui.screens.home.HomeRoute
import com.example.frontend.ui.screens.subscription.SubscriptionRoute

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Route.Home.route) {
        showLogin(navController)
        showHome(navController)
        showExplore(navController)
        showReader(navController)
        showBookmarkList(navController)
        showBookmarkListDetail(navController)
        showSubscription(navController)
    }
}

private fun NavGraphBuilder.showLogin(navController: NavController) {
    composable(Route.Login.route) {

    }
}

private fun NavGraphBuilder.showHome(navController: NavController) {
    composable(Route.Home.route) {
        HomeRoute(
            viewModel = hiltViewModel(),
            onArticleClick = {},
            onNavigateNavBar = {},
            onAboutClick = { /*TODO*/ },
            onLogOutClick = { /*TODO*/ }
        )
    }
}

private fun NavGraphBuilder.showExplore(navController: NavController) {
    composable(Route.Explore.route) {

    }
}

private fun NavGraphBuilder.showReader(navController: NavController) {
    composable(Route.Reader.route) {

    }
}

private fun NavGraphBuilder.showBookmarkList(navController: NavController) {
    composable(Route.BookmarkList.route) {

    }
}

private fun NavGraphBuilder.showBookmarkListDetail(navController: NavController) {
    composable(Route.BookmarkListDetail.route) {

    }
}

private fun NavGraphBuilder.showSubscription(navController: NavController) {
    composable(Route.Following.route) {
        SubscriptionRoute(
            viewModel = hiltViewModel(),
            onNavigateNavBar = {},
        )
    }
}