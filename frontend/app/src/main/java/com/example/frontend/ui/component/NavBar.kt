package com.example.frontend.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.navigation.Route
import com.example.frontend.ui.theme.Colors

enum class NavBarTab(
    val title: String,
    val imageVector: ImageVector,
    val route: Route
) {
    HOME("Home", Icons.Outlined.Home, Route.Home),
    EXPLORE("Explore", Icons.Outlined.Public, Route.Explore),
    BOOKMARK("Bookmark", Icons.Outlined.CollectionsBookmark, Route.BookmarkList),
    FOLLOWING("Following", Icons.Outlined.HistoryEdu, Route.Following),
}

@Composable
fun NavBar(
    tabs: Array<NavBarTab> = NavBarTab.entries.toTypedArray(),
    currentRoute: String,
    navigateToBottomBarRoute: (route: Route) -> Unit,
) {
    val currentTab = tabs.first { it.route.route == currentRoute }
    BottomBarLayout(
        containerColor = Colors.navBarContainer,
    ) {
        tabs.forEach { tab ->
            val selected = (tab == currentTab)
            if (selected) {
                Button(
                    onClick = { navigateToBottomBarRoute(tab.route) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = Colors.navBarContainer,
                    ),
                    contentPadding = PaddingValues(
                        vertical = 12.dp,
                        horizontal = 16.dp
                    ),
                ) {
                    Icon(
                        imageVector = tab.imageVector,
                        contentDescription = tab.title,
                        modifier = Modifier
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                        )
                    )
                }
            } else {
                IconButton(
                    onClick = { navigateToBottomBarRoute(tab.route) },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                ) {
                    Icon(
                        imageVector = tab.imageVector,
                        contentDescription = tab.title
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBarLayout(
    modifier: Modifier = Modifier,
    containerColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(
            topStart = CornerSize(24.dp),
            topEnd = CornerSize(24.dp),
            bottomEnd = CornerSize(0.dp),
            bottomStart = CornerSize(0.dp),
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}