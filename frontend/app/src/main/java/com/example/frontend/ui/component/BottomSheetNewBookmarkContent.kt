package com.example.frontend.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.frontend.ui.theme.UiConfig

@Composable
fun BottomSheetNewBookmarkContent(
    modifier: Modifier = Modifier,
    maxChar: Int = 50,
    onCreateNewBookmark: (name: String) -> Unit,
    onClose: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    val onDone = {
        if (name.isNotBlank())
            onCreateNewBookmark(name)
        name = ""
    }
    Column(
        modifier = modifier
            .padding(
                start = UiConfig.sideScreenPadding,
                end = UiConfig.sideScreenPadding,
                bottom = 16.dp,
            ),
    ) {
        Text(
            text = "New Bookmark",
            style = MaterialTheme.typography.titleMedium,
        )
        TextField(
            value = name,
            onValueChange = {
                if (it.length <= maxChar) name = it
            },
            label = { Text("Name") },
            singleLine = true,
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDone()
                }
            ),
            supportingText = {
                Text(
                    text = "${name.length} / $maxChar",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Cancel",
                )
            }
            IconButton(onClick = onDone) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Create new bookmark",
                )
            }
        }
    }
}