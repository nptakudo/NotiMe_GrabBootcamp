package com.example.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.frontend.ui.screens.article_list.ArticleListRoute
import com.example.frontend.ui.screens.article_list.ArticleType
import com.example.frontend.ui.screens.bookmark.BookmarkRoute
import com.example.frontend.ui.screens.home.ExploreRoute
import com.example.frontend.ui.screens.home.HomeRoute
import com.example.frontend.ui.screens.login.LoginRoute
import com.example.frontend.ui.screens.reader.NewReaderRoute
import com.example.frontend.ui.screens.reader.ReaderRoute
import com.example.frontend.ui.screens.search.SearchResultRoute
import com.example.frontend.ui.screens.search.SearchScreen
import com.example.frontend.ui.screens.subscription.SubscriptionRoute
import java.math.BigInteger
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Route.Login.route) {
        showLogin(navController)
        showHome(navController)
        showExplore(navController)
        showReader(navController)
        showNewReader(navController)
        showBookmarkList(navController)
        showBookmarkListDetail(navController)
        showSubscription(navController)
        showSubscriptionDetail(navController)
        showSearch(navController)
        showSearchResult(navController)
        showLogin(navController)
    }
}

private fun NavGraphBuilder.showLogin(navController: NavController) {
    composable(Route.Login.route) {
        LoginRoute(
            viewModel = hiltViewModel(),
            onLoginSuccess = {
                navController.navigate(Route.Home.route)
            }
        )
    }
}

private fun NavGraphBuilder.showHome(navController: NavController) {
    composable(Route.Home.route) {
        HomeRoute(
            viewModel = hiltViewModel(),
            onArticleClick = {
                navController.navigate(Route.Reader.route + "/$it")
            },
            onNavigateNavBar = { route -> navController.navigate(route.route) },
            onAboutClick = { /*TODO*/ },
            onLogOutClick = { /*TODO*/ }
        )
    }
}

private fun NavGraphBuilder.showExplore(navController: NavController) {
    composable(Route.Explore.route) {
        ExploreRoute(
            viewModel = hiltViewModel(),
            onArticleClick = {
                navController.navigate(Route.Reader.route + "/$it")
            },
            onNavigateNavBar = { route -> navController.navigate(route.route) },
            onAboutClick = { /*TODO*/ }) {

        }
    }
}

private fun NavGraphBuilder.showReader(navController: NavController) {
    val route = Route.Reader.route + "/{${Route.Reader.args[0]}}"
    composable(route) {
        val parentEntry = remember(it) {
            navController.getBackStackEntry(route)
        }
        ReaderRoute(
            viewModel = hiltViewModel(parentEntry),
            onReadAnotherArticle = {
                navController.navigate(Route.Reader.route + "/$it")
            },
            onBack = { navController.navigateUp() }
        )
    }
}
private fun NavGraphBuilder.showNewReader(navController: NavController) {
    val route = Route.Reader.route + "/new/{${Route.Reader.args[0]}}"
    composable(route) {
        val parentEntry = remember(it) {
            navController.getBackStackEntry(route)
        }
        NewReaderRoute(
            viewModel = hiltViewModel(parentEntry),
            onBack = { navController.navigateUp() }
        )
    }
}

private fun NavGraphBuilder.showBookmarkList(navController: NavController) {
    composable(Route.BookmarkList.route) {
        BookmarkRoute(
            viewModel = hiltViewModel(),
            onBookmarkDetail = {
                navController.navigate(Route.BookmarkListDetail.route + "/$it")
            }
        )
    }
}

private fun NavGraphBuilder.showBookmarkListDetail(navController: NavController) {
    composable(Route.BookmarkListDetail.route + "/{bookmarkListId}") {
        val bookmarkListId = it.arguments?.getString("bookmarkListId")
        ArticleListRoute(
            viewModel = hiltViewModel(),
            articleType = ArticleType.BOOKMARK,
            id = BigInteger(bookmarkListId ?: "0"),
            disableBookmark = false,
            onBack = { navController.navigateUp() },
            onArticleClick = {
                navController.navigate(Route.Reader.route + "/$it")
            }
        )
    }
}

private fun NavGraphBuilder.showSubscription(navController: NavController) {
    composable(Route.Following.route) {
        SubscriptionRoute(
            viewModel = hiltViewModel(),
            onSubscriptionClick = {
                navController.navigate(Route.SubscriptionDetail.route + "/$it")
            },
            onSearchIconClick = { navController.navigate(Route.Search.route) },
            onToExplore = { navController.navigate(Route.Explore.route) }
        )
    }
}

private fun NavGraphBuilder.showSubscriptionDetail(navController: NavController) {
    composable(Route.SubscriptionDetail.route + "/{publisherId}") {
        val publisherId = it.arguments?.getString("publisherId")
        ArticleListRoute(
            viewModel = hiltViewModel(),
            articleType = ArticleType.PUBLISHER,
            id = BigInteger(publisherId ?: "0"),
            disableBookmark = false,
            onBack = { navController.navigateUp() },
            onArticleClick = {
                navController.navigate(Route.Reader.route + "/$it")
            }
        )
    }
}

private fun NavGraphBuilder.showSearch(navController: NavController) {
    composable(Route.Search.route) {
        SearchScreen(
            onBack = { navController.navigateUp() },
            onSearch = {
                val query = if (it.contains("/")) {
                    URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                } else {
                    it
                }
                navController.navigate(Route.SearchResult.route + "/$query")
            }
        )
    }
}

private fun NavGraphBuilder.showSearchResult(navController: NavController) {
    composable(Route.SearchResult.route + "/{query}") { it ->
        val encodedQuery = it.arguments?.getString("query")
        val query = encodedQuery?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
        SearchResultRoute(
            viewModel = hiltViewModel(),
            query = query ?: "",
            disableBookmark = true,
            obBack = { navController.navigateUp() },
            onSubscriptionClick = {
                navController.navigate(Route.SubscriptionDetail.route + "/$it")
            },
            onReadNewArticle = {
                val encodedUrl = URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                navController.navigate(Route.Reader.route + "/new/$encodedUrl")
            },
            onAddSourceSuccess = {
                navController.navigate(Route.Following.route)
            }
        )
    }
}
