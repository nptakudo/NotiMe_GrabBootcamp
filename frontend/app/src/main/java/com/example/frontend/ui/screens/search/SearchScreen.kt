package com.example.frontend.ui.screens.search

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.ui.theme.Colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen (
    onSearch: (String) -> Unit,
    onBack: () -> Unit
) {
    val searchString = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.SemiBold
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
                    start = 12.dp,
                    end = 12.dp
                )
                .fillMaxSize()
        ) {
            SearchBar(
                searchString = searchString,
                onSearch = onSearch
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchString: MutableState<String>,
    onSearch: (String) -> Unit
) {
    TextField(
        value = searchString.value,
        onValueChange = {
            searchString.value = it
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 24.dp,
                horizontal = 12.dp
            )
            .height(48.dp),
        placeholder = {
            Text(
                "Enter search term",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            ) },
        leadingIcon = {
            IconButton(
                onClick = {
                    onSearch(searchString.value)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            }
        },
        shape = RoundedCornerShape(26.dp),
        singleLine = true,
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 16.sp,
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Colors.topBarContainer,
            unfocusedContainerColor = Colors.topBarContainer,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}
