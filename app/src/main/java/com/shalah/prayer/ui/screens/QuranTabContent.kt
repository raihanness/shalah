package com.shalah.prayer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shalah.prayer.data.model.JuzInfo
import com.shalah.prayer.data.model.QuranDailyLog
import com.shalah.prayer.data.model.QuranData
import com.shalah.prayer.data.model.QuranProgress
import com.shalah.prayer.data.model.Surah
import com.shalah.prayer.ui.theme.AccentGold
import com.shalah.prayer.ui.theme.ActivePrayerHighlight
import com.shalah.prayer.ui.theme.CanvasBackground
import com.shalah.prayer.ui.theme.CharcoalBlack
import com.shalah.prayer.ui.theme.DividerColor
import com.shalah.prayer.ui.theme.GrayTextMuted
import com.shalah.prayer.ui.theme.GrayTextPrimary
import com.shalah.prayer.ui.theme.GrayTextSecondary
import com.shalah.prayer.ui.theme.GreenSuccess
import com.shalah.prayer.ui.theme.OnPureBlack
import com.shalah.prayer.ui.theme.PlusJakartaSansFamily
import com.shalah.prayer.ui.theme.ProgressTrack
import com.shalah.prayer.ui.theme.PureBlack
import com.shalah.prayer.ui.theme.SurfaceCard
import com.shalah.prayer.ui.theme.SurfaceCardPressed
import com.shalah.prayer.ui.theme.SurfacePill
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranTabContent(
    progress: QuranProgress,
    todayLog: QuranDailyLog?,
    recentLogs: List<QuranDailyLog>,
    streak: Int,
    onUpdateBookmark: (surahNumber: Int, ayahNumber: Int, pageNumber: Int) -> Unit,
    onAddPagesRead: (pages: Int) -> Unit,
    onSetKhatamTarget: (days: Int) -> Unit,
    onCompleteKhatam: () -> Unit,
    onResetProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    var showSurahSheet by remember { mutableStateOf(false) }
    var showTargetSheet by remember { mutableStateOf(false) }
    var showManualPageDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val currentSurah = remember(progress.lastSurahNumber) {
        QuranData.getSurahByNumber(progress.lastSurahNumber)
    }

    val todayPages = todayLog?.pagesRead ?: 0
    val targetPagesToday = progress.pagesPerDayTarget
    val isTargetMetToday = todayPages >= targetPagesToday

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Atmospheric Sky Header Section (Title + Reading Streak Badge)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tilawah & Khatam",
                        fontSize = 32.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = PureBlack,
                        letterSpacing = (-0.8).sp
                    )

                    // Streak Badge
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SurfaceCard,
                        modifier = Modifier.clip(RoundedCornerShape(50))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$streak Hari",
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                color = PureBlack
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pantau target khatam dan tanda baca Mushaf Anda",
                    fontSize = 12.5.sp,
                    fontFamily = PlusJakartaSansFamily,
                    color = GrayTextSecondary
                )
            }
        }

        // Hero Radial Khatam Progress Ring Card
        item {
            HeroKhatamCard(
                progress = progress,
                onTargetClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showTargetSheet = true
                },
                onCompleteClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showCompleteDialog = true
                }
            )
        }

        // Active Bookmark Card ("Tanda Baca Terakhir")
        item {
            ActiveBookmarkCard(
                surah = currentSurah,
                progress = progress,
                onBookmarkClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showSurahSheet = true
                },
                onEditPageClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showManualPageDialog = true
                }
            )
        }

        // Daily Target & Fast Logging Section
        item {
            DailyTargetQuickLogCard(
                todayPages = todayPages,
                targetPages = targetPagesToday,
                pagesPerPrayer = progress.pagesPerPrayerTarget,
                isTargetMet = isTargetMetToday,
                onAddPages = { delta ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddPagesRead(delta)
                }
            )
        }

        // 7-Day Consistency Momentum Strip
        item {
            WeeklyMomentumCard(
                recentLogs = recentLogs,
                targetPages = targetPagesToday
            )
        }

        // Secondary Actions & Reset Footer
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = SurfaceCard,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showTargetSheet = true
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null,
                            tint = PureBlack,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ganti Target (${progress.targetKhatamDays} Hari)",
                            fontSize = 11.5.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = PureBlack
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = SurfaceCard,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showResetDialog = true
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = GrayTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reset Bacaan",
                            fontSize = 11.5.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = GrayTextSecondary
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets and Dialogs
    if (showSurahSheet) {
        SurahSelectionSheet(
            currentSurahNumber = progress.lastSurahNumber,
            currentAyahNumber = progress.lastAyahNumber,
            currentPage = progress.lastPageNumber,
            onDismiss = { showSurahSheet = false },
            onSelectSurah = { surah, ayah, page ->
                onUpdateBookmark(surah.number, ayah, page)
                showSurahSheet = false
            }
        )
    }

    if (showTargetSheet) {
        KhatamTargetSheet(
            currentDays = progress.targetKhatamDays,
            onDismiss = { showTargetSheet = false },
            onSelectDays = { days ->
                onSetKhatamTarget(days)
                showTargetSheet = false
            }
        )
    }

    if (showManualPageDialog) {
        ManualPageInputDialog(
            currentPage = progress.lastPageNumber,
            onDismiss = { showManualPageDialog = false },
            onConfirm = { newPage ->
                val surah = QuranData.getSurahByPage(newPage)
                onUpdateBookmark(surah.number, 1, newPage)
                showManualPageDialog = false
            }
        )
    }

    if (showCompleteDialog) {
        KhatamCelebrationDialog(
            currentKhatamCount = progress.completedKhatamCount,
            onDismiss = { showCompleteDialog = false },
            onConfirm = {
                onCompleteKhatam()
                showCompleteDialog = false
            }
        )
    }

    if (showResetDialog) {
        ResetQuranDialog(
            onDismiss = { showResetDialog = false },
            onConfirm = {
                onResetProgress()
                showResetDialog = false
            }
        )
    }
}

