package com.naufal.cheddar

import android.content.Context
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

        // Initialize SharedPreferences to store small data persistently
        val sharedPrefs = getSharedPreferences("journote_prefs", Context.MODE_PRIVATE)

        setContent {
            // Read saved values when the app starts, defaulting to SYSTEM and false
            val savedTheme = sharedPrefs.getString("theme", ThemeOption.SYSTEM.name) ?: ThemeOption.SYSTEM.name
            val savedAmoled = sharedPrefs.getBoolean("amoled", false)

            var themeOption by remember { mutableStateOf(ThemeOption.valueOf(savedTheme)) }
            var isAmoledTheme by remember { mutableStateOf(savedAmoled) }

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
                    onThemeChange = { newTheme ->
                        themeOption = newTheme
                        // Save the choice instantly
                        sharedPrefs.edit().putString("theme", newTheme.name).apply()
                    },
                    onAmoledToggle = {
                        isAmoledTheme = !isAmoledTheme
                        // Save the choice instantly
                        sharedPrefs.edit().putBoolean("amoled", isAmoledTheme).apply()
                    }
                )
            }
        }
    }
}