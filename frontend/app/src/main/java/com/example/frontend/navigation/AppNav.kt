package com.example.frontend.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.frontend.ui.screens.home.HomeRoute
import com.example.frontend.ui.screens.reader.ReaderRoute
import com.example.frontend.ui.screens.search.SearchScreen
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
        showSearch(navController)
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
            onArticleClick = {
                navController.navigate(Route.Reader.route + "/$it")
            },
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
    composable(Route.Reader.route + "/{articleId}") {
        val articleId = it.arguments?.getString("articleId")
        if (articleId != null) {
            ReaderRoute(
                viewModel = hiltViewModel(),
                onReadAnotherArticle = {},
                onBack = { navController.navigateUp()}
            )
        }
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
            onSearchIconClick = {navController.navigate(Route.Search.route)}
        )
    }
}
private fun NavGraphBuilder.showSearch(navController: NavController) {
    composable(Route.Search.route) {
        SearchScreen(
            onBack = { navController.navigateUp() },
            onSearch = {
                Log.i("AppNavGraph", "Search for $it")
            }
        )
    }
}