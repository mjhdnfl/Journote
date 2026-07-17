package com.naufal.cheddar.ui.screens.settings

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    onBack: () -> Unit,
    onThemeChange: (ThemeOption) -> Unit,
    onAmoledToggle: () -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("journote_prefs", Context.MODE_PRIVATE) }

    val isSystemDark = isSystemInDarkTheme()

    // State management
    var isAmoled by remember {
        mutableStateOf(sharedPrefs.getBoolean("is_amoled_enabled", false))
    }
    var isDynamic by remember {
        mutableStateOf(sharedPrefs.getBoolean("is_dynamic_enabled", true))
    }

    // Read saved theme, defaulting to SYSTEM
    var currentTheme by remember {
        val savedTheme = sharedPrefs.getString("app_theme", ThemeOption.SYSTEM.name) ?: ThemeOption.SYSTEM.name
        mutableStateOf(ThemeOption.valueOf(savedTheme))
    }

    // Logic to determine if True Black toggle should be visible
    val showTrueBlack = currentTheme == ThemeOption.DARK || (currentTheme == ThemeOption.SYSTEM && isSystemDark)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Theme Row (Header)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6750A4)), // Deep Purple
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.InvertColors,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Theme",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Theme Segmented Buttons
                    val themeOptions = listOf(ThemeOption.LIGHT, ThemeOption.DARK, ThemeOption.SYSTEM)
                    val themeLabels = listOf("Light", "Dark", "System")

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp)
                    ) {
                        themeOptions.forEachIndexed { index, option ->
                            SegmentedButton(
                                selected = currentTheme == option,
                                onClick = {
                                    currentTheme = option
                                    sharedPrefs.edit { putString("app_theme", option.name) }
                                    onThemeChange(option)
                                },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = themeOptions.size)
                            ) {
                                Text(themeLabels[index])
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = showTrueBlack,
                        enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                    ) {
                        Column {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF34495E)), // Dark Slate
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DarkMode,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "True Black",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Pitch black for OLED displays",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = isAmoled,
                                    onCheckedChange = { isChecked ->
                                        isAmoled = isChecked
                                        sharedPrefs.edit { putBoolean("is_amoled_enabled", isChecked) }
                                        onAmoledToggle()
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    )

                    // Dynamic Colors Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2980B9)), // Dynamic Blue
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ColorLens,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Dynamic",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = isDynamic,
                            onCheckedChange = { isChecked ->
                                isDynamic = isChecked
                                sharedPrefs.edit { putBoolean("is_dynamic_enabled", isChecked) }
                                // TODO: Trigger dynamic theme engine update if needed
                            }
                        )
                    }
                }
            }
        }
    }
}