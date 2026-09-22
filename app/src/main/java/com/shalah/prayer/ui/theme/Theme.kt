package com.shalah.prayer.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private fun createMaterialColorScheme(colors: ShalahColors) = if (colors.isDark) {
    darkColorScheme(
        primary = colors.pureBlack,
        onPrimary = colors.onPureBlack,
        primaryContainer = colors.surfaceCard,
        onPrimaryContainer = colors.pureBlack,
        secondary = colors.charcoalBlack,
        onSecondary = colors.onPureBlack,
        background = colors.canvasBackground,
        onBackground = colors.grayTextPrimary,
        surface = colors.surfaceCard,
        onSurface = colors.grayTextPrimary,
        surfaceVariant = colors.canvasBackground,
        onSurfaceVariant = colors.grayTextSecondary,
        outline = colors.dividerColor
    )
} else {
    lightColorScheme(
        primary = colors.pureBlack,
        onPrimary = colors.onPureBlack,
        primaryContainer = colors.surfaceCard,
        onPrimaryContainer = colors.pureBlack,
        secondary = colors.charcoalBlack,
        onSecondary = colors.onPureBlack,
        background = colors.canvasBackground,
        onBackground = colors.grayTextPrimary,
        surface = colors.surfaceCard,
        onSurface = colors.grayTextPrimary,
        surfaceVariant = colors.canvasBackground,
        onSurfaceVariant = colors.grayTextSecondary,
        outline = colors.dividerColor
    )
}

@Composable
fun ShalahTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> systemInDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val currentColors = if (isDark) DarkShalahColors else LightShalahColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = currentColors.canvasBackground.toArgb()
            window.navigationBarColor = currentColors.canvasBackground.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDark
            insetsController.isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(LocalShalahColors provides currentColors) {
        MaterialTheme(
            colorScheme = createMaterialColorScheme(currentColors),
            typography = Typography,
            content = content
        )
    }
}
