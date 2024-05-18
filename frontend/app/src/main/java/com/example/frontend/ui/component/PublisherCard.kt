package com.example.frontend.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.frontend.utils.isValidUrl

@Composable
fun PublisherCard(
    modifier: Modifier = Modifier,
    name: String,
    avatarUrl: String?,
    timestamp: String?,
    isFollowing: Boolean,
    onFollowClick: (isFollowing: Boolean) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isValidUrl(avatarUrl)) {
                ImageFromUrl(
                    url = avatarUrl!!,
                    contentDescription = "Avatar of $name",
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .requiredSize(50.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = name,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                timestamp?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }
            }
        }
        if (isFollowing) {
            Button(
                onClick = { onFollowClick(true) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = "Following",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    )
                )
            }
        } else {
            Button(
                onClick = { onFollowClick(false) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Follow",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    )
                )
            }
        }
    }
}