// =========================================================================
// HERO KHATAM PROGRESS CARD (Radial Indicator & Target Stats)
// =========================================================================

@Composable
private fun HeroKhatamCard(
    progress: QuranProgress,
    onTargetClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    val animatedPercent by animateFloatAsState(
        targetValue = progress.progressPercent,
        animationSpec = tween(durationMillis = 800),
        label = "KhatamProgress"
    )

    Surface(
        shape = RoundedCornerShape(26.dp),
        color = SurfaceCard,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AutoStories,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Perjalanan Khatam",
                        fontSize = 15.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = PureBlack
                    )
                }

                if (progress.completedKhatamCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = AccentGold.copy(alpha = 0.12f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EmojiEvents,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Khatam ${progress.completedKhatamCount}x",
                                fontSize = 11.sp,
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                color = AccentGold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Radial Progress Canvas
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(116.dp)
                ) {
                    val progressTrackColor = ProgressTrack
                    val indicatorColor = PureBlack
                    val goldColor = AccentGold

                    Canvas(modifier = Modifier.size(116.dp)) {
                        val strokeWidth = 10.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
                        val arcSize = Size(diameter, diameter)

                        // Background full circle
                        drawArc(
                            color = progressTrackColor,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Foreground progress arc
                        val sweepAngle = (animatedPercent / 100f) * 360f
                        drawArc(
                            color = if (animatedPercent >= 100f) goldColor else indicatorColor,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f%%", animatedPercent),
                            fontSize = 19.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            color = PureBlack
                        )
                        Text(
                            text = "Selesai",
                            fontSize = 10.5.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = GrayTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Stats breakdown
                Column(modifier = Modifier.weight(1f)) {
                    StatPill(
                        label = "Posisi",
                        value = "Juz ${progress.lastJuzNumber} • Hal ${progress.lastPageNumber}/604"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatPill(
                        label = "Sisa",
                        value = "${progress.remainingPages} Halaman Lagi"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatPill(
                        label = "Target",
                        value = "${progress.targetKhatamDays} Hari (${progress.pagesPerDayTarget} hal/hari)",
                        onClick = onTargetClick
                    )
                }
            }

            if (progress.lastPageNumber >= 604) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GreenSuccess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onCompleteClick() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Alhamdulillah, Selesaikan Khatam 🎉",
                            fontSize = 13.5.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfacePill,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.SemiBold,
                color = GrayTextSecondary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    fontSize = 11.5.sp,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (onClick != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = GrayTextMuted,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}

// =========================================================================
// ACTIVE BOOKMARK CARD (Tanda Baca Terakhir)
// =========================================================================

@Composable
private fun ActiveBookmarkCard(
    surah: Surah,
    progress: QuranProgress,
    onBookmarkClick: () -> Unit,
    onEditPageClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = SurfaceCard,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tanda Baca Terakhir",
                        fontSize = 15.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = PureBlack
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = SurfacePill,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onEditPageClick() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = PureBlack,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ubah Hal",
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = PureBlack
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Surah Box & Arabic Calligraphy
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = ActivePrayerHighlight,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onBookmarkClick() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "QS. ${surah.number}: ${surah.nameLatin}",
                                fontSize = 17.sp,
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                color = PureBlack
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Ayat ${progress.lastAyahNumber} • Halaman ${progress.lastPageNumber} • Juz ${progress.lastJuzNumber}",
                            fontSize = 12.5.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = GrayTextSecondary
                        )
                        Text(
                            text = "${surah.translationId} • ${if (surah.isMakkiyyah) "Makkiyyah" else "Madaniyyah"}",
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaSansFamily,
                            color = GrayTextMuted
                        )
                    }

                    // Arabic Surah Calligraphy
                    Text(
                        text = surah.nameArabic,
                        fontSize = 24.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = PureBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action: Browse all 114 Surahs
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PureBlack,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onBookmarkClick() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        tint = OnPureBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Buka Daftar Surah & Juz",
                        fontSize = 13.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = OnPureBlack
                    )
                }
            }
        }
    }
}

// =========================================================================
// DAILY TARGET & FAST LOGGING CARD
// =========================================================================

@Composable
private fun DailyTargetQuickLogCard(
    todayPages: Int,
    targetPages: Int,
    pagesPerPrayer: Int,
    isTargetMet: Boolean,
    onAddPages: (Int) -> Unit
) {
    val progressRatio = if (targetPages > 0) (todayPages.toFloat() / targetPages.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedRatio by animateFloatAsState(targetValue = progressRatio, label = "dailyRatio")

    Surface(
        shape = RoundedCornerShape(26.dp),
        color = SurfaceCard,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Speed,
                        contentDescription = null,
                        tint = if (isTargetMet) GreenSuccess else PureBlack,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Target Tilawah Hari Ini",
                        fontSize = 15.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = PureBlack
                    )
                }

                Text(
                    text = "$todayPages / $targetPages Hal",
                    fontSize = 14.sp,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isTargetMet) GreenSuccess else PureBlack
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Linear Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(ProgressTrack)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedRatio)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (isTargetMet) GreenSuccess else PureBlack)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Add Action Buttons
            Text(
                text = "Catat Bacaan Cepat:",
                fontSize = 12.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.SemiBold,
                color = GrayTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickAddButton(
                    label = "+1 Hal",
                    sub = "1 Halaman",
                    modifier = Modifier.weight(1f),
                    onClick = { onAddPages(1) }
                )
                QuickAddButton(
                    label = "+2 Hal",
                    sub = "1 Lembar",
                    modifier = Modifier.weight(1f),
                    onClick = { onAddPages(2) }
                )
                QuickAddButton(
                    label = "+4 Hal",
                    sub = "2 Lembar",
                    modifier = Modifier.weight(1f),
                    onClick = { onAddPages(4) }
                )
                QuickAddButton(
                    label = "+20 Hal",
                    sub = "1 Juz",
                    modifier = Modifier.weight(1f),
                    onClick = { onAddPages(20) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Smart Prayer Tip Helper
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ActivePrayerHighlight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Tips: Cukup baca $pagesPerPrayer halaman (±${((pagesPerPrayer + 1) / 2)} lembar) ba'da shalat fardhu untuk penuhi target hari ini.",
                        fontSize = 11.5.sp,
                        fontFamily = PlusJakartaSansFamily,
                        color = GrayTextPrimary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickAddButton(
    label: String,
    sub: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfacePill,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 13.5.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.ExtraBold,
                color = PureBlack
            )
            Text(
                text = sub,
                fontSize = 9.5.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Medium,
                color = GrayTextSecondary
            )
        }
    }
}

// =========================================================================
// 7-DAY READING MOMENTUM MATRIX
// =========================================================================

@Composable
private fun WeeklyMomentumCard(
    recentLogs: List<QuranDailyLog>,
    targetPages: Int
) {
    val logsMap = remember(recentLogs) { recentLogs.associateBy { it.date } }
    val today = LocalDate.now()
    val last7Days = remember(today) {
        (6 downTo 0).map { today.minusDays(it.toLong()) }
    }

    Surface(
        shape = RoundedCornerShape(26.dp),
        color = SurfaceCard,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Konsistensi 7 Hari Terakhir",
                fontSize = 15.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                last7Days.forEach { date ->
                    val dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val log = logsMap[dateStr]
                    val pages = log?.pagesRead ?: 0
                    val isToday = date.isEqual(today)
                    val dayName = date.format(DateTimeFormatter.ofPattern("EEE", Locale("id", "ID")))

                    val barRatio = if (targetPages > 0) (pages.toFloat() / targetPages.toFloat()).coerceIn(0.1f, 1f) else 0.1f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(36.dp)
                    ) {
                        Text(
                            text = if (pages > 0) "$pages" else "-",
                            fontSize = 10.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = if (pages > 0) PureBlack else GrayTextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height((48 * barRatio).dp.coerceAtLeast(8.dp))
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when {
                                        pages >= targetPages -> GreenSuccess
                                        pages > 0 -> PureBlack
                                        else -> ProgressTrack
                                    }
                                )
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = dayName,
                            fontSize = 10.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                            color = if (isToday) PureBlack else GrayTextSecondary
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// SEARCHABLE SURAH & JUZ SELECTOR MODAL SHEET
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SurahSelectionSheet(
    currentSurahNumber: Int,
    currentAyahNumber: Int,
    currentPage: Int,
    onDismiss: () -> Unit,
    onSelectSurah: (Surah, Int, Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = 114 Surah, 1 = 30 Juz

    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) QuranData.SURAHS
        else {
            val q = searchQuery.trim().lowercase(Locale.getDefault())
            QuranData.SURAHS.filter {
                it.number.toString() == q ||
                it.nameLatin.lowercase(Locale.getDefault()).contains(q) ||
                it.translationId.lowercase(Locale.getDefault()).contains(q) ||
                it.nameArabic.contains(q)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Mushaf Al-Qur'an",
                    fontSize = 20.sp,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = PureBlack
                )
                Surface(
                    shape = CircleShape,
                    color = SurfacePill,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onDismiss() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = PureBlack,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search input field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Cari Surah (contoh: Al-Kahf, 18, Yasin)",
                        fontSize = 13.sp,
                        fontFamily = PlusJakartaSansFamily,
                        color = GrayTextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = GrayTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hapus",
                            tint = GrayTextSecondary,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { searchQuery = "" }
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PureBlack,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor = SurfacePill,
                    unfocusedContainerColor = SurfacePill
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Tabs: Surah vs Juz
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (selectedTab == 0) PureBlack else SurfacePill,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .clickable { selectedTab = 0 }
                ) {
                    Text(
                        text = "114 Surah",
                        fontSize = 12.5.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 0) OnPureBlack else GrayTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (selectedTab == 1) PureBlack else SurfacePill,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .clickable { selectedTab = 1 }
                ) {
                    Text(
                        text = "30 Juz",
                        fontSize = 12.5.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 1) OnPureBlack else GrayTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // List Items
            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSurahs, key = { it.number }) { surah ->
                        val isSelected = surah.number == currentSurahNumber

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) ActivePrayerHighlight else SurfacePill,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onSelectSurah(surah, 1, surah.startPage)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Surah Number Badge
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) PureBlack else DividerColor,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${surah.number}",
                                                fontSize = 11.5.sp,
                                                fontFamily = PlusJakartaSansFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isSelected) OnPureBlack else PureBlack
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = surah.nameLatin,
                                            fontSize = 14.sp,
                                            fontFamily = PlusJakartaSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = PureBlack
                                        )
                                        Text(
                                            text = "${surah.translationId} • ${surah.totalAyah} Ayat • Hal ${surah.startPage}",
                                            fontSize = 11.sp,
                                            fontFamily = PlusJakartaSansFamily,
                                            color = GrayTextSecondary
                                        )
                                    }
                                }

                                Text(
                                    text = surah.nameArabic,
                                    fontSize = 18.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = PureBlack
                                )
                            }
                        }
                    }
                }
            } else {
                // 30 Juz List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(30) { index ->
                        val juzNumber = index + 1
                        val startPage = QuranData.JUZ_START_PAGES[index]
                        val endPage = if (index < 29) QuranData.JUZ_START_PAGES[index + 1] - 1 else 604
                        val surah = QuranData.getSurahByPage(startPage)

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SurfacePill,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onSelectSurah(surah, 1, startPage)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = PureBlack,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "$juzNumber",
                                                fontSize = 12.sp,
                                                fontFamily = PlusJakartaSansFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = OnPureBlack
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = "Juz $juzNumber",
                                            fontSize = 14.sp,
                                            fontFamily = PlusJakartaSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = PureBlack
                                        )
                                        Text(
                                            text = "Mulai dari QS. ${surah.nameLatin} (Hal. $startPage - $endPage)",
                                            fontSize = 11.sp,
                                            fontFamily = PlusJakartaSansFamily,
                                            color = GrayTextSecondary
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = GrayTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// =========================================================================
// KHATAM TARGET SELECTION SHEET
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KhatamTargetSheet(
    currentDays: Int,
    onDismiss: () -> Unit,
    onSelectDays: (Int) -> Unit
) {
    val presets = listOf(
        Triple(15, "15 Hari (Ramadan Sprint)", "40 Halaman/Hari • 8 Hal/Shalat"),
        Triple(30, "30 Hari (Standar 1 Juz/Hari)", "20 Halaman/Hari • 4 Hal/Shalat"),
        Triple(60, "60 Hari (Santai 1/2 Juz/Hari)", "10 Halaman/Hari • 2 Hal/Shalat"),
        Triple(90, "90 Hari (3 Bulan)", "7 Halaman/Hari • 1-2 Hal/Shalat")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Pilih Target Khatam",
                fontSize = 20.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.ExtraBold,
                color = PureBlack
            )
            Text(
                text = "Atur durasi hari untuk menghitung target bacaan harian",
                fontSize = 12.5.sp,
                fontFamily = PlusJakartaSansFamily,
                color = GrayTextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                presets.forEach { (days, title, desc) ->
                    val isSelected = days == currentDays

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) ActivePrayerHighlight else SurfacePill,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, PureBlack) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onSelectDays(days) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = title,
                                    fontSize = 14.5.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = PureBlack
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    fontSize = 11.5.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    color = GrayTextSecondary
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = PureBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// =========================================================================
// MANUAL PAGE INPUT DIALOG
// =========================================================================

@Composable
private fun ManualPageInputDialog(
    currentPage: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var pageText by remember { mutableStateOf("$currentPage") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Update Halaman Terakhir",
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )
        },
        text = {
            Column {
                Text(
                    text = "Masukkan nomor halaman Mushaf Madinah (1 - 604):",
                    fontSize = 12.5.sp,
                    fontFamily = PlusJakartaSansFamily,
                    color = GrayTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = pageText,
                    onValueChange = {
                        pageText = it.filter { char -> char.isDigit() }
                        errorMessage = null
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        val num = pageText.toIntOrNull()
                        if (num != null && num in 1..604) {
                            onConfirm(num)
                        } else {
                            errorMessage = "Nomor halaman harus antara 1 sampai 604"
                        }
                    }),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureBlack,
                        unfocusedBorderColor = DividerColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 11.sp,
                        color = Color.Red,
                        fontFamily = PlusJakartaSansFamily
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val num = pageText.toIntOrNull()
                    if (num != null && num in 1..604) {
                        onConfirm(num)
                    } else {
                        errorMessage = "Nomor halaman harus antara 1 sampai 604"
                    }
                }
            ) {
                Text(
                    text = "Simpan",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    fontFamily = PlusJakartaSansFamily,
                    color = GrayTextSecondary
                )
            }
        },
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(22.dp)
    )
}

// =========================================================================
// KHATAM CELEBRATION & RESET DIALOGS
// =========================================================================

@Composable
private fun KhatamCelebrationDialog(
    currentKhatamCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.EmojiEvents,
                contentDescription = null,
                tint = AccentGold,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Alhamdulillah!",
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = PureBlack
            )
        },
        text = {
            Text(
                text = "Barakallahu fiik! Anda telah menyelesaikan pembacaan seluruh 30 Juz Al-Qur'an (Khatam ke-${currentKhatamCount + 1}). Catat pencapaian ini dan mulai siklus tilawah baru?",
                fontSize = 13.sp,
                fontFamily = PlusJakartaSansFamily,
                textAlign = TextAlign.Center,
                color = GrayTextSecondary,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Selesaikan & Mulai Baru",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = GreenSuccess
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Nanti",
                    fontFamily = PlusJakartaSansFamily,
                    color = GrayTextSecondary
                )
            }
        },
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(22.dp)
    )
}

@Composable
private fun ResetQuranDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reset Tanda Baca?",
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )
        },
        text = {
            Text(
                text = "Tanda baca akan dikembalikan ke Surah Al-Fatihah Halaman 1. Catatan riwayat khatam Anda sebelumnya tetap aman tersimpan.",
                fontSize = 13.sp,
                fontFamily = PlusJakartaSansFamily,
                color = GrayTextSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Ya, Reset",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    fontFamily = PlusJakartaSansFamily,
                    color = GrayTextSecondary
                )
            }
        },
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(22.dp)
    )
}
