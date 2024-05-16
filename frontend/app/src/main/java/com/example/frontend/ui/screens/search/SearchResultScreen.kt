package com.example.frontend.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.Publisher
import com.example.frontend.data.model.Subscription
import com.example.frontend.navigation.Route
import com.example.frontend.ui.component.NewArticleCard
import com.example.frontend.ui.component.SubscriptionCard
import com.example.frontend.ui.screens.home.Divider
import com.example.frontend.ui.screens.subscription.SubscriptionScreenContent
import com.example.frontend.ui.theme.Colors
import com.example.frontend.ui.theme.UiConfig
import com.example.frontend.utils.dateToStringAgoFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen (
    modifier: Modifier = Modifier,
    uiState: SearchResultUiState,
    onBack: () -> Unit,
    query: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Results for \"$query\"",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Colors.topBarContainer
            ),
            navigationIcon = {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBackIosNew,
                        contentDescription = "back"
                    )
                }
            }
        )
        Box(
            modifier = Modifier
                .padding(
                    start = UiConfig.sideScreenPadding,
                    end = UiConfig.sideScreenPadding,
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when (uiState.isNewSource) {
                false -> SearchResultContentForPublishers(
                    modifier = modifier,
                    subscriptions = uiState.subscriptions
                )
                true -> SearchResultContentForArticles(
                    modifier = modifier,
                    articles = uiState.articles
                )
            }
        }
    }
}

@Composable
fun SearchResultContentForPublishers (
    modifier: Modifier,
    subscriptions: List<Publisher>,
) {
    if (subscriptions.isNotEmpty()) {
        Column (
            modifier = modifier
                .fillMaxSize()
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

@Composable
fun SearchResultContentForArticles (
    modifier: Modifier,
    articles: List<ArticleMetadata>,
) {
    if (articles.isNotEmpty()) {
        Column (
            modifier = modifier
                .fillMaxSize()
                .padding(
                    start = UiConfig.sideScreenPadding,
                    end = UiConfig.sideScreenPadding,
                    top = 16.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text (
                    text = "This publisher is not in our database yet. Wanna add?",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    modifier = Modifier.width(112.dp),
                    onClick = {  },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Add",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        )
                    )
                }
            }
            Divider()
            Text (
                text = "Articles from this publisher",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            Column (
                modifier = Modifier.padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                articles.forEach { blog ->
                    NewArticleCard(
                        articleImageUrl = blog.articleImageUrl,
                        title = blog.title,
                        publisher = blog.publisher.name,
                        date = dateToStringAgoFormat(blog.date),
                        onClick = {}
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
