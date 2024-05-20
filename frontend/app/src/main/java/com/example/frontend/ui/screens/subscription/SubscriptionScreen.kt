package com.example.frontend.ui.screens.subscription

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.frontend.data.model.Publisher
import com.example.frontend.ui.component.SubscriptionCard
import com.example.frontend.ui.screens.home.Screen
import com.example.frontend.ui.theme.Colors
import com.example.frontend.ui.theme.UiConfig
import kotlinx.coroutines.launch
import java.math.BigInteger

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    modifier: Modifier = Modifier,
    uiState: SubscriptionUiState,
    onRefresh: () -> Unit,
    onSearchIconClick: () -> Unit,
    onSubscriptionClick: (publisherId: BigInteger) -> Unit,
    onSubscribe: (publisherId: BigInteger) -> Unit,
    onUnSubscribe: (publisherId: BigInteger) -> Unit,
    onToExplore: () -> Unit
) {
    val refreshScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    if (refreshState.isRefreshing) {
        refreshScope.launch {
            onRefresh()
            refreshState.endRefresh()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "Subscribed",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 10.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Colors.topBarContainer
                ),
                actions = {
                    Row {
                        IconButton(
                            onClick = onSearchIconClick
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "search"
                            )
                        }
                    }
                },
                modifier = Modifier.zIndex(1f)
            )

            Box(
                modifier = Modifier
                    .padding(
                        start = UiConfig.sideScreenPadding,
                        end = UiConfig.sideScreenPadding,
                    )
                    .fillMaxSize()
                    .nestedScroll(refreshState.nestedScrollConnection)
            ) {
                if (!refreshState.isRefreshing) {
                    if (uiState.state == State.Loading) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Loading...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        SubscriptionScreenContent(
                            subscriptions = uiState.subscriptions,
                            onSubscriptionClick = onSubscriptionClick,
                            onSubscribe = onSubscribe,
                            onUnSubscribe = onUnSubscribe,
                            onToExplore = onToExplore,
                        )
                    }
                }
                PullToRefreshContainer(
                    state = refreshState,
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                    containerColor = Colors.topBarContainer
                )
            }
        }
    }
}

@Composable
fun SubscriptionScreenContent(
    modifier: Modifier = Modifier,
    subscriptions: List<Publisher>,
    onSubscriptionClick: (publisherId: BigInteger) -> Unit,
    onSubscribe: (publisherId: BigInteger) -> Unit,
    onUnSubscribe: (publisherId: BigInteger) -> Unit,
    onToExplore: () -> Unit
) {

    if (subscriptions.isNotEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = UiConfig.sideScreenPadding,
                    end = UiConfig.sideScreenPadding,
                    top = 16.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subscriptions.forEach { publisher ->
                    val isFollowing = remember { mutableStateOf(publisher.isSubscribed) }
                    SubscriptionCard(
                        name = publisher.name,
                        avatarUrl = publisher.avatarUrl,
                        url = publisher.url,
                        isFollowing = isFollowing,
                        onSubscribe = {
                            isFollowing.value = true
                            onSubscribe(publisher.id)
                        },
                        onUnSubscribe = {
                            onUnSubscribe(publisher.id)
                            isFollowing.value = false
                        },
                        onClick = {
                            onSubscriptionClick(publisher.id)
                        }
                    )
                    HorizontalDivider()
                }
            }
        }

    } else {
        val annotatedString = buildAnnotatedString {
            append("You haven't subscribed to any publisher yet! Hop over to ")

            pushStringAnnotation(tag = "explore", annotation = "explore")
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            ) {
                append("Explore")
            }
            pop()

            append(" or search for new publishers.")
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = UiConfig.sideScreenPadding),
            contentAlignment = Alignment.Center
        ) {
            ClickableText(
                text = annotatedString,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                onClick = { offset ->
                    annotatedString.getStringAnnotations(
                        tag = "explore",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        onToExplore()
                    }
                }
            )
        }
    }
}


