package com.example.frontend.ui.screens.reader

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.imageLoader
import com.example.frontend.ui.component.BottomSheetBookmarkContent
import com.example.frontend.ui.component.BottomSheetNewBookmarkContent
import com.example.frontend.ui.component.ImageFromUrl
import com.example.frontend.ui.component.PublisherCard
import com.example.frontend.ui.component.SmallArticleCard
import com.example.frontend.ui.theme.Colors
import com.example.frontend.ui.theme.ReaderTextStyle
import com.example.frontend.ui.theme.UiConfig
import com.example.frontend.utils.dateToStringAgoFormat
import com.example.frontend.utils.isValidImageUrl
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigInteger

object ReaderUiConfig {
    const val ARTICLE_IMG_HEIGHT = 200

    enum class BottomSheetContentType(val id: Int) {
        NONE(1),
        BOOKMARK(2),
        NEW_BOOKMARK(3),
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    modifier: Modifier = Modifier,
    uiState: ReaderUiState,
    onRefresh: () -> Unit,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
    onShare: (ctx: Context) -> Unit,
    onToBrowser: (ctx: Context) -> Unit,
    onRelatedArticleClick: (articleId: BigInteger) -> Unit,
    onBookmark: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onUnBookmark: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onNewBookmark: (name: String, articleId: BigInteger) -> Unit,
    onLoadMoreRelatedArticles: () -> Unit,
    onBack: () -> Unit,
) {
    val refreshScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    if (refreshState.isRefreshing) {
        refreshScope.launch {
            onRefresh()
            refreshState.endRefresh()
        }
    }

    var currentArticleBookmarkRequest by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isValidImage = remember { mutableStateOf(false) }
    LaunchedEffect(uiState.article.metadata.imageUrl) {
        isValidImage.value = uiState.article.metadata.imageUrl?.let { url ->
            coroutineScope.async {
                isValidImageUrl(context, url)
            }.await()
        } ?: false
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            modifier = Modifier
                .requiredHeight(50.dp)
                .shadow(
                    elevation = 16.dp,
                    spotColor = Color.DarkGray
                )
                .zIndex(1f),
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
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
            },
            actions = {
                Row {
                    if (uiState.article.metadata.isBookmarked) {
                        IconButton(
                            onClick = {
                                currentArticleBookmarkRequest = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bookmark,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "unbookmark",
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                currentArticleBookmarkRequest = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = "bookmark",
                            )
                        }
                    }
                    val currentCtx = LocalContext.current
                    IconButton(
                        onClick = { onShare(currentCtx) },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "share",
                        )
                    }
                    IconButton(
                        onClick = { onToBrowser(currentCtx) },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.OpenInBrowser,
                            contentDescription = "open in browser",
                        )
                    }
                }
            }
        )
        Box(
            modifier = Modifier
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
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                } else {
                    if (isValidImage.value) {
                        ImageFromUrl(
                            url = uiState.article.metadata.imageUrl!!,
                            contentDescription = "article image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(ReaderUiConfig.ARTICLE_IMG_HEIGHT.dp + 40.dp)
                                .align(Alignment.TopCenter),
                        )
                    }
                    ReaderScreenContent(
                        firstSpacerHeight = if (!isValidImage.value) {
                            0.dp
                        } else {
                            ReaderUiConfig.ARTICLE_IMG_HEIGHT.dp
                        },
                        uiState = uiState,
                        onFollow = onFollow,
                        onUnfollow = onUnfollow,
                        onArticleClick = onRelatedArticleClick,
                        onBookmark = onBookmark,
                        onUnBookmark = onUnBookmark,
                        onNewBookmark = onNewBookmark,
                        onLoadMoreRelatedArticles = onLoadMoreRelatedArticles,
                        onToBrowser = onToBrowser,
                        currentArticleBookmarkRequest = currentArticleBookmarkRequest,
                        onCurrentArticleBookmarkRequestCompleted = {
                            currentArticleBookmarkRequest = false
                        },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreenContent(
    modifier: Modifier = Modifier,
    firstSpacerHeight: Dp,
    uiState: ReaderUiState,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
    onArticleClick: (articleId: BigInteger) -> Unit,
    onBookmark: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onUnBookmark: (articleId: BigInteger, bookmarkId: BigInteger) -> Unit,
    onNewBookmark: (name: String, articleId: BigInteger) -> Unit,
    onLoadMoreRelatedArticles: () -> Unit,
    onToBrowser: (ctx: Context) -> Unit,
    currentArticleBookmarkRequest: Boolean,
    onCurrentArticleBookmarkRequestCompleted: () -> Unit,
) {
    val publisher = uiState.article.metadata.publisher
    val metadata = uiState.article.metadata
    val summary = uiState.article.summary
    val content = uiState.article.content.content
    val relatedArticles = uiState.relatedArticles

    val bottomSheetState = rememberModalBottomSheetState()
    val bottomSheetScope = rememberCoroutineScope()
    var bottomSheetContent by rememberSaveable {
        mutableStateOf(ReaderUiConfig.BottomSheetContentType.NONE)
    }
    var bottomSheetBookmarkArticleId by rememberSaveable {
        mutableStateOf(BigInteger.ZERO)
    }
    val expandBottomSheet: (ReaderUiConfig.BottomSheetContentType) -> Unit = {
        bottomSheetContent = it
        bottomSheetScope.launch { bottomSheetState.show() }
    }
    if (currentArticleBookmarkRequest) {
        bottomSheetBookmarkArticleId = metadata.id
        expandBottomSheet(ReaderUiConfig.BottomSheetContentType.BOOKMARK)
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        Spacer(modifier = Modifier.height(firstSpacerHeight))
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = if (firstSpacerHeight != 0.dp) {
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            } else {
                RoundedCornerShape(0.dp)
            },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        start = UiConfig.sideScreenPadding,
                        end = UiConfig.sideScreenPadding,
                        top = 16.dp,
                        bottom = 50.dp,
                    )
                    .background(MaterialTheme.colorScheme.surface),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PublisherCard(
                    name = publisher.name,
                    avatarUrl = publisher.avatarUrl,
                    timestamp = dateToStringAgoFormat(metadata.date),
                    isFollowing = publisher.isSubscribed,
                    onFollowClick = {
                        if (it) {
                            onUnfollow()
                        } else {
                            onFollow()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = metadata.title,
                    style = ReaderTextStyle.title,
                )
                if (summary != "") {
                    SummaryCard(
                        summary = summary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (content != "") {
                    MarkdownText(
                        markdown = content,
                        fontResource = ReaderTextStyle.bodyResource,
                        linkColor = MaterialTheme.colorScheme.primary,
                        imageLoader = LocalContext.current.imageLoader,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = publisher.name,
                            style = ReaderTextStyle.credit
                        )
                    }
                } else {
                    val annotatedString = buildAnnotatedString {
                        append("Sadly, we could not load the article. You will need to find the article fresh ")

                        pushStringAnnotation(tag = "url", annotation = metadata.url)
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontStyle = FontStyle.Italic,
                            )
                        ) {
                            append("on its website")
                        }
                        pop()

                        append(".")
                    }
                    val currentCtx = LocalContext.current
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
                                tag = "url",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let {
                                onToBrowser(currentCtx)
                            }
                        }
                    )
                }
                HorizontalDivider()
                Text(
                    text = "Related Articles",
                    style = MaterialTheme.typography.titleMedium
                )
                relatedArticles.forEach { article ->
                    SmallArticleCard(
                        articleImageUrl = article.imageUrl,
                        title = article.title,
                        publisher = article.publisher.name,
                        date = dateToStringAgoFormat(article.date),
                        isBookmarked = article.isBookmarked,
                        onClick = { onArticleClick(article.id) },
                        onBookmarkClick = {
                            bottomSheetBookmarkArticleId = article.id
                            expandBottomSheet(ReaderUiConfig.BottomSheetContentType.BOOKMARK)
                        },
                    )
                }
            }
        }
        if (bottomSheetContent != ReaderUiConfig.BottomSheetContentType.NONE) {
            val onClose: (() -> Unit) -> Unit = { afterClose ->
                bottomSheetScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        bottomSheetContent = ReaderUiConfig.BottomSheetContentType.NONE
                    }
                    if (currentArticleBookmarkRequest) {
                        onCurrentArticleBookmarkRequestCompleted()
                    }
                    afterClose()
                }
            }
            ModalBottomSheet(
                onDismissRequest = { onClose {} },
                sheetState = bottomSheetState
            ) {
                if (bottomSheetContent == ReaderUiConfig.BottomSheetContentType.BOOKMARK) {
                    BottomSheetBookmarkContent(
                        articleId = bottomSheetBookmarkArticleId,
                        bookmarkLists = uiState.bookmarks,
                        onNewBookmarkList = {
                            onClose {
                                expandBottomSheet(ReaderUiConfig.BottomSheetContentType.NEW_BOOKMARK)
                            }
                        },
                        onBookmark = { onBookmark(bottomSheetBookmarkArticleId, it) },
                        onUnBookmark = { onUnBookmark(bottomSheetBookmarkArticleId, it) },
                        onClose = { onClose {} },
                    )
                } else if (bottomSheetContent == ReaderUiConfig.BottomSheetContentType.NEW_BOOKMARK) {
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
fun SummaryCard(
    modifier: Modifier = Modifier,
    summary: String,
) {
    var isCollapsed by rememberSaveable { mutableStateOf(true) }
    Card(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { isCollapsed = !isCollapsed },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                if (isCollapsed)
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowUp,
                        contentDescription = "hide summary",
                    )
                else
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "show summary",
                    )
            }
            if (isCollapsed) {
                MarkdownText(
                    markdown = summary.trim(),
                    fontResource = ReaderTextStyle.bodyResource,
                )
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreenForNewArticle(
    modifier: Modifier = Modifier,
    uiState: NewReaderUiState,
    onRefresh: () -> Unit,
    onToBrowser: (ctx: Context) -> Unit,
    onBack: () -> Unit,
) {
    val refreshScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    if (refreshState.isRefreshing) {
        refreshScope.launch {
            onRefresh()
            refreshState.endRefresh()
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isValidImage = remember { mutableStateOf(false) }
    LaunchedEffect(uiState.article.metadata.imageUrl) {
        isValidImage.value = uiState.article.metadata.imageUrl?.let { url ->
            coroutineScope.async {
                isValidImageUrl(context, url)
            }.await()
        } ?: false
    }


    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            modifier = Modifier
                .requiredHeight(50.dp)
                .shadow(
                    elevation = 16.dp,
                    spotColor = Color.DarkGray
                )
                .zIndex(1f),
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
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
            },
            actions = {
                Row {
                    val currentCtx = LocalContext.current
                    IconButton(
                        onClick = { onToBrowser(currentCtx) },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.OpenInBrowser,
                            contentDescription = "open in browser",
                        )
                    }
                }
            }
        )
        Box(
            modifier = Modifier
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
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                } else {
                    if (isValidImage.value) {
                        ImageFromUrl(
                            url = uiState.article.metadata.imageUrl!!,
                            contentDescription = "article image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(ReaderUiConfig.ARTICLE_IMG_HEIGHT.dp + 40.dp)
                                .align(Alignment.TopCenter),
                        )
                    }
                    ReaderScreenContentForNewArticle(
                        firstSpacerHeight = if (!isValidImage.value) {
                            0.dp
                        } else {
                            ReaderUiConfig.ARTICLE_IMG_HEIGHT.dp
                        },
                        uiState = uiState,
                        onToBrowser = onToBrowser
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
@Composable
fun ReaderScreenContentForNewArticle(
    modifier: Modifier = Modifier,
    firstSpacerHeight: Dp,
    uiState: NewReaderUiState,
    onToBrowser: (ctx: Context) -> Unit,
) {
    val metadata = uiState.article.metadata
    val summary = uiState.article.summary
    val content = uiState.article.content.content

    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        Spacer(modifier = Modifier.height(firstSpacerHeight))
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = if (firstSpacerHeight != 0.dp) {
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            } else {
                RoundedCornerShape(0.dp)
            },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        start = UiConfig.sideScreenPadding,
                        end = UiConfig.sideScreenPadding,
                        top = 16.dp,
                        bottom = 50.dp,
                    )
                    .background(MaterialTheme.colorScheme.surface),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = metadata.title.trim(),
                    style = ReaderTextStyle.title,
                )
                if (summary != "") {
                    SummaryCard(
                        summary = summary.trim(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (content != "") {
                    MarkdownText(
                        markdown = content.trim(),
                        fontResource = ReaderTextStyle.bodyResource,
                    )
                } else {
                    val annotatedString = buildAnnotatedString {
                        append("Sadly, we could not load the article. You will need to find the article fresh ")

                        pushStringAnnotation(tag = "url", annotation = metadata.url)
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontStyle = FontStyle.Italic,
                            )
                        ) {
                            append("on its website")
                        }
                        pop()

                        append(".")
                    }
                    val currentCtx = LocalContext.current
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
                                tag = "url",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let {
                                onToBrowser(currentCtx)
                            }
                        }
                    )
                }
            }
        }
    }
}