package com.example.frontend.ui.screens.subscription

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.frontend.data.model.Publisher
import com.example.frontend.navigation.Route
import com.example.frontend.ui.component.NavBar
import com.example.frontend.ui.component.PublisherCard
import com.example.frontend.ui.component.SubscriptionCard
import com.example.frontend.ui.theme.Colors
import com.example.frontend.ui.theme.UiConfig
import kotlinx.coroutines.launch
import java.math.BigInteger

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

    Column(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start = UiConfig.sideScreenPadding,
                    end = UiConfig.sideScreenPadding,
                    top = 8.dp,
                )
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
            Column (
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                subscriptions.forEach { publisher ->
                    val isFollowing = remember { mutableStateOf(publisher.isSubscribed) }
                    SubscriptionCard(
                        name = publisher.name,
                        avatarUrl = publisher.avatarUrl,
                        url = publisher.url,
                        isFollowing = isFollowing,
                        onFollowClick = {
                            isFollowing.value = !isFollowing.value
                        }
                    )
                }
            }
        }

    } else {
        Text(
            text = "Start subscribing to publishers to see articles here! Hop over to Explore to find new publishers.",
            style = MaterialTheme.typography.titleMedium
        )
    }
}


