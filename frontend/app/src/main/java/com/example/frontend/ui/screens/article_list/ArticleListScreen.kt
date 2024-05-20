package com.example.frontend.ui.screens.article_list

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.BookmarkList
import com.example.frontend.ui.component.BottomSheetBookmarkContent
import com.example.frontend.ui.component.BottomSheetNewBookmarkContent
import com.example.frontend.ui.component.SmallArticleCard
import com.example.frontend.ui.theme.Colors
import com.example.frontend.ui.theme.UiConfig
import com.example.frontend.utils.dateToStringExactDateFormat
import kotlinx.coroutines.launch
import java.math.BigInteger

object ArticleListUiConfig {
    enum class BottomSheetContentType(val id: Int) {
        NONE(1),
        BOOKMARK(2),
        NEW_BOOKMARK(3),
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen(
    modifier: Modifier = Modifier,
    articleType: ArticleType,
    uiState: ArticleListUiState,
    onBookmark: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onUnbookmark: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onNewBookmark: (name: String, articleId: BigInteger) -> Unit,
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
                        text = if (articleType == ArticleType.BOOKMARK) "Bookmarks" else "From Publisher",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
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
                ),
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
                        ArticleListScreenContent(
                            modifier = modifier,
                            articles = uiState.articles,
                            onArticleClick = onArticleClick,
                            bookmarks = uiState.bookmarks,
                            onBookmark = onBookmark,
                            onUnbookmark = onUnbookmark,
                            onNewBookmark = onNewBookmark
                        )
                    }
                }
                PullToRefreshContainer(
                    state = refreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = Colors.topBarContainer
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreenContent(
    modifier: Modifier = Modifier,
    articles: List<ArticleMetadata>,
    bookmarks: List<BookmarkList>,
    onBookmark: (articleId: BigInteger, bookmarkListId: BigInteger) -> Unit,
    onUnbookmark: (articleId: BigInteger, bookmarkListId: BigInteger) -> Unit,
    onNewBookmark: (name: String, articleId: BigInteger) -> Unit,
    onArticleClick: (articleId: BigInteger) -> Unit
) {
    if (articles.isNotEmpty()) {
        val bottomSheetState = rememberModalBottomSheetState()
        val bottomSheetScope = rememberCoroutineScope()
        var bottomSheetContent by rememberSaveable {
            mutableStateOf(ArticleListUiConfig.BottomSheetContentType.NONE)
        }
        var bottomSheetBookmarkArticleId by rememberSaveable {
            mutableStateOf(BigInteger.ZERO)
        }
        val expandBottomSheet: (ArticleListUiConfig.BottomSheetContentType) -> Unit = {
            bottomSheetContent = it
            bottomSheetScope.launch { bottomSheetState.show() }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = UiConfig.sideScreenPadding,
                    end = UiConfig.sideScreenPadding,
                    top = 16.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            articles.forEach { article ->
                SmallArticleCard(
                    articleImageUrl = article.imageUrl,
                    title = article.title,
                    publisher = article.publisher.name,
                    date = dateToStringExactDateFormat(article.date),
                    onClick = { onArticleClick(article.id) },
                    onBookmarkClick = {
                        bottomSheetBookmarkArticleId = article.id
                        expandBottomSheet(ArticleListUiConfig.BottomSheetContentType.BOOKMARK)
                    },
                    disableBookmarkButton = true,
                    isBookmarked = article.isBookmarked
                )
                HorizontalDivider()
            }
            if (bottomSheetContent != ArticleListUiConfig.BottomSheetContentType.NONE) {
                val onClose: (() -> Unit) -> Unit = { afterClose ->
                    bottomSheetScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            bottomSheetContent = ArticleListUiConfig.BottomSheetContentType.NONE
                        }
                        afterClose()
                    }
                }
                ModalBottomSheet(
                    onDismissRequest = { onClose {} },
                    sheetState = bottomSheetState
                ) {
                    if (bottomSheetContent == ArticleListUiConfig.BottomSheetContentType.BOOKMARK) {
                        BottomSheetBookmarkContent(
                            articleId = bottomSheetBookmarkArticleId,
                            bookmarkLists = bookmarks,
                            onNewBookmarkList = {
                                onClose {
                                    expandBottomSheet(ArticleListUiConfig.BottomSheetContentType.NEW_BOOKMARK)
                                }
                            },
                            onBookmark = { onBookmark(bottomSheetBookmarkArticleId, it) },
                            onUnBookmark = { onUnbookmark(bottomSheetBookmarkArticleId, it) },
                            onClose = { onClose {} },
                        )
                    } else if (bottomSheetContent == ArticleListUiConfig.BottomSheetContentType.NEW_BOOKMARK) {
                        BottomSheetNewBookmarkContent(
                            onCreateNewBookmark = { name ->
                                onNewBookmark(name, bottomSheetBookmarkArticleId)
                                onClose {}
                            },
                            onClose = { onClose {} },
                        )
                    }
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
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
            )
        }
    }
}
