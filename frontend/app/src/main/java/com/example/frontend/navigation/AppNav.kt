package com.example.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Route.Login.route) {
        showLogin(navController)
        showLatest(navController)
        showExplore(navController)
        showPostDetail(navController)
        showReadingList(navController)
        showListDetail(navController)
    }
}
private fun NavGraphBuilder.showLogin(navController: NavController) {
    composable(Route.Login.route) {

    }
}
private fun NavGraphBuilder.showLatest(navController: NavController) {
    composable(Route.Latest.route) {

    }
}
private fun NavGraphBuilder.showExplore(navController: NavController) {
    composable(Route.Explore.route) {

    }
}
private fun NavGraphBuilder.showPostDetail(navController: NavController) {
    composable(Route.PostDetail.route) {

    }
}
private fun NavGraphBuilder.showReadingList(navController: NavController) {
    composable(Route.ReadingList.route) {

    }
}
private fun NavGraphBuilder.showListDetail(navController: NavController) {
    composable(Route.ListDetail.route) {

    }
}