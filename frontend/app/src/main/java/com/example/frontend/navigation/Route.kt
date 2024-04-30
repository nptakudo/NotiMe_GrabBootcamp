package com.example.frontend.navigation

sealed class Route (val route: String) {
    data object Login : Route("login")
    data object Latest : Route("latest")  // latest articles
    data object Explore : Route("explore")  // explore articles
    data object ArticleDetail : Route("article_detail")
    data object ReadingList : Route("reading_list")
    data object ListDetail : Route("list_detail")
}