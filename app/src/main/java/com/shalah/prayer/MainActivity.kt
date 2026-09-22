package com.shalah.prayer

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shalah.prayer.ui.screens.HomeScreen
import com.shalah.prayer.ui.theme.AppThemeMode
import com.shalah.prayer.ui.theme.ShalahTheme
import com.shalah.prayer.ui.viewmodel.PrayerViewModel
import com.shalah.prayer.widget.CompactPrayerWidgetProvider
import com.shalah.prayer.widget.PrayerWidgetProvider

class MainActivity : ComponentActivity() {

    private val viewModel: PrayerViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as ShalahApp
                return PrayerViewModel(app.repository) as T
            }
        }
    }

    private var currentThemeMode by mutableStateOf(AppThemeMode.SYSTEM)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("shalah_user_prefs", Context.MODE_PRIVATE)
        val savedThemeKey = prefs.getString("app_theme_mode", AppThemeMode.SYSTEM.key)
        currentThemeMode = AppThemeMode.fromKey(savedThemeKey)

        setContent {
            ShalahTheme(themeMode = currentThemeMode) {
                HomeScreen(
                    viewModel = viewModel,
                    currentThemeMode = currentThemeMode,
                    onThemeChanged = { newMode ->
                        currentThemeMode = newMode
                        prefs.edit().putString("app_theme_mode", newMode.key).apply()
                        PrayerWidgetProvider.updateAllWidgets(this)
                        CompactPrayerWidgetProvider.updateAllWidgets(this)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh widget state whenever user returns to app
        PrayerWidgetProvider.updateAllWidgets(this)
        CompactPrayerWidgetProvider.updateAllWidgets(this)
    }
}
