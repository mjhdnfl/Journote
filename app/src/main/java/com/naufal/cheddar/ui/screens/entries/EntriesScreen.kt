package com.naufal.cheddar.ui.screens.entries

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.naufal.cheddar.data.Note
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriesScreen(
    notes: List<Note>,
    hasSeenSplash: Boolean,
    onSplashFinished: () -> Unit,
    onMenuClick: () -> Unit,
    onFabClick: () -> Unit,
    onEntryClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val isScrolled by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0 } }

    // State to track if the FAB menu is open
    var isFabExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(hasSeenSplash) {
        if (!hasSeenSplash) {
            delay(2500)
            onSplashFinished()
        }
    }

    val showLargeTitle = !hasSeenSplash && !isScrolled

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                // The expandable menu options
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                    exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        FabOption(label = "Image", icon = Icons.Outlined.Image) { /* TODO */ }
                        FabOption(label = "Drawing", icon = Icons.Outlined.Brush) { /* TODO */ }
                        FabOption(label = "Audio", icon = Icons.Outlined.Mic) { /* TODO */ }
                        FabOption(label = "List", icon = Icons.Outlined.CheckBox) { /* TODO */ }
                        FabOption(label = "Text", icon = Icons.Outlined.TextFields) {
                            isFabExpanded = false
                            onFabClick() // Launch normal editor
                        }
                    }
                }

                // The Main FAB
                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(24.dp),
                    // Increased size to match Google Keep's chunkier button
                    modifier = Modifier.size(68.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
                ) {
                    // Smoothly animate between the Edit pencil and a Close X
                    Crossfade(targetState = isFabExpanded, label = "fab_icon") { expanded ->
                        if (expanded) {
                            Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(28.dp))
                        } else {
                            Icon(Icons.Default.Edit, contentDescription = "New Entry", modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            AnimatedVisibility(
                visible = showLargeTitle,
                enter = fadeIn(spring(stiffness = Spring.StiffnessLow)) + expandVertically(spring(stiffness = Spring.StiffnessLow)),
                exit = fadeOut(spring(stiffness = Spring.StiffnessLow)) + shrinkVertically(spring(stiffness = Spring.StiffnessLow))
            ) {
                Text(
                    text = "Journote",
                    // Also enforced the Black weight here just in case!
                    style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp, bottom = 24.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f).height(52.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Search entries...", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { }, modifier = Modifier.clip(CircleShape).size(40.dp)) { Icon(Icons.Default.Person, contentDescription = "Account") }
            }

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                items(notes) { note ->
                    ElevatedCard(
                        onClick = { onEntryClick(note.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(note.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(note.content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3)
                        }
                    }
                }
            }
        }
    }
}

// Custom reusable component for the pop-up pill buttons
@Composable
private fun FabOption(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = onClick,
        shadowElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}