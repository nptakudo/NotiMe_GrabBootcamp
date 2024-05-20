package com.example.frontend.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.Publisher
import com.example.frontend.ui.component.SmallArticleCard
import com.example.frontend.ui.component.SubscriptionCard
import com.example.frontend.ui.theme.Colors
import com.example.frontend.ui.theme.UiConfig
import com.example.frontend.utils.dateToStringExactDateFormat
import java.math.BigInteger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    modifier: Modifier = Modifier,
    uiState: SearchResultUiState,
    onBack: () -> Unit,
    onSubscriptionClick: (publisherId: BigInteger) -> Unit,
    query: String,
    onSubscribe: (publisherId: BigInteger) -> Unit,
    onUnSubscribe: (publisherId: BigInteger) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Results for \"$query\"",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
        Column(
            modifier = Modifier
                .padding(
                    start = UiConfig.sideScreenPadding,
                    end = UiConfig.sideScreenPadding,
                )
                .fillMaxSize()
        ) {
            when (uiState.isNewSource) {
                true -> SearchResultContentForPublishers(
                    modifier = modifier,
                    subscriptions = uiState.subscriptions,
                    onSubscriptionClick = onSubscriptionClick,
                    onSubscribe = onSubscribe,
                    onUnSubscribe = onUnSubscribe
                )

                false -> SearchResultContentForArticles(
                    modifier = modifier,
                    articles = uiState.articles
                )
            }
        }
    }
}

@Composable
fun SearchResultContentForPublishers(
    modifier: Modifier,
    subscriptions: List<Publisher>,
    onSubscriptionClick: (publisherId: BigInteger) -> Unit,
    onSubscribe: (publisherId: BigInteger) -> Unit,
    onUnSubscribe: (publisherId: BigInteger) -> Unit
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = UiConfig.sideScreenPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nothing here :-(",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun SearchResultContentForArticles(
    modifier: Modifier,
    articles: List<ArticleMetadata>,
) {
    if (articles.isNotEmpty()) {
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
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
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
                    onClick = { },
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
            HorizontalDivider()
            Text(
                text = "Articles from this publisher",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                articles.forEach { article ->
                    SmallArticleCard(
                        articleImageUrl = article.imageUrl,
                        title = article.title,
                        publisher = article.publisher.name,
                        date = dateToStringExactDateFormat(article.date),
                        onClick = { /* TODO */ },
                        disableBookmarkButton = true,
                        onBookmarkClick = {},
                        isBookmarked = article.isBookmarked
                    )
                    HorizontalDivider()
                }
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = UiConfig.sideScreenPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nothing here :-(",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
