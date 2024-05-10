package com.example.frontend.ui.screens.subscribed_list

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.frontend.data.model.Publisher
import com.example.frontend.navigation.Route
import com.example.frontend.ui.component.NavBar
import com.example.frontend.ui.screens.home.HomeScreenContentSortByDate
import com.example.frontend.ui.theme.Colors
import com.example.frontend.ui.theme.UiConfig
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen (
    modifier: Modifier = Modifier,
    uiState: SubscriptionUiState,
    onRefresh: () -> Unit,
    onSearchIconClick: () -> Unit,
    onNavigateNavBar: (route: Route) -> Unit
) {
    val refreshScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    if (refreshState.isRefreshing) {
        refreshScope.launch {
            onRefresh()
            refreshState.endRefresh()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Colors.topBarContainer
                ),
                actions = {
                    Row {
                        IconButton(
                            onClick = onSearchIconClick,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "search for articles",
                            )
                        }
                        IconButton(
                            onClick = { /*TODO*/ },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = "more options",
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavBar(
                currentRoute = Route.Following,
                navigateToBottomBarRoute = onNavigateNavBar
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .nestedScroll(refreshState.nestedScrollConnection)
        ) {
            if (!refreshState.isRefreshing) {
                SubscriptionScreenContent(
                    subscriptions = uiState.subscriptions
                )
            }
            PullToRefreshContainer(
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreenContent(
    modifier: Modifier = Modifier,
    subscriptions: List<Publisher>,
) {

    if (subscriptions.isNotEmpty()) {
        val scrollState = rememberScrollState()

        Column (
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    start = UiConfig.sideScreenPadding,
                    end = UiConfig.sideScreenPadding,
                    top = 16.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text (
                text = "Subscribed Publishers",
                style = MaterialTheme.typography.titleMedium
            )
        }

    } else {
        Text(
            text = "Start subscribing to publishers to see articles here! Hop over to Explore to find new publishers.",
            style = MaterialTheme.typography.titleMedium
        )
    }
}


