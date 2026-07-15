package com.naufal.cheddar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.naufal.cheddar.data.AppDatabase
import com.naufal.cheddar.ui.navigation.JournoteApp
import com.naufal.cheddar.ui.theme.JournoteTheme
import com.naufal.cheddar.ui.screens.settings.ThemeOption
import com.naufal.cheddar.ui.viewmodel.JournoteViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val database = AppDatabase.getDatabase(this)
        val viewModelFactory = JournoteViewModelFactory(database.noteDao())

        setContent {
            // State for the 3-way theme
            var themeOption by remember { mutableStateOf(ThemeOption.SYSTEM) }
            var isAmoledTheme by remember { mutableStateOf(false) }

            // Determine if the app should actually be dark based on the option selected
            val systemDarkTheme = isSystemInDarkTheme()
            val isDarkTheme = when (themeOption) {
                ThemeOption.SYSTEM -> systemDarkTheme
                ThemeOption.LIGHT -> false
                ThemeOption.DARK -> true
            }

            JournoteTheme(
                darkTheme = isDarkTheme,
                amoledTheme = isAmoledTheme,
                dynamicColor = true
            ) {
                JournoteApp(
                    viewModelFactory = viewModelFactory,
                    onThemeChange = { newTheme -> themeOption = newTheme }, // Pass the function down
                    onAmoledToggle = { isAmoledTheme = !isAmoledTheme }
                )
            }
        }
    }
}