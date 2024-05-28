package com.example.frontend.navigation

sealed class Route(
    val route: String,
    val args: List<String> = emptyList()
) {
    data object Login : Route("login")
    data object Home : Route("home")  // latest articles
    data object Explore : Route("explore")  // explore articles
    data object Reader : Route(route = "reader", args = listOf("article_id"))
    data object BookmarkList : Route("bookmark_list")
    data object BookmarkListDetail : Route("bookmark_detail")
    data object Following : Route("following")
    data object SubscriptionDetail : Route("subscription_detail")
    data object Search : Route("search")
    data object SearchResult : Route("search_result")
}