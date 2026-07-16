package com.naufal.cheddar.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class ThemeOption(val label: String) {
    SYSTEM("System Default"),
    LIGHT("Light"),
    DARK("Dark")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToData: () -> Unit // This was missing!
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {

                    SettingsBlockItem(
                        title = "Notifications",
                        subtitle = "Reminders and summaries",
                        icon = Icons.Default.Notifications,
                        iconBgColor = Color(214, 145, 84),
                        onClick = { /* TODO */ }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)

                    SettingsBlockItem(
                        title = "Appearance",
                        subtitle = "Themes, layout, and visuals",
                        icon = Icons.Default.Palette,
                        iconBgColor = Color(0xFF8E5A69),
                        onClick = onNavigateToAppearance
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)

                    SettingsBlockItem(
                        title = "Security",
                        subtitle = "App lock and biometric authentication",
                        icon = Icons.Default.Lock,
                        iconBgColor = Color(0xFF4A5C7A),
                        onClick = { /* TODO */ }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)

                    SettingsBlockItem(
                        title = "Data",
                        subtitle = "Trash, backup, and sync",
                        icon = Icons.Default.Storage,
                        iconBgColor = Color(0xFFD4A373),
                        onClick = onNavigateToData
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)

                    SettingsBlockItem(
                        title = "Language",
                        subtitle = "English",
                        icon = Icons.Default.Language,
                        iconBgColor = Color(0xFF5A7A69),
                        onClick = { /* TODO */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsBlockItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}