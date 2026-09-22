package com.shalah.prayer.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * App Theme Mode options: System Default, Light, Dark
 */
enum class AppThemeMode(val key: String, val label: String, val description: String) {
    SYSTEM("system", "Sistem", "Mengikuti tema bawaan perangkat"),
    LIGHT("light", "Terang", "Tampilan cerah & bersih"),
    DARK("dark", "Gelap", "Tampilan hitam pekat elegan");

    companion object {
        fun fromKey(key: String?): AppThemeMode {
            return entries.find { it.key == key } ?: SYSTEM
        }
    }
}

/**
 * Design system semantic colors for Shalah
 */
data class ShalahColors(
    val canvasBackground: Color,
    val surfaceCard: Color,
    val surfaceCardPressed: Color,
    val surfacePill: Color,
    val surfacePillBorder: Color,
    val pureBlack: Color,
    val onPureBlack: Color,
    val charcoalBlack: Color,
    val grayTextPrimary: Color,
    val grayTextSecondary: Color,
    val grayTextMuted: Color,
    val dividerColor: Color,
    val progressTrack: Color,
    val progressIndicator: Color,
    val greenSuccess: Color,
    val blueAccent: Color,
    val warmCanvas: Color,
    val activePrayerHighlight: Color,
    val softBorder: Color,
    val accentGold: Color,
    val isDark: Boolean
)

val LightShalahColors = ShalahColors(
    canvasBackground = Color(0xFFF7F6F2),
    surfaceCard = Color(0xFFFFFFFF),
    surfaceCardPressed = Color(0xFFEFECE5),
    surfacePill = Color(0xFFEFECE5),
    surfacePillBorder = Color.Transparent,
    pureBlack = Color(0xFF143E33),
    onPureBlack = Color(0xFFFFFFFF),
    charcoalBlack = Color(0xFF191C1A),
    grayTextPrimary = Color(0xFF191C1A),
    grayTextSecondary = Color(0xFF5A6660),
    grayTextMuted = Color(0xFF8A9791),
    dividerColor = Color(0xFFE8E5DC),
    progressTrack = Color(0xFFE5E2D8),
    progressIndicator = Color(0xFF143E33),
    greenSuccess = Color(0xFF15803D),
    blueAccent = Color(0xFF0F766E),
    warmCanvas = Color(0xFFEFECE5),
    activePrayerHighlight = Color(0xFFE8F1EC),
    softBorder = Color.Transparent,
    accentGold = Color(0xFFC29B38),
    isDark = false
)

val DarkShalahColors = ShalahColors(
    canvasBackground = Color(0xFF0B100E),
    surfaceCard = Color(0xFF131916),
    surfaceCardPressed = Color(0xFF1A231F),
    surfacePill = Color(0xFF18221D),
    surfacePillBorder = Color.Transparent,
    pureBlack = Color(0xFF34D399),
    onPureBlack = Color(0xFF0B100E),
    charcoalBlack = Color(0xFFF0FDF4),
    grayTextPrimary = Color(0xFFF0FDF4),
    grayTextSecondary = Color(0xFF94A89E),
    grayTextMuted = Color(0xFF62756B),
    dividerColor = Color(0xFF1C2621),
    progressTrack = Color(0xFF1C2822),
    progressIndicator = Color(0xFF34D399),
    greenSuccess = Color(0xFF10B981),
    blueAccent = Color(0xFF2DD4BF),
    warmCanvas = Color(0xFF111714),
    activePrayerHighlight = Color(0xFF1A2B23),
    softBorder = Color.Transparent,
    accentGold = Color(0xFFFBBF24),
    isDark = true
)

val LocalShalahColors = staticCompositionLocalOf { LightShalahColors }

// Dynamic Composable color getters for seamless integration
val CanvasBackground: Color @Composable get() = LocalShalahColors.current.canvasBackground
val SurfaceCard: Color @Composable get() = LocalShalahColors.current.surfaceCard
val SurfaceCardPressed: Color @Composable get() = LocalShalahColors.current.surfaceCardPressed
val SurfacePill: Color @Composable get() = LocalShalahColors.current.surfacePill
val SurfacePillBorder: Color @Composable get() = LocalShalahColors.current.surfacePillBorder
val PureBlack: Color @Composable get() = LocalShalahColors.current.pureBlack
val OnPureBlack: Color @Composable get() = LocalShalahColors.current.onPureBlack
val CharcoalBlack: Color @Composable get() = LocalShalahColors.current.charcoalBlack
val GrayTextPrimary: Color @Composable get() = LocalShalahColors.current.grayTextPrimary
val GrayTextSecondary: Color @Composable get() = LocalShalahColors.current.grayTextSecondary
val GrayTextMuted: Color @Composable get() = LocalShalahColors.current.grayTextMuted
val DividerColor: Color @Composable get() = LocalShalahColors.current.dividerColor
val ProgressTrack: Color @Composable get() = LocalShalahColors.current.progressTrack
val ProgressIndicator: Color @Composable get() = LocalShalahColors.current.progressIndicator
val GreenSuccess: Color @Composable get() = LocalShalahColors.current.greenSuccess
val BlueAccent: Color @Composable get() = LocalShalahColors.current.blueAccent
val WarmCanvas: Color @Composable get() = LocalShalahColors.current.warmCanvas
val ActivePrayerHighlight: Color @Composable get() = LocalShalahColors.current.activePrayerHighlight
val SoftBorder: Color @Composable get() = LocalShalahColors.current.softBorder
val AccentGold: Color @Composable get() = LocalShalahColors.current.accentGold
