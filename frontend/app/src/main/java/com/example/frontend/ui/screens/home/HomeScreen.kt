package com.example.frontend.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.BookmarkList
import com.example.frontend.navigation.Route
import com.example.frontend.ui.component.BigArticleCard
import com.example.frontend.ui.component.BottomSheetBookmarkContent
import com.example.frontend.ui.component.BottomSheetNewBookmarkContent
import com.example.frontend.ui.component.SmallArticleCard
import com.example.frontend.ui.screens.search.SearchScreen
import com.example.frontend.ui.theme.Colors
import com.example.frontend.ui.theme.UiConfig
import com.example.frontend.utils.dateDifferenceFromNow
import com.example.frontend.utils.dateToStringAgoFormat
import kotlinx.coroutines.launch
import java.math.BigInteger

object HomeUiConfig {
    enum class BottomSheetContentType(val id: Int) {
        NONE(1),
        BOOKMARK(2),
        NEW_BOOKMARK(3),
        TOP_RIGHT_OPTIONS(4),
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onLoadMoreArticles: () -> Unit,
    onBookmark: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onUnbookmark: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onNewBookmark: (name: String, articleId: BigInteger) -> Unit,
    onSubscribePublisher: (publisherId: BigInteger) -> Unit,
    onUnsubscribePublisher: (publisherId: BigInteger) -> Unit,
    onRefresh: () -> Unit,
    onSearchIconClick: () -> Unit,
    onSearchSubmit: (query: String) -> Unit,
    onSearchCancel: () -> Unit,
    onArticleClick: (articleId: BigInteger) -> Unit,
    onNavigateNavBar: (route: Route) -> Unit,
    onAboutClick: () -> Unit,
    onLogOutClick: () -> Unit
) {
    if (uiState.state == State.MainLoading || uiState.state == State.MainIdle) {
        val refreshScope = rememberCoroutineScope()
        val refreshState = rememberPullToRefreshState()
        if (refreshState.isRefreshing) {
            refreshScope.launch {
                onRefresh()
                refreshState.endRefresh()
            }
        }

        var requestingTopRightOptions by rememberSaveable { mutableStateOf(false) }

        Column(modifier = modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.screen == Screen.Home) "Home" else "Explore",
                        style = MaterialTheme.typography.headlineLarge.copy(
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
                            onClick = onSearchIconClick,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "search for articles",
                            )
                        }
                        IconButton(
                            onClick = { requestingTopRightOptions = true },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = "more options",
                            )
                        }
                    }
                },
                modifier = Modifier.zIndex(1f)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .nestedScroll(refreshState.nestedScrollConnection)
            ) {
                if (!refreshState.isRefreshing) {
                    if (uiState.state == State.MainLoading) {
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
                        HomeScreenContentSortByDate(
                            screenType = uiState.screen,
                            articles = uiState.articles,
                            bookmarks = uiState.bookmarks,
                            onArticleClick = onArticleClick,
                            onBookmark = onBookmark,
                            onUnbookmark = onUnbookmark,
                            onNewBookmark = onNewBookmark,
                            requestingTopRightOptions = requestingTopRightOptions,
                            onTopRightOptionsClose = { requestingTopRightOptions = false },
                            onToExplore = { onNavigateNavBar(Route.Explore) },
                            onLogout = onLogOutClick
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
    if (uiState.state == State.Searching) {
        SearchScreen(
            onSearch = onSearchSubmit,
            onBack = onSearchCancel
        )
    }
    if (uiState.state == State.ShowSearchResults) {
        Column(modifier = modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Search results",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onSearchCancel,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = "back to home",
                        )
                    }
                }
            )
            ShowSearchResults(
                searchResults = uiState.searchResults,
                onArticleClick = onArticleClick,
                bookmarks = uiState.bookmarks,
                onBookmark = onBookmark,
                onUnbookmark = onUnbookmark,
                onNewBookmark = onNewBookmark,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContentSortByDate(
    modifier: Modifier = Modifier,
    screenType: Screen,
    articles: List<ArticleMetadata>,
    bookmarks: List<BookmarkList>,
    onArticleClick: (articleId: BigInteger) -> Unit,
    onBookmark: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onUnbookmark: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onNewBookmark: (name: String, articleId: BigInteger) -> Unit,
    requestingTopRightOptions: Boolean,
    onTopRightOptionsClose: () -> Unit,
    onToExplore: () -> Unit,
    onLogout: () -> Unit
) {
    if (articles.isNotEmpty()) {
        val bigArticle = articles.first()
        val remainingArticles = articles.drop(1)
        val latestArticles = remainingArticles.filter { dateDifferenceFromNow(it.date) == 0L }
        val yesterdayArticles = remainingArticles.filter { dateDifferenceFromNow(it.date) == 1L }
        val olderArticles = remainingArticles.filter { dateDifferenceFromNow(it.date) > 1L }

        val bottomSheetState = rememberModalBottomSheetState()
        val bottomSheetScope = rememberCoroutineScope()
        var bottomSheetContent by rememberSaveable {
            mutableStateOf(HomeUiConfig.BottomSheetContentType.NONE)
        }
        var bottomSheetBookmarkArticleId by rememberSaveable {
            mutableStateOf(BigInteger.ZERO)
        }
        val expandBottomSheet: (HomeUiConfig.BottomSheetContentType) -> Unit = {
            bottomSheetContent = it
            bottomSheetScope.launch { bottomSheetState.show() }
        }
        if (requestingTopRightOptions) {
            expandBottomSheet(HomeUiConfig.BottomSheetContentType.TOP_RIGHT_OPTIONS)
        }

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Latest news",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.inverseOnSurface
                ),
            )
            BigArticleCard(
                publisherAvatarUrl = bigArticle.publisher.avatarUrl,
                articleImageUrl = bigArticle.imageUrl,
                title = bigArticle.title,
                publisher = bigArticle.publisher.name,
                date = dateToStringAgoFormat(bigArticle.date),
                isBookmarked = bigArticle.isBookmarked,
                onClick = { onArticleClick(bigArticle.id) },
                onBookmarkClick = {
                    bottomSheetBookmarkArticleId = bigArticle.id
                    expandBottomSheet(HomeUiConfig.BottomSheetContentType.BOOKMARK)
                },
            )
            ArticleColumn(
                articles = latestArticles,
                onArticleClick = onArticleClick,
                onBookmarkClick = {
                    bottomSheetBookmarkArticleId = it
                    expandBottomSheet(HomeUiConfig.BottomSheetContentType.BOOKMARK)
                },
            )
            if (yesterdayArticles.isNotEmpty()) {
                BigDivider()
                Text(
                    text = "Yesterday",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    ),
                )
                ArticleColumn(
                    articles = yesterdayArticles,
                    onArticleClick = onArticleClick,
                    onBookmarkClick = {
                        bottomSheetBookmarkArticleId = it
                        expandBottomSheet(HomeUiConfig.BottomSheetContentType.BOOKMARK)
                    },
                )
            }
            if (olderArticles.isNotEmpty()) {
                BigDivider()
                Text(
                    text = "A few days ago...",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    ),
                )
                ArticleColumn(
                    articles = olderArticles,
                    onArticleClick = onArticleClick,
                    onBookmarkClick = {
                        bottomSheetBookmarkArticleId = it
                        expandBottomSheet(HomeUiConfig.BottomSheetContentType.BOOKMARK)
                    },
                )
            }
            Spacer(modifier = Modifier.requiredHeight(25.dp))

            if (bottomSheetContent != HomeUiConfig.BottomSheetContentType.NONE) {
                val onClose: (() -> Unit) -> Unit = { afterClose ->
                    bottomSheetScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            bottomSheetContent = HomeUiConfig.BottomSheetContentType.NONE
                        }
                        if (requestingTopRightOptions) {
                            onTopRightOptionsClose()
                        }
                        afterClose()
                    }
                }
                ModalBottomSheet(
                    onDismissRequest = { onClose {} },
                    sheetState = bottomSheetState
                ) {
                    if (bottomSheetContent == HomeUiConfig.BottomSheetContentType.BOOKMARK) {
                        BottomSheetBookmarkContent(
                            articleId = bottomSheetBookmarkArticleId,
                            bookmarkLists = bookmarks,
                            onNewBookmarkList = {
                                onClose {
                                    expandBottomSheet(HomeUiConfig.BottomSheetContentType.NEW_BOOKMARK)
                                }
                            },
                            onBookmark = { onBookmark(bottomSheetBookmarkArticleId, it) },
                            onUnBookmark = { onUnbookmark(bottomSheetBookmarkArticleId, it) },
                            onClose = { onClose {} },
                        )
                    } else if (bottomSheetContent == HomeUiConfig.BottomSheetContentType.NEW_BOOKMARK) {
                        BottomSheetNewBookmarkContent(
                            onCreateNewBookmark = { name ->
                                onNewBookmark(name, bottomSheetBookmarkArticleId)
                                onClose {}
                            },
                            onClose = { onClose {} },
                        )
                    } else if (bottomSheetContent == HomeUiConfig.BottomSheetContentType.TOP_RIGHT_OPTIONS) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = UiConfig.sideScreenPadding)
                                .padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable { onLogout() }
                                    .padding(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Logout,
                                    tint = MaterialTheme.colorScheme.error,
                                    contentDescription = "New bookmark"
                                )
                                Text(
                                    text = "Logout",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = MaterialTheme.colorScheme.error,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        val annotatedString = if (screenType == Screen.Home)
            buildAnnotatedString {
                append("Start subscribing to publishers to see articles here! Hop over to ")

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

                append(" to find new publishers.")
            }
        else
            AnnotatedString("Wait a bit for us to load some articles for you!")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowSearchResults(
    modifier: Modifier = Modifier,
    searchResults: List<ArticleMetadata>,
    bookmarks: List<BookmarkList>,
    onArticleClick: (articleId: BigInteger) -> Unit,
    onBookmark: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onUnbookmark: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onNewBookmark: (name: String, articleId: BigInteger) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState()
    val bottomSheetScope = rememberCoroutineScope()
    var bottomSheetContent by rememberSaveable {
        mutableStateOf(HomeUiConfig.BottomSheetContentType.NONE)
    }
    var bottomSheetBookmarkArticleId by rememberSaveable {
        mutableStateOf(BigInteger.ZERO)
    }
    val expandBottomSheet: (HomeUiConfig.BottomSheetContentType) -> Unit = {
        bottomSheetContent = it
        bottomSheetScope.launch { bottomSheetState.show() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = UiConfig.sideScreenPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ArticleColumn(
            articles = searchResults,
            onArticleClick = onArticleClick,
            onBookmarkClick = {
                bottomSheetBookmarkArticleId = it
                expandBottomSheet(HomeUiConfig.BottomSheetContentType.BOOKMARK)
            },
        )
        if (bottomSheetContent != HomeUiConfig.BottomSheetContentType.NONE) {
            val onClose: (() -> Unit) -> Unit = { afterClose ->
                bottomSheetScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        bottomSheetContent = HomeUiConfig.BottomSheetContentType.NONE
                    }
                    afterClose()
                }
            }
            ModalBottomSheet(
                onDismissRequest = { onClose {} },
                sheetState = bottomSheetState
            ) {
                if (bottomSheetContent == HomeUiConfig.BottomSheetContentType.BOOKMARK) {
                    BottomSheetBookmarkContent(
                        articleId = bottomSheetBookmarkArticleId,
                        bookmarkLists = bookmarks,
                        onNewBookmarkList = {
                            onClose {
                                expandBottomSheet(HomeUiConfig.BottomSheetContentType.NEW_BOOKMARK)
                            }
                        },
                        onBookmark = { onBookmark(bottomSheetBookmarkArticleId, it) },
                        onUnBookmark = { onUnbookmark(bottomSheetBookmarkArticleId, it) },
                        onClose = { onClose {} },
                    )
                } else if (bottomSheetContent == HomeUiConfig.BottomSheetContentType.NEW_BOOKMARK) {
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
            articleImageUrl = article.imageUrl,
            title = article.title,
            publisher = article.publisher.name,
            date = dateToStringAgoFormat(article.date),
            isBookmarked = article.isBookmarked,
            onClick = { onArticleClick(article.id) },
            onBookmarkClick = { onBookmarkClick(article.id) },
            modifier = modifier
        )
        SmallDivider()
    }
}

@Composable
fun BigDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier
            .padding(vertical = 8.dp),
        thickness = 2.dp
    )
}

@Composable
fun SmallDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 0.5.dp
    )
}