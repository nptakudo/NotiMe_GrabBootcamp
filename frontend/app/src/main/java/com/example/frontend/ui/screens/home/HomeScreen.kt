package com.example.frontend.ui.screens.home

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
import androidx.compose.material3.HorizontalDivider
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
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.navigation.Route
import com.example.frontend.ui.component.BigArticleCard
import com.example.frontend.ui.component.NavBar
import com.example.frontend.ui.component.SmallArticleCard
import com.example.frontend.ui.theme.Colors
import com.example.frontend.ui.theme.UiConfig
import com.example.frontend.utils.dateDifferenceFromNow
import com.example.frontend.utils.dateToStringAgoFormat
import kotlinx.coroutines.launch
import java.math.BigInteger

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onLoadMoreArticles: () -> Unit,
    onBookmarkArticle: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onUnbookmarkArticle: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onSubscribePublisher: (publisherId: BigInteger) -> Unit,
    onUnsubscribePublisher: (publisherId: BigInteger) -> Unit,
    onRefresh: () -> Unit,
    onSearchIconClick: () -> Unit,
    onArticleClick: (articleId: BigInteger) -> Unit,
    onNavigateNavBar: (route: Route) -> Unit,
    onAboutClick: () -> Unit,
    onLogOutClick: () -> Unit
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
                currentRoute = Route.Home,
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
                HomeScreenContentSortByDate(
                    articles = uiState.articles,
                    onArticleClick = onArticleClick,
                    onBookmarkClick = { /* TODO */ },
                )
            }
            PullToRefreshContainer(
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}

@Composable
fun HomeScreenContentSortByDate(
    modifier: Modifier = Modifier,
    articles: List<ArticleMetadata>,
    onArticleClick: (articleId: BigInteger) -> Unit,
    onBookmarkClick: (articleId: BigInteger) -> Unit,
) {
    if (articles.isNotEmpty()) {
        val bigArticle = articles.first()
        val remainingArticles = articles.drop(1)
        val latestArticles = remainingArticles.filter { dateDifferenceFromNow(it.date) == 0L }
        val yesterdayArticles = remainingArticles.filter { dateDifferenceFromNow(it.date) == 1L }
        val olderArticles = remainingArticles.filter { dateDifferenceFromNow(it.date) > 1L }

        val scrollState = rememberScrollState()
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    start = UiConfig.sideScreenPadding,
                    end = UiConfig.sideScreenPadding,
                    top = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Latest news",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.inverseOnSurface
                ),
            )
            BigArticleCard(
                publisherAvatarUrl = bigArticle.publisher.avatarUrl,
                articleImageUrl = bigArticle.articleImageUrl,
                title = bigArticle.title,
                publisher = bigArticle.publisher.name,
                date = dateToStringAgoFormat(bigArticle.date),
                isBookmarked = bigArticle.isBookmarked,
                onClick = { onArticleClick(bigArticle.id) },
                onBookmarkClick = { onBookmarkClick(bigArticle.id) },
            )
            ArticleColumn(
                articles = latestArticles,
                onArticleClick = onArticleClick,
                onBookmarkClick = onBookmarkClick,
            )
            Divider()
            Text(
                text = "Yesterday",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.inverseOnSurface
                ),
            )
            ArticleColumn(
                articles = yesterdayArticles,
                onArticleClick = onArticleClick,
                onBookmarkClick = onBookmarkClick,
            )
            Divider()
            Text(
                text = "A few days ago...",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.inverseOnSurface
                ),
            )
            ArticleColumn(
                articles = olderArticles,
                onArticleClick = onArticleClick,
                onBookmarkClick = onBookmarkClick,
            )
        }
    } else {
        Text(
            text = "Start subscribing to publishers to see articles here! Hop over to Explore to find new publishers.",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun ArticleColumn(
    modifier: Modifier = Modifier,
    articles: List<ArticleMetadata>,
    onArticleClick: (articleId: BigInteger) -> Unit,
    onBookmarkClick: (articleId: BigInteger) -> Unit,
) {
    articles.forEach { article ->
        SmallArticleCard(
            articleImageUrl = article.articleImageUrl,
            title = article.title,
            publisher = article.publisher.name,
            date = dateToStringAgoFormat(article.date),
            isBookmarked = article.isBookmarked,
            onClick = { onArticleClick(article.id) },
            onBookmarkClick = { onBookmarkClick(article.id) },
            modifier = modifier
        )
    }
}

@Composable
fun Divider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier.padding(vertical = 8.dp),
    )
}