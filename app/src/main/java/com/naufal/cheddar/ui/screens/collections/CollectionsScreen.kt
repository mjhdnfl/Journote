package com.naufal.cheddar.ui.screens.collections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen() {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Library") },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2-column grid
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- Permanent Folders ---
            item {
                CollectionFolderCard(
                    title = "Favorites",
                    icon = Icons.Default.Favorite,
                    iconTint = MaterialTheme.colorScheme.error, // Red for favorites
                    itemCount = 0, // We will wire this to the DB later
                    onClick = { /* TODO: Navigate to Favorites Filter */ }
                )
            }
            item {
                CollectionFolderCard(
                    title = "Archive",
                    icon = Icons.Default.Archive,
                    iconTint = MaterialTheme.colorScheme.primary,
                    itemCount = 0,
                    onClick = { /* TODO: Navigate to Archive Filter */ }
                )
            }
            item {
                CollectionFolderCard(
                    title = "Trash",
                    icon = Icons.Default.Delete,
                    iconTint = MaterialTheme.colorScheme.secondary,
                    itemCount = 0,
                    onClick = { /* TODO: Navigate to Trash Filter */ }
                )
            }

            // --- Custom Tags / Folders Section ---
            // This item spans across both columns to act as a header
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "Your Tags",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Placeholder for a custom tag
            item {
                CollectionFolderCard(
                    title = "Ideas",
                    icon = Icons.Outlined.Tag,
                    iconTint = MaterialTheme.colorScheme.tertiary,
                    itemCount = 3,
                    onClick = { /* TODO: Navigate to Tag Filter */ }
                )
            }
        }
    }
}

// Reusable Component for the Grid Items
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionFolderCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    itemCount: Int,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        // aspect ratio 1f makes it a perfect square
        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$itemCount entries",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}