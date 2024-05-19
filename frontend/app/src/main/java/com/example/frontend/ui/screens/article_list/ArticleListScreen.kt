package com.example.frontend.ui.screens.article_list

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.ui.component.NewArticleCard
import com.example.frontend.ui.component.SubscriptionCard
import com.example.frontend.ui.screens.home.Divider
import com.example.frontend.ui.screens.subscription.SubscriptionScreenContent
import com.example.frontend.ui.screens.subscription.SubscriptionUiState
import com.example.frontend.ui.theme.Colors
import com.example.frontend.ui.theme.UiConfig
import com.example.frontend.utils.dateToStringAgoFormat
import com.example.frontend.utils.dateToStringExactDateFormat
import kotlinx.coroutines.launch
import java.math.BigInteger

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen (
    modifier: Modifier = Modifier,
    uiState: ArticleListUiState,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    onArticleClick: (articleId: BigInteger) -> Unit
) {
    val refreshScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    if (refreshState.isRefreshing) {
        refreshScope.launch {
            onRefresh()
            refreshState.endRefresh()
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "Subscribed Publishers",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = "back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Colors.topBarContainer
                )
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
                    ArticleListScreenContent(
                        modifier = modifier,
                        articles = uiState.articles,
                        onArticleClick = onArticleClick
                    )
                }
            }
        }
    }
}
@Composable
fun ArticleListScreenContent(
    modifier: Modifier = Modifier,
    articles: List<ArticleMetadata>,
    onArticleClick: (articleId: BigInteger) -> Unit
) {
    if (articles.isNotEmpty()) {
        Column (
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
            Column (
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                articles.forEach { blog ->
                    NewArticleCard(
                        articleImageUrl = blog.imageUrl,
                        title = blog.title,
                        publisher = blog.publisher.name,
                        date = dateToStringExactDateFormat(blog.date),
                        onClick = { onArticleClick(blog.id) }
                    )
                    Divider()
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
