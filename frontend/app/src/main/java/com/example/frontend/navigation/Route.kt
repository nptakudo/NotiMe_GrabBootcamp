package com.example.frontend.navigation

sealed class Route (val route: String) {
    data object Login : Route("login")
    data object Latest : Route("latest")  // latest posts
    data object Explore : Route("explore")  // explore posts
    data object PostDetail : Route("post_detail")
    data object ReadingList : Route("reading_list")
    data object ListDetail : Route("list_detail")
}