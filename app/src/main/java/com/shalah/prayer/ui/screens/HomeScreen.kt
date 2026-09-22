package com.shalah.prayer.ui.screens

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shalah.prayer.alarm.PrayerAlarmScheduler
import com.shalah.prayer.data.backup.BackupManager
import com.shalah.prayer.data.calculator.PrayerTimeCalculator
import com.shalah.prayer.data.model.PrayerRecord
import com.shalah.prayer.data.model.PrayerType
import com.shalah.prayer.data.model.SunnahPrayerType
import com.shalah.prayer.ui.theme.AccentGold
import com.shalah.prayer.ui.theme.ActivePrayerHighlight
import com.shalah.prayer.ui.theme.AppThemeMode
import com.shalah.prayer.ui.theme.CanvasBackground
import com.shalah.prayer.ui.theme.DividerColor
import com.shalah.prayer.ui.theme.GrayTextMuted
import com.shalah.prayer.ui.theme.GrayTextPrimary
import com.shalah.prayer.ui.theme.GrayTextSecondary
import com.shalah.prayer.ui.theme.GreenSuccess
import com.shalah.prayer.ui.theme.OnPureBlack
import com.shalah.prayer.ui.theme.PlusJakartaSansFamily
import com.shalah.prayer.ui.theme.ProgressIndicator
import com.shalah.prayer.ui.theme.ProgressTrack
import com.shalah.prayer.ui.theme.PureBlack
import com.shalah.prayer.ui.theme.SoftBorder
import com.shalah.prayer.ui.theme.SurfaceCard
import com.shalah.prayer.ui.theme.SurfacePill
import com.shalah.prayer.ui.theme.SurfacePillBorder
import com.shalah.prayer.ui.theme.WarmCanvas
import com.shalah.prayer.ui.viewmodel.PrayerViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 5 Destinations for Shalah:
 * 1. Tracker (Celestial Arc Hero + Week Strip + Checklist Cards + Sunnah)
 * 2. Quran (Khatam Progress Ring + Last Read Bookmark + Quick Log + 114 Surah/30 Juz Index)
 * 3. Kiblat (Precision Compass with 3D Kaaba Badge)
 * 4. Kalender (Monthly Consistency Heatmap + Prayer Trends Matrix + 7-Day History)
 * 5. Pengaturan (Settings, 30m/10m/0m Notifications, Themes, Backup)
 */
enum class ShalahTab(val label: String) {
    TRACKER("Shalah"),
    QURAN("Quran"),
    KIBLAT("Kiblat"),
    KALENDER("Kalender"),
    PENGATURAN("Pengaturan")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PrayerViewModel,
    currentThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    onThemeChanged: (AppThemeMode) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val currentRecord by viewModel.currentRecord.collectAsStateWithLifecycle()
    val isToday by viewModel.isSelectedDateToday.collectAsStateWithLifecycle()
    val relativeLabel by viewModel.relativeDateLabel.collectAsStateWithLifecycle()
    val fullDateText by viewModel.selectedFullDateText.collectAsStateWithLifecycle()
    val hijriDateText by viewModel.selectedHijriDateText.collectAsStateWithLifecycle()
    val selectedDateStr by viewModel.selectedDateString.collectAsStateWithLifecycle()
    val recentRecords by viewModel.recentRecords.collectAsStateWithLifecycle()
    val allRecords by viewModel.allRecords.collectAsStateWithLifecycle()
    val fullDaysCompleted by viewModel.fullDaysCompletedCount.collectAsStateWithLifecycle()
    val currentStreak by viewModel.currentStreak.collectAsStateWithLifecycle()

    val prayerSchedule by viewModel.prayerSchedule.collectAsStateWithLifecycle()
    val activePrayer by viewModel.activePrayerType.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val isSunnahExpanded by viewModel.isSunnahExpanded.collectAsStateWithLifecycle()

    val calendarYearMonth by viewModel.calendarYearMonth.collectAsStateWithLifecycle()
    val monthName by viewModel.calendarMonthName.collectAsStateWithLifecycle()
    val monthRecordsMap by viewModel.monthRecordsMap.collectAsStateWithLifecycle()
    val totalRecordsCount by viewModel.totalRecordsCount.collectAsStateWithLifecycle()

    val quranProgress by viewModel.quranProgress.collectAsStateWithLifecycle()
    val todayQuranLog by viewModel.todayQuranLog.collectAsStateWithLifecycle()
    val recentQuranLogs by viewModel.recentQuranLogs.collectAsStateWithLifecycle()
    val quranStreak by viewModel.quranStreak.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(ShalahTab.TRACKER) }

    var showResetDialog by remember { mutableStateOf(false) }
    var showCitySheet by remember { mutableStateOf(false) }
    var editingPrayerTime by remember { mutableStateOf<PrayerTimeToEdit?>(null) }

    val userPrefs = remember { context.getSharedPreferences("shalah_user_prefs", Context.MODE_PRIVATE) }
    var showPrivacyPledge by remember {
        mutableStateOf(!userPrefs.getBoolean("has_seen_privacy_pledge", false))
    }

    val coroutineScope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val json = viewModel.getBackupJson()
                val success = BackupManager.writeToUri(context, uri, json)
                if (success) {
                    Toast.makeText(
                        context,
                        "Cadangan berhasil disimpan ($totalRecordsCount catatan)",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(context, "Gagal menulis file cadangan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val json = BackupManager.readFromUri(context, uri)
                if (json != null) {
                    val result = viewModel.restoreBackup(json)
                    result.onSuccess { count ->
                        Toast.makeText(context, "Alhamdulillah, $count catatan berhasil dipulihkan!", Toast.LENGTH_LONG)
                            .show()
                    }.onFailure { err ->
                        Toast.makeText(context, "Gagal: ${err.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Gagal membaca file yang dipilih", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
    ) {
        // Tab View Switching
        when (selectedTab) {
            ShalahTab.TRACKER -> {
                TrackerTabContent(
                    selectedDate = selectedDate,
                    isToday = isToday,
                    fullDateText = fullDateText,
                    hijriDateText = hijriDateText,
                    selectedCity = selectedCity,
                    prayerSchedule = prayerSchedule,
                    activePrayer = activePrayer,
                    currentRecord = currentRecord,
                    monthRecordsMap = monthRecordsMap,
                    currentStreak = currentStreak,
                    isSunnahExpanded = isSunnahExpanded,
                    onCityClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showCitySheet = true
                    },
                    onSelectDate = { date ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.selectDate(date)
                    },
                    onPreviousDay = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.previousDay()
                    },
                    onNextDay = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.nextDay()
                    },
                    onResetClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showResetDialog = true
                    },
                    onTogglePrayer = { prayer ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.togglePrayer(prayer)
                    },
                    onToggleSunnahSection = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.toggleSunnahExpanded()
                    },
                    onToggleSunnahPrayer = { sunnah ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.toggleSunnahPrayer(sunnah)
                    },
                    onEditPrayerTime = { target ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        editingPrayerTime = target
                    }
                )
            }

            ShalahTab.QURAN -> {
                QuranTabContent(
                    progress = quranProgress,
                    todayLog = todayQuranLog,
                    recentLogs = recentQuranLogs,
                    streak = quranStreak,
                    onUpdateBookmark = { surahNumber, ayahNumber, pageNumber ->
                        viewModel.updateBookmark(surahNumber, ayahNumber, pageNumber)
                    },
                    onAddPagesRead = { pages ->
                        viewModel.addPagesRead(pages)
                    },
                    onSetKhatamTarget = { days ->
                        viewModel.setKhatamTarget(days)
                    },
                    onCompleteKhatam = {
                        viewModel.completeKhatam()
                    },
                    onResetProgress = {
                        viewModel.resetQuranProgress()
                    }
                )
            }

            ShalahTab.KIBLAT -> {
                KiblatTabContent(
                    cityName = selectedCity.name,
                    latitude = selectedCity.latitude,
                    longitude = selectedCity.longitude,
                    onCityClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showCitySheet = true
                    }
                )
            }

            ShalahTab.KALENDER -> {
                KalenderTabContent(
                    yearMonth = calendarYearMonth,
                    monthName = monthName,
                    recordsMap = monthRecordsMap,
                    selectedDateStr = selectedDateStr,
                    allRecords = allRecords,
                    recentRecords = recentRecords,
                    fullDaysCompleted = fullDaysCompleted,
                    onSelectDate = { dateStr ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.selectDate(dateStr)
                        selectedTab = ShalahTab.TRACKER
                    },
                    onPreviousMonth = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.previousCalendarMonth()
                    },
                    onNextMonth = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.nextCalendarMonth()
                    }
                )
            }

            ShalahTab.PENGATURAN -> {
                PengaturanTabContent(
                    selectedCity = selectedCity,
                    totalRecords = totalRecordsCount,
                    currentThemeMode = currentThemeMode,
                    onThemeChanged = onThemeChanged,
                    onCityClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showCitySheet = true
                    },
                    onPrivacyPledgeClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showPrivacyPledge = true
                    },
                    onExportClick = {
                        val dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        exportLauncher.launch("shalah_backup_$dateStr.json")
                    },
                    onImportClick = {
                        importLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                    }
                )
            }
        }

        // Floating Minimalist Navigation Dock (Pillars Style)
        FloatingBottomDock(
            currentTab = selectedTab,
            onTabSelected = { tab ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                selectedTab = tab
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 22.dp, start = 24.dp, end = 24.dp)
        )
    }

    // Privacy & Offline First-Time Commitment Dialog
    if (showPrivacyPledge) {
        PrivacyPledgeDialog(
            onDismiss = {
                userPrefs.edit().putBoolean("has_seen_privacy_pledge", true).apply()
                showPrivacyPledge = false
            }
        )
    }

    // City Selection Modal Bottom Sheet
    if (showCitySheet) {
        CitySelectionSheet(
            currentCity = selectedCity,
            onSelect = { city ->
                viewModel.selectCity(city)
                showCitySheet = false
            },
            onDismiss = { showCitySheet = false }
        )
    }

    // Reset Day Confirmation Dialog
    if (showResetDialog) {
        ResetConfirmDialog(
            dateLabel = relativeLabel,
            onConfirm = {
                viewModel.resetCurrentDay()
                showResetDialog = false
            },
            onDismiss = { showResetDialog = false }
        )
    }

    // Edit Custom Prayer Time Dialog
    if (editingPrayerTime != null) {
        val editTarget = editingPrayerTime!!
        EditPrayerTimeDialog(
            target = editTarget,
            onSave = { hour, minute ->
                if (editTarget.prayerType != null) {
                    viewModel.updatePrayerTime(editTarget.prayerType, hour, minute)
                } else if (editTarget.sunnahType != null) {
                    viewModel.updateSunnahPrayerTime(editTarget.sunnahType, hour, minute)
                }
                Toast.makeText(context, "Waktu shalat ${editTarget.title} diperbarui", Toast.LENGTH_SHORT).show()
                editingPrayerTime = null
            },
            onDismiss = { editingPrayerTime = null }
        )
    }
}

// =========================================================================
// 1. TAB CONTENT: TRACKER (Celestial Arc Hero + Week Strip + Checklist + Sunnah)
// =========================================================================

@Composable
fun TrackerTabContent(
    selectedDate: LocalDate,
    isToday: Boolean,
    fullDateText: String,
    hijriDateText: String,
    selectedCity: PrayerTimeCalculator.CityCoordinate,
    prayerSchedule: PrayerTimeCalculator.PrayerSchedule,
    activePrayer: PrayerType?,
    currentRecord: PrayerRecord,
    monthRecordsMap: Map<String, PrayerRecord>,
    currentStreak: Int,
    isSunnahExpanded: Boolean,
    onCityClick: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onResetClick: () -> Unit,
    onTogglePrayer: (PrayerType) -> Unit,
    onToggleSunnahSection: () -> Unit,
    onToggleSunnahPrayer: (SunnahPrayerType) -> Unit,
    onEditPrayerTime: (PrayerTimeToEdit) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var now by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(10_000L)
            now = LocalTime.now()
        }
    }

    // Compute active or upcoming prayer station
    val (activeOrNextName, targetTime, isTomorrow) = remember(now, prayerSchedule) {
        when {
            now < prayerSchedule.fajr -> Triple("Subuh", prayerSchedule.fajr, false)
            now < prayerSchedule.sunrise -> Triple("Subuh", prayerSchedule.sunrise, false)
            now < prayerSchedule.dhuhr -> Triple("Dzuhur", prayerSchedule.dhuhr, false)
            now < prayerSchedule.asr -> Triple("Ashar", prayerSchedule.asr, false)
            now < prayerSchedule.maghrib -> Triple("Maghrib", prayerSchedule.maghrib, false)
            now < prayerSchedule.isha -> Triple("Isya", prayerSchedule.isha, false)
            else -> Triple("Subuh", prayerSchedule.fajr, true)
        }
    }

    val displayPrayerName = if (isToday) {
        when (activePrayer) {
            PrayerType.FAJR -> "Subuh"
            PrayerType.DHUHR -> "Dzuhur"
            PrayerType.ASR -> "Ashar"
            PrayerType.MAGHRIB -> "Maghrib"
            PrayerType.ISHA -> "Isya"
            null -> activeOrNextName
        }
    } else {
        "Pencatat Shalat"
    }

    val remainingMinutes = remember(now, targetTime, isTomorrow) {
        if (!isTomorrow) {
            Duration.between(now, targetTime).toMinutes().coerceAtLeast(0)
        } else {
            val toMidnight = Duration.between(now, LocalTime.MAX).toMinutes()
            val fromMidnight = Duration.between(LocalTime.MIN, targetTime).toMinutes()
            toMidnight + fromMidnight + 1
        }
    }

    val countdownPillText = remember(remainingMinutes, isToday, activeOrNextName) {
        if (!isToday) return@remember "Jadwal Astronomis Kemenag RI"
        val hours = remainingMinutes / 60
        val mins = remainingMinutes % 60
        when {
            hours > 0 && mins > 0 -> "$hours jam $mins mnt menuju $activeOrNextName"
            hours > 0 -> "$hours jam menuju $activeOrNextName"
            mins > 0 -> "$mins mnt menuju $activeOrNextName"
            else -> "Waktu $activeOrNextName sedang masuk"
        }
    }

    val cityNameShort = selectedCity.name.split("/")[0].trim()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 120.dp)
    ) {
        // Atmospheric Sky Header Section (Title + Countdown Badge)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayPrayerName,
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
                                text = "$currentStreak Hari",
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                color = PureBlack
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = SurfaceCard,
                    modifier = Modifier.clip(RoundedCornerShape(50))
                ) {
                    Text(
                        text = countdownPillText,
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = GrayTextSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Celestial Solar Arc Curve Canvas (Continuous Smooth Sun Journey)
        item {
            CelestialSolarArcView(
                schedule = prayerSchedule,
                now = now,
                isToday = isToday
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Location Pill button & Quick Actions
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
                        .clickable { onCityClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isToday) "HARI INI" else "PILIH TANGGAL",
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = PureBlack,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = " | ",
                            fontSize = 11.sp,
                            color = GrayTextMuted
                        )
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PureBlack,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = cityNameShort,
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = PureBlack
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = SurfaceCard,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable { onResetClick() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = GrayTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Date Navigation Row with Chevrons & Hijri Moon Subtitle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = SurfaceCard,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable { onPreviousDay() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Hari Sebelumnya",
                            tint = PureBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = fullDateText,
                        fontSize = 14.5.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = PureBlack
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = hijriDateText,
                            fontSize = 11.5.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Medium,
                            color = AccentGold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.Nightlight,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = if (!selectedDate.isEqual(LocalDate.now())) SurfaceCard else CanvasBackground,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !selectedDate.isEqual(LocalDate.now())) { onNextDay() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Hari Berikutnya",
                            tint = if (!selectedDate.isEqual(LocalDate.now())) PureBlack else GrayTextMuted.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 7-Day Glanceable Horizontal Week Strip
        item {
            WeekDayStrip(
                selectedDate = selectedDate,
                recordsMap = monthRecordsMap,
                onSelectDate = onSelectDate
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section Title: Shalat Fardhu
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Shalat Fardhu",
                    fontSize = 16.5.sp,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack
                )
                Text(
                    text = "${currentRecord.completedCount} / 5",
                    fontSize = 13.sp,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (currentRecord.completedCount == 5) GreenSuccess else GrayTextSecondary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 5 Fardhu Prayer Checklist Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val prayers = listOf(
                    Triple(PrayerType.FAJR, "Subuh", prayerSchedule.fajr),
                    Triple(PrayerType.DHUHR, "Dzuhur", prayerSchedule.dhuhr),
                    Triple(PrayerType.ASR, "Ashar", prayerSchedule.asr),
                    Triple(PrayerType.MAGHRIB, "Maghrib", prayerSchedule.maghrib),
                    Triple(PrayerType.ISHA, "Isya", prayerSchedule.isha)
                )

                prayers.forEach { (prayerType, name, scheduledLocalTime) ->
                    val isDone = currentRecord.isCompleted(prayerType)
                    val completedTime = currentRecord.getTime(prayerType)
                    val scheduledTimeFormatted = DateTimeFormatter.ofPattern("HH:mm").format(scheduledLocalTime)

                    PillarsChecklistCard(
                        title = name,
                        time = scheduledTimeFormatted,
                        completedTimestamp = completedTime,
                        isDone = isDone,
                        onToggle = { onTogglePrayer(prayerType) },
                        onEditTime = {
                            onEditPrayerTime(
                                PrayerTimeToEdit(
                                    prayerType = prayerType,
                                    title = name,
                                    scheduledTime = scheduledLocalTime,
                                    initialTimestamp = completedTime
                                )
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Expandable Sunnah Prayer Section
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SurfaceCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onToggleSunnahSection() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Shalat Sunnah & Rawatib",
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = PureBlack
                        )
                        if (currentRecord.completedSunnahCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = GreenSuccess.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${currentRecord.completedSunnahCount} Dikerjakan",
                                    fontSize = 10.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenSuccess,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = if (isSunnahExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = PureBlack
                    )
                }
            }

            AnimatedVisibility(
                visible = isSunnahExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sunnahs = listOf(
                        Pair(SunnahPrayerType.RAWATIB, "Shalat Sunnah Rawatib (Qobliyah & Ba'diyah)"),
                        Pair(SunnahPrayerType.DHUHA, "Shalat Dhuha"),
                        Pair(SunnahPrayerType.TAHAJJUD, "Shalat Tahajjud & Qiyamul Lail"),
                        Pair(SunnahPrayerType.WITIR, "Shalat Witir")
                    )

                    sunnahs.forEach { (type, name) ->
                        val isDone = currentRecord.isSunnahCompleted(type)
                        val completedTime = currentRecord.getSunnahTime(type)

                        PillarsChecklistCard(
                            title = name,
                            time = "Sunnah",
                            completedTimestamp = completedTime,
                            isDone = isDone,
                            onToggle = { onToggleSunnahPrayer(type) },
                            onEditTime = {
                                onEditPrayerTime(
                                    PrayerTimeToEdit(
                                        sunnahType = type,
                                        title = name,
                                        initialTimestamp = completedTime
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// CELESTIAL SOLAR ARC VIEW (Continuous Smooth Sun Journey)
// =========================================================================

@Composable
fun CelestialSolarArcView(
    schedule: PrayerTimeCalculator.PrayerSchedule,
    now: LocalTime,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = SurfaceCard,
        modifier = modifier
            .fillMaxWidth()
            .height(175.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val startX = 24.dp.toPx()
                val endX = w - 24.dp.toPx()
                val baselineY = h - 34.dp.toPx()
                val peakY = 36.dp.toPx()

                // 1. Subtle top-right Sun Aura Halo
                val auraCenter = Offset(w * 0.92f, 16.dp.toPx())
                drawCircle(
                    color = Color(0xFFFBBF24).copy(alpha = 0.05f),
                    radius = 140.dp.toPx(),
                    center = auraCenter
                )
                drawCircle(
                    color = Color(0xFFFBBF24).copy(alpha = 0.09f),
                    radius = 90.dp.toPx(),
                    center = auraCenter
                )
                drawCircle(
                    color = Color(0xFFFBBF24).copy(alpha = 0.14f),
                    radius = 50.dp.toPx(),
                    center = auraCenter
                )

                // 2. Minimalist Pine Tree Silhouettes on Left Baseline
                val tree1 = Path().apply {
                    val bx = 16.dp.toPx()
                    val by = baselineY
                    moveTo(bx, by)
                    lineTo(bx + 4.dp.toPx(), by - 26.dp.toPx())
                    lineTo(bx + 8.dp.toPx(), by)
                    close()
                }
                val tree2 = Path().apply {
                    val bx = 22.dp.toPx()
                    val by = baselineY
                    moveTo(bx, by)
                    lineTo(bx + 5.dp.toPx(), by - 18.dp.toPx())
                    lineTo(bx + 10.dp.toPx(), by)
                    close()
                }
                drawPath(
                    path = tree1,
                    color = Color(0xFF143E33).copy(alpha = 0.20f),
                    style = Fill
                )
                drawPath(
                    path = tree2,
                    color = Color(0xFF143E33).copy(alpha = 0.28f),
                    style = Fill
                )

                // 3. Mathematical Celestial Solar Dome Curve Function
                fun getArcPoint(t: Float): Offset {
                    val clampedT = t.coerceIn(0f, 1f)
                    val x = startX + (endX - startX) * clampedT
                    val height = baselineY - peakY
                    val y = baselineY - height * kotlin.math.sin(clampedT * Math.PI.toFloat())
                    return Offset(x, y)
                }

                // 4. Background Full Continuous Solar Arc Path
                val fullPath = Path()
                val totalSteps = 120
                for (i in 0..totalSteps) {
                    val t = i.toFloat() / totalSteps
                    val pt = getArcPoint(t)
                    if (i == 0) fullPath.moveTo(pt.x, pt.y) else fullPath.lineTo(pt.x, pt.y)
                }
                drawPath(
                    path = fullPath,
                    color = Color(0xFF143E33).copy(alpha = 0.16f),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Astronomical timespan: Fajr-45m to Isha+45m
                val startDayTime = schedule.fajr.minusMinutes(45)
                val endDayTime = schedule.isha.plusMinutes(45)
                val totalDayMinutes = Duration.between(startDayTime, endDayTime).toMinutes().toFloat().coerceAtLeast(1f)

                val currentProgress = if (isToday) {
                    when {
                        now < startDayTime -> 0f
                        now > endDayTime -> 1f
                        else -> {
                            val elapsed = Duration.between(startDayTime, now).toMinutes().toFloat()
                            (elapsed / totalDayMinutes).coerceIn(0f, 1f)
                        }
                    }
                } else 0.5f

                // 5. Passed Portion of Curve (drawn with exact same mathematical points)
                if (currentProgress > 0.005f) {
                    val passedPath = Path()
                    val passedSteps = (totalSteps * currentProgress).toInt().coerceAtLeast(1)
                    for (i in 0..passedSteps) {
                        val t = (i.toFloat() / totalSteps).coerceAtMost(currentProgress)
                        val pt = getArcPoint(t)
                        if (i == 0) passedPath.moveTo(pt.x, pt.y) else passedPath.lineTo(pt.x, pt.y)
                    }
                    val exactEndPt = getArcPoint(currentProgress)
                    passedPath.lineTo(exactEndPt.x, exactEndPt.y)

                    drawPath(
                        path = passedPath,
                        color = Color(0xFF143E33),
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // 6. Station Milestone Nodes (Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha)
                val stations = listOf(
                    schedule.fajr,
                    schedule.sunrise,
                    schedule.dhuhr,
                    schedule.asr,
                    schedule.maghrib,
                    schedule.isha
                )

                stations.forEach { stationTime ->
                    val elapsed = Duration.between(startDayTime, stationTime).toMinutes().toFloat()
                    val stationT = (elapsed / totalDayMinutes).coerceIn(0.03f, 0.97f)
                    val pt = getArcPoint(stationT)

                    val isStationPassed = isToday && (now >= stationTime)

                    if (isStationPassed) {
                        // Solid dark emerald dot
                        drawCircle(
                            color = Color(0xFF143E33),
                            radius = 4.5.dp.toPx(),
                            center = pt
                        )
                    } else {
                        // Future station: Clean white fill with emerald outline
                        drawCircle(
                            color = Color.White,
                            radius = 4.5.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = Color(0xFF143E33).copy(alpha = 0.6f),
                            radius = 4.5.dp.toPx(),
                            center = pt,
                            style = Stroke(width = 1.8.dp.toPx())
                        )
                    }
                }

                // 7. Active Glowing Celestial Sun Marker
                if (isToday) {
                    val sunPt = getArcPoint(currentProgress)

                    // Outer soft glow halo
                    drawCircle(
                        color = Color(0xFFFBBF24).copy(alpha = 0.22f),
                        radius = 13.dp.toPx(),
                        center = sunPt
                    )
                    // Middle amber core ring
                    drawCircle(
                        color = Color(0xFFFBBF24),
                        radius = 6.dp.toPx(),
                        center = sunPt,
                        style = Stroke(width = 2.2.dp.toPx())
                    )
                    // Core white center spark
                    drawCircle(
                        color = Color(0xFFFFFFFF),
                        radius = 2.8.dp.toPx(),
                        center = sunPt
                    )
                }
            }
        }
    }
}

// =========================================================================
// 2. TAB CONTENT: KIBLAT (Deep Midnight Precision Compass + 3D Kaaba)
// =========================================================================

@Composable
fun KiblatTabContent(
    cityName: String,
    latitude: Double,
    longitude: Double,
    onCityClick: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val qiblaBearing = remember(latitude, longitude) {
        PrayerTimeCalculator.calculateQiblaBearing(latitude, longitude).toFloat()
    }

    var azimuth by remember { mutableFloatStateOf(0f) }
    var isSensorAvailable by remember { mutableStateOf(true) }
    var showInfoDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sensorManager?.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
            ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)

        val accelSensor =
            if (rotationSensor == null) sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) else null
        val magnetSensor =
            if (rotationSensor == null) sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) else null

        if (rotationSensor == null && (accelSensor == null || magnetSensor == null)) {
            isSensorAvailable = false
        } else {
            isSensorAvailable = true
        }

        var gravity: FloatArray? = null
        var geomagnetic: FloatArray? = null

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                when (event.sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR,
                    Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                        val rotationMatrix = FloatArray(9)
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(rotationMatrix, orientation)
                        var deg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                        if (deg < 0) deg += 360f
                        azimuth = deg
                    }

                    Sensor.TYPE_ORIENTATION -> {
                        var deg = event.values[0]
                        if (deg < 0) deg += 360f
                        azimuth = deg
                    }

                    Sensor.TYPE_ACCELEROMETER -> {
                        gravity = event.values.clone()
                        computeFromAccelMag()
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        geomagnetic = event.values.clone()
                        computeFromAccelMag()
                    }
                }
            }

            private fun computeFromAccelMag() {
                val g = gravity ?: return
                val m = geomagnetic ?: return
                val r = FloatArray(9)
                val i = FloatArray(9)
                if (SensorManager.getRotationMatrix(r, i, g, m)) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(r, orientation)
                    var deg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    if (deg < 0) deg += 360f
                    azimuth = deg
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationSensor != null) {
            sensorManager?.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        } else if (accelSensor != null && magnetSensor != null) {
            sensorManager?.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_UI)
            sensorManager?.registerListener(listener, magnetSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    // Relative angle calculations
    val relativeAngle = (qiblaBearing - azimuth + 360f) % 360f
    val diffToQibla = if (relativeAngle > 180f) 360f - relativeAngle else relativeAngle
    val isAligned = isSensorAvailable && diffToQibla <= 4f

    var lastAlignedState by remember { mutableStateOf(false) }
    LaunchedEffect(isAligned) {
        if (isAligned && !lastAlignedState) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        lastAlignedState = isAligned
    }

    val animatedRotation by animateFloatAsState(
        targetValue = if (isSensorAvailable) -azimuth else 0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "compass_rot"
    )

    // Direction instruction: "Turn to your right" / "Turn to your left" / "Anda menghadap Kiblat"
    val turnInstruction = remember(relativeAngle, isAligned) {
        when {
            isAligned -> "Anda menghadap Kiblat"
            relativeAngle in 1f..180f -> "Putar ke arah kanan"
            else -> "Putar ke arah kiri"
        }
    }

    val cityNameShort = cityName.split("/")[0].trim()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B100E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: LOCATION Pill & Info Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LOKASI",
                        fontSize = 10.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF62756B),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFF131916),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { onCityClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cityNameShort,
                                fontSize = 14.sp,
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF0FDF4)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFF131916),
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { showInfoDialog = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "i",
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A89E)
                        )
                    }
                }
            }

            // Precision Compass Instrument Face
            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isAligned) {
                    Box(
                        modifier = Modifier
                            .size(290.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34D399).copy(alpha = 0.18f))
                    )
                }

                Canvas(
                    modifier = Modifier
                        .size(260.dp)
                        .rotate(animatedRotation)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2

                    drawCircle(
                        color = Color(0xFFF6F4ED),
                        radius = radius,
                        center = center
                    )

                    for (i in 0 until 360 step 5) {
                        val angleRad = Math.toRadians(i.toDouble())
                        val isMajor = i % 30 == 0
                        val tickLen = if (isMajor) 12.dp.toPx() else 6.dp.toPx()
                        val outerRadius = radius - 8.dp.toPx()
                        val innerRadius = outerRadius - tickLen

                        val startP = Offset(
                            (center.x + innerRadius * Math.sin(angleRad)).toFloat(),
                            (center.y - innerRadius * Math.cos(angleRad)).toFloat()
                        )
                        val endP = Offset(
                            (center.x + outerRadius * Math.sin(angleRad)).toFloat(),
                            (center.y - outerRadius * Math.cos(angleRad)).toFloat()
                        )

                        drawLine(
                            color = if (isMajor) Color(0xFF191C1A) else Color(0xFF8A9791).copy(alpha = 0.6f),
                            start = startP,
                            end = endP,
                            strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
                        )
                    }

                    // Draw 3D Kaaba Badge at exact Qibla Bearing angle on dial
                    val qiblaRad = Math.toRadians(qiblaBearing.toDouble())
                    val kaabaRadius = radius - 36.dp.toPx()
                    val kaabaCenter = Offset(
                        (center.x + kaabaRadius * Math.sin(qiblaRad)).toFloat(),
                        (center.y - kaabaRadius * Math.cos(qiblaRad)).toFloat()
                    )

                    drawRect(
                        color = Color(0xFF191C1A),
                        topLeft = Offset(kaabaCenter.x - 12.dp.toPx(), kaabaCenter.y - 12.dp.toPx()),
                        size = Size(24.dp.toPx(), 24.dp.toPx())
                    )
                    drawRect(
                        color = Color(0xFFFBBF24),
                        topLeft = Offset(kaabaCenter.x - 12.dp.toPx(), kaabaCenter.y - 4.dp.toPx()),
                        size = Size(24.dp.toPx(), 4.dp.toPx())
                    )
                }

                // Center Needle Pointer
                Canvas(modifier = Modifier.size(260.dp)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val needlePath = Path().apply {
                        moveTo(center.x, center.y - 58.dp.toPx())
                        lineTo(center.x + 10.dp.toPx(), center.y)
                        lineTo(center.x - 10.dp.toPx(), center.y)
                        close()
                    }
                    drawPath(
                        path = needlePath,
                        color = if (isAligned) Color(0xFF10B981) else Color(0xFFF97316),
                        style = Fill
                    )
                }
            }

            // Direction Guidance Typographic Prompt
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = turnInstruction,
                    fontSize = 22.sp,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isAligned) Color(0xFF34D399) else Color(0xFFF0FDF4),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${azimuth.toInt()}° • Arah Kiblat: ${qiblaBearing.toInt()}°",
                    fontSize = 13.sp,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF94A89E)
                )
            }
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    text = "Kalibrasi Kompas",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack
                )
            },
            text = {
                Text(
                    text = "Untuk akurasi maksimal, gerakkan perangkat Anda membentuk angka 8 di udara dan jauhkan dari benda berbahan logam atau magnet.",
                    fontFamily = PlusJakartaSansFamily,
                    fontSize = 13.sp,
                    color = GrayTextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Mengerti", color = PureBlack, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// =========================================================================
// 3. TAB CONTENT: KALENDER (Monthly Heatmap Grid, Prayer Trends & 7-Day History)
// =========================================================================

@Composable
fun KalenderTabContent(
    yearMonth: YearMonth,
    monthName: String,
    recordsMap: Map<String, PrayerRecord>,
    selectedDateStr: String,
    allRecords: List<PrayerRecord>,
    recentRecords: List<PrayerRecord>,
    fullDaysCompleted: Int,
    onSelectDate: (String) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value // 1=Mon, 7=Sun
    val leadOffset = firstDayOfWeek - 1

    val completedDaysCount = recordsMap.values.count { it.isAllCompleted }
    val totalPrayersInMonth = recordsMap.values.sumOf { it.completedCount }
    val isCurrentMonth = yearMonth == YearMonth.now()
    val daysElapsed = if (isCurrentMonth) LocalDate.now().dayOfMonth else daysInMonth
    val monthPercentage = if (daysElapsed > 0) {
        ((totalPrayersInMonth / (daysElapsed * 5.0f)) * 100).toInt().coerceIn(0, 100)
    } else 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 22.dp, bottom = 120.dp)
    ) {
        // Title Header
        item {
            Text(
                text = "Kalender & Riwayat",
                fontSize = 28.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.ExtraBold,
                color = PureBlack,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Pantau konsistensi ibadah shalat Anda setiap hari",
                fontSize = 12.sp,
                fontFamily = PlusJakartaSansFamily,
                color = GrayTextSecondary
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Month Selector Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthName,
                    fontSize = 18.sp,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = SurfaceCard,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable { onPreviousMonth() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = null,
                                tint = PureBlack,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    val canGoNext = !yearMonth.isAfter(YearMonth.now().minusMonths(1))
                    Surface(
                        shape = CircleShape,
                        color = if (canGoNext) SurfaceCard else CanvasBackground,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable(enabled = canGoNext) { onNextMonth() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = if (canGoNext) PureBlack else GrayTextMuted.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 3-Metric Summary Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalendarStatCard(modifier = Modifier.weight(1f), value = "$completedDaysCount", label = "Hari Lengkap")
                CalendarStatCard(modifier = Modifier.weight(1f), value = "$totalPrayersInMonth", label = "Total Shalat")
                CalendarStatCard(modifier = Modifier.weight(1f), value = "$monthPercentage%", label = "Konsistensi")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Calendar Heatmap Table Card
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SurfaceCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Day of week headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("SEN", "SEL", "RAB", "KAM", "JUM", "SAB", "AHD").forEach { d ->
                            Text(
                                text = d,
                                fontSize = 11.sp,
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.Bold,
                                color = GrayTextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(38.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = DividerColor, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    val totalCells = leadOffset + daysInMonth
                    val rows = (totalCells + 6) / 7

                    for (r in 0 until rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (c in 0 until 7) {
                                val cellIndex = r * 7 + c
                                val dayNum = cellIndex - leadOffset + 1
                                if (dayNum in 1..daysInMonth) {
                                    val date = yearMonth.atDay(dayNum)
                                    val dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    val record = recordsMap[dateStr]
                                    val isFuture = date.isAfter(LocalDate.now())
                                    val isSelected = (dateStr == selectedDateStr)
                                    val isCurrentDay = date.isEqual(LocalDate.now())

                                    CalendarGridDayCell(
                                        dayNumber = dayNum,
                                        record = record,
                                        isFuture = isFuture,
                                        isSelected = isSelected,
                                        isToday = isCurrentDay,
                                        onClick = {
                                            if (!isFuture) onSelectDate(dateStr)
                                        }
                                    )
                                } else {
                                    Box(modifier = Modifier.size(38.dp))
                                }
                            }
                        }
                        if (r < rows - 1) {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Legend
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = PureBlack, text = "5/5 Lengkap")
                Spacer(modifier = Modifier.width(14.dp))
                LegendItem(color = Color(0xFF143E33).copy(alpha = 0.2f), text = "1-4 Shalat")
                Spacer(modifier = Modifier.width(14.dp))
                LegendItem(color = CanvasBackground, text = "Kosong", border = true)
            }
            Spacer(modifier = Modifier.height(22.dp))
        }

        // PRAYER TRENDS (GitHub-Style Contribution Heatmap Matrix)
        item {
            Text(
                text = "TREN SHALAT (PRAYER TRENDS)",
                fontSize = 12.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = GrayTextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            PrayerTrendsMatrixCard(
                allRecords = allRecords
            )
            Spacer(modifier = Modifier.height(22.dp))
        }

        // 7-Day History Card Strip
        item {
            Text(
                text = "Riwayat 7 Hari Terakhir",
                fontSize = 15.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (recentRecords.isEmpty()) {
                Text(
                    text = "Belum ada riwayat tersimpan.",
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSansFamily,
                    color = GrayTextMuted
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recentRecords) { record ->
                        DayHistoryChip(
                            record = record,
                            isSelected = record.date == selectedDateStr,
                            onClick = { onSelectDate(record.date) }
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 4. TAB CONTENT: PENGATURAN (Settings & 30m/10m/0m Notification Preferences)
// =========================================================================

@Composable
fun PengaturanTabContent(
    selectedCity: PrayerTimeCalculator.CityCoordinate,
    totalRecords: Int,
    currentThemeMode: AppThemeMode,
    onThemeChanged: (AppThemeMode) -> Unit,
    onCityClick: () -> Unit,
    onPrivacyPledgeClick: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAlarmDialog by remember { mutableStateOf(false) }
    var showCalculationDialog by remember { mutableStateOf(false) }
    var showWidgetGuideDialog by remember { mutableStateOf(false) }

    var isReminderEnabled by remember {
        mutableStateOf(PrayerAlarmScheduler.isReminderEnabled(context))
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 22.dp, bottom = 120.dp)
    ) {
        // Header Row: Title "Pengaturan" + Share Action Pill
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pengaturan",
                    fontSize = 30.sp,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = PureBlack,
                    letterSpacing = (-0.5).sp
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = SurfaceCard,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Aplikasi Shalah")
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Gunakan Shalah untuk jadwal shalat & pencatat ibadah 100% offline, tanpa iklan, dan menjaga privasi."
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Shalah"))
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = PureBlack,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Bagikan Kebaikan",
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = PureBlack
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Highlight Banner Card
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFF143E33),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF34D399).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "100% OFFLINE & PRIVAT",
                                fontSize = 9.5.sp,
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Ketenangan Tanpa Jejak Digital",
                        fontSize = 17.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Shalah tidak memerlukan koneksi internet, tidak memiliki iklan, dan tidak melacak data Anda sedikitpun.",
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSansFamily,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Full-Width Rounded Action Pill Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PillarsSettingsActionItem(
                    icon = Icons.Outlined.Schedule,
                    title = "Waktu & Jadwal Shalat",
                    subtitle = selectedCity.name.split("/")[0].trim(),
                    onClick = onCityClick
                )

                PillarsSettingsActionItem(
                    icon = if (isReminderEnabled) Icons.Outlined.NotificationsActive else Icons.Outlined.Notifications,
                    title = "Notifikasi & Pengingat",
                    subtitle = if (isReminderEnabled) "Aktif (30m, 10m & Tepat Waktu)" else "Nonaktif",
                    onClick = { showAlarmDialog = true }
                )

                PillarsSettingsActionItem(
                    icon = Icons.Outlined.Palette,
                    title = "Tema Tampilan",
                    subtitle = currentThemeMode.label,
                    onClick = { showThemeDialog = true }
                )

                PillarsSettingsActionItem(
                    icon = Icons.Outlined.Straighten,
                    title = "Metode Perhitungan",
                    subtitle = "Kemenag RI (Subuh 20°, Isya 18°)",
                    onClick = { showCalculationDialog = true }
                )

                PillarsSettingsActionItem(
                    icon = Icons.Outlined.Widgets,
                    title = "Widget Layar Utama",
                    subtitle = "Panduan & Sinkronisasi",
                    onClick = { showWidgetGuideDialog = true }
                )

                PillarsSettingsActionItem(
                    icon = Icons.Outlined.FileDownload,
                    title = "Ekspor Cadangan JSON",
                    subtitle = "$totalRecords catatan riwayat",
                    onClick = onExportClick
                )

                PillarsSettingsActionItem(
                    icon = Icons.Outlined.FileUpload,
                    title = "Pulihkan Cadangan JSON",
                    subtitle = "Impor file .json",
                    onClick = onImportClick
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Privacy Centre Card
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onPrivacyPledgeClick() }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = PureBlack,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pusat Privasi (Privacy Centre)",
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = PureBlack
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Privasi Anda adalah prioritas mutlak kami. Pelajari komitmen kedaulatan data dan keterbukaan tanpa internet.",
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSansFamily,
                        color = GrayTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // App Footer
        item {
            Text(
                text = "Shalah v1.0.0 • Artisanal Islamic Companion",
                fontSize = 11.sp,
                fontFamily = PlusJakartaSansFamily,
                color = GrayTextMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }

    // Alarm & Notification Modal Dialog (30m, 10m & Exact time)
    if (showAlarmDialog) {
        AlertDialog(
            onDismissRequest = { showAlarmDialog = false },
            title = {
                Text(
                    text = "Pengingat Waktu Shalat",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Aktifkan Pengingat",
                                fontSize = 14.5.sp,
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.Bold,
                                color = PureBlack
                            )
                            Text(
                                text = "Jadwal alarm exact Android offline",
                                fontSize = 11.5.sp,
                                fontFamily = PlusJakartaSansFamily,
                                color = GrayTextSecondary
                            )
                        }

                        Switch(
                            checked = isReminderEnabled,
                            onCheckedChange = { checked ->
                                isReminderEnabled = checked
                                PrayerAlarmScheduler.setReminderEnabled(context, checked, selectedCity)
                                Toast.makeText(
                                    context,
                                    if (checked) "Pengingat shalat 30m, 10m & tepat waktu diaktifkan" else "Pengingat dinonaktifkan",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GreenSuccess,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = GrayTextMuted
                            )
                        )
                    }

                    HorizontalDivider(color = DividerColor, thickness = 0.6.dp)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Jadwal Notifikasi Otomatis:",
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = PureBlack
                        )
                        ReminderScheduleBullet(
                            timeLabel = "30 Menit Sebelum",
                            description = "Pengingat awal untuk persiapan & mengambil wudhu"
                        )
                        ReminderScheduleBullet(
                            timeLabel = "10 Menit Sebelum",
                            description = "Pemberitahuan bersiap mendirikan shalat"
                        )
                        ReminderScheduleBullet(
                            timeLabel = "Tepat Waktu Masuk",
                            description = "Pemberitahuan waktu shalat fardhu telah tiba"
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAlarmDialog = false }) {
                    Text("Selesai", color = PureBlack, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Theme Picker Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text = "Pilih Tema Tampilan",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppThemeMode.entries.forEach { mode ->
                        val isSelected = (mode == currentThemeMode)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) PureBlack else CanvasBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onThemeChanged(mode)
                                    showThemeDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = mode.label,
                                        fontSize = 14.sp,
                                        fontFamily = PlusJakartaSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) OnPureBlack else PureBlack
                                    )
                                    Text(
                                        text = mode.description,
                                        fontSize = 11.sp,
                                        fontFamily = PlusJakartaSansFamily,
                                        color = if (isSelected) OnPureBlack.copy(alpha = 0.7f) else GrayTextSecondary
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = OnPureBlack,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Calculation Method Dialog
    if (showCalculationDialog) {
        AlertDialog(
            onDismissRequest = { showCalculationDialog = false },
            title = {
                Text(
                    text = "Metode Perhitungan",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack
                )
            },
            text = {
                Text(
                    text = "Aplikasi Shalah menggunakan standar Kementerian Agama Republik Indonesia (Kemenag RI):\n\n• Sudut Fajar (Subuh): 20.0°\n• Sudut Isya: 18.0°\n• Imsak Buffer: 10 menit sebelum Subuh\n• Pengaman Waktu: +2 menit (Standar Kemenag)",
                    fontFamily = PlusJakartaSansFamily,
                    fontSize = 13.sp,
                    color = GrayTextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showCalculationDialog = false }) {
                    Text("Tutup", color = PureBlack, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Widget Guide Dialog
    if (showWidgetGuideDialog) {
        AlertDialog(
            onDismissRequest = { showWidgetGuideDialog = false },
            title = {
                Text(
                    text = "Widget Layar Utama",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack
                )
            },
            text = {
                Text(
                    text = "Shalah menyediakan 2 jenis widget elegan:\n\n1. Widget 4x2: Menampilkan jadwal shalat lengkap dan progres hari ini.\n2. Widget 2x2: Menampilkan shalat berikutnya dan countdown ringkas.\n\nWidget otomatis tersinkronisasi saat Anda mencatat shalat.",
                    fontFamily = PlusJakartaSansFamily,
                    fontSize = 13.sp,
                    color = GrayTextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showWidgetGuideDialog = false }) {
                    Text("Tutup", color = PureBlack, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun ReminderScheduleBullet(
    timeLabel: String,
    description: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(GreenSuccess)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = timeLabel,
                fontSize = 12.5.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )
            Text(
                text = description,
                fontSize = 11.sp,
                fontFamily = PlusJakartaSansFamily,
                color = GrayTextSecondary
            )
        }
    }
}

@Composable
fun PillarsSettingsActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PureBlack,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 14.5.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = PureBlack
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        fontFamily = PlusJakartaSansFamily,
                        color = GrayTextSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GrayTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// =========================================================================
// FLOATING MINIMALIST BOTTOM NAVIGATION DOCK (4 Pillars-Inspired Tabs)
// =========================================================================

@Composable
fun FloatingBottomDock(
    currentTab: ShalahTab,
    onTabSelected: (ShalahTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = SurfaceCard,
        shadowElevation = 16.dp,
        modifier = modifier.height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShalahTab.entries.forEach { tab ->
                val isSelected = (tab == currentTab)
                val icon = when (tab) {
                    ShalahTab.TRACKER -> Icons.Filled.Check
                    ShalahTab.QURAN -> Icons.Outlined.AutoStories
                    ShalahTab.KIBLAT -> Icons.Outlined.Explore
                    ShalahTab.KALENDER -> Icons.Filled.CalendarMonth
                    ShalahTab.PENGATURAN -> Icons.Filled.Settings
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isSelected) PureBlack else Color.Transparent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onTabSelected(tab) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = if (isSelected) 12.dp else 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = if (isSelected) OnPureBlack else GrayTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = tab.label,
                                fontSize = 11.5.sp,
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.Bold,
                                color = OnPureBlack
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 7-DAY GLANCEABLE HORIZONTAL WEEK STRIP
// =========================================================================

@Composable
fun WeekDayStrip(
    selectedDate: LocalDate,
    recordsMap: Map<String, PrayerRecord>,
    onSelectDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val localeId = Locale("id", "ID")
    val mondayOfWeek = remember(selectedDate) {
        selectedDate.with(DayOfWeek.MONDAY)
    }
    val weekDays = remember(mondayOfWeek) {
        (0..6).map { mondayOfWeek.plusDays(it.toLong()) }
    }
    val today = remember { LocalDate.now() }

    val monthYearTitle = remember(mondayOfWeek) {
        val sundayOfWeek = mondayOfWeek.plusDays(6)
        if (mondayOfWeek.month == sundayOfWeek.month) {
            DateTimeFormatter.ofPattern("MMMM yyyy", localeId).format(mondayOfWeek)
        } else {
            val startMonth = DateTimeFormatter.ofPattern("MMM", localeId).format(mondayOfWeek)
            val endMonth = DateTimeFormatter.ofPattern("MMM yyyy", localeId).format(sundayOfWeek)
            "$startMonth - $endMonth"
        }
    }

    val canGoNext = !mondayOfWeek.plusWeeks(1).isAfter(today)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthYearTitle,
                fontSize = 13.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = CircleShape,
                    color = SurfaceCard,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { onSelectDate(selectedDate.minusWeeks(1)) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Minggu Sebelumnya",
                            tint = PureBlack,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = if (canGoNext) SurfaceCard else CanvasBackground,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(enabled = canGoNext) {
                            val target = selectedDate.plusWeeks(1)
                            onSelectDate(if (target.isAfter(today)) today else target)
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Minggu Berikutnya",
                            tint = if (canGoNext) PureBlack else GrayTextMuted.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 7 Capsules (Sen - Min)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val dayAbbrevs = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

            weekDays.forEachIndexed { index, date ->
                val isSelected = date.isEqual(selectedDate)
                val isActualToday = date.isEqual(today)
                val isFuture = date.isAfter(today)
                val dateStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date)
                val record = recordsMap[dateStr]
                val completed = record?.completedCount ?: 0

                val capsuleBg = if (isSelected) PureBlack else SurfaceCard
                val dayTextColor = if (isSelected) {
                    OnPureBlack.copy(alpha = 0.85f)
                } else if (isFuture) {
                    GrayTextMuted.copy(alpha = 0.4f)
                } else {
                    GrayTextSecondary
                }

                val numTextColor = if (isSelected) {
                    OnPureBlack
                } else if (isFuture) {
                    GrayTextMuted.copy(alpha = 0.4f)
                } else {
                    PureBlack
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = capsuleBg,
                    modifier = Modifier
                        .weight(1f)
                        .height(68.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !isFuture) { onSelectDate(date) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dayAbbrevs[index],
                            fontSize = 10.5.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = if (isSelected || isActualToday) FontWeight.Bold else FontWeight.Medium,
                            color = dayTextColor
                        )

                        Text(
                            text = "${date.dayOfMonth}",
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = if (isSelected || isActualToday) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = numTextColor
                        )

                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> OnPureBlack.copy(alpha = 0.7f)
                                        completed == 5 -> GreenSuccess
                                        completed in 1..4 -> GrayTextPrimary
                                        isActualToday -> GrayTextMuted
                                        else -> Color.Transparent
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// CHECKLIST CARD & PRAYER TRENDS
// =========================================================================

@Composable
fun PillarsChecklistCard(
    title: String,
    time: String,
    completedTimestamp: Long?,
    isDone: Boolean,
    onToggle: () -> Unit,
    onEditTime: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val completedTimeStr = remember(completedTimestamp) {
        if (completedTimestamp != null) {
            val df = java.text.SimpleDateFormat("HH:mm", Locale("id", "ID"))
            "${df.format(java.util.Date(completedTimestamp))} WIB"
        } else null
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceCard,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Circular Check-Ring (Pillars style)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isDone) GreenSuccess else Color.Transparent)
                        .border(
                            width = 2.dp,
                            color = if (isDone) GreenSuccess else GrayTextMuted.copy(alpha = 0.5f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = PureBlack
                    )
                    if (completedTimeStr != null && isDone) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GreenSuccess.copy(alpha = 0.10f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onEditTime?.invoke() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Schedule,
                                    contentDescription = "Ubah Waktu",
                                    tint = GreenSuccess,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Selesai $completedTimeStr",
                                    fontSize = 11.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GreenSuccess
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Edit",
                                    tint = GreenSuccess.copy(alpha = 0.7f),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = time,
                    fontSize = 14.sp,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (isDone) GreenSuccess else GrayTextPrimary
                )
                if (isDone && onEditTime != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = CircleShape,
                        color = CanvasBackground,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .clickable { onEditTime.invoke() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = "Ubah Waktu",
                                tint = GrayTextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrayerTrendsMatrixCard(
    allRecords: List<PrayerRecord>,
    modifier: Modifier = Modifier
) {
    val recordsMap = remember(allRecords) {
        allRecords.associateBy { it.date }
    }

    val today = remember { LocalDate.now() }
    val days = remember(today) {
        (20 downTo 0).map { today.minusDays(it.toLong()) }
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SurfaceCard,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            val prayerRows = listOf(
                Pair("FAJR", PrayerType.FAJR),
                Pair("DHUHR", PrayerType.DHUHR),
                Pair("ASR", PrayerType.ASR),
                Pair("MAGHRIB", PrayerType.MAGHRIB),
                Pair("ISHA", PrayerType.ISHA)
            )

            prayerRows.forEach { (label, prayerType) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 9.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = GrayTextMuted,
                        modifier = Modifier.width(55.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        days.forEach { date ->
                            val dateStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date)
                            val record = recordsMap[dateStr]
                            val isCompleted = record?.isCompleted(prayerType) == true

                            Box(
                                modifier = Modifier
                                    .size(9.5.dp)
                                    .clip(RoundedCornerShape(2.5.dp))
                                    .background(
                                        if (isCompleted) GreenSuccess else Color(0xFF143E33).copy(alpha = 0.12f)
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = DividerColor, thickness = 0.6.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Konsistensi 3 Minggu Terakhir",
                    fontSize = 11.sp,
                    fontFamily = PlusJakartaSansFamily,
                    color = GrayTextSecondary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF143E33).copy(alpha = 0.12f))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Belum", fontSize = 10.sp, color = GrayTextMuted)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(GreenSuccess)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Dikerjakan", fontSize = 10.sp, color = GrayTextMuted)
                }
            }
        }
    }
}

// =========================================================================
// CALENDAR STATS & CELLS
// =========================================================================

@Composable
fun CalendarStatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontFamily = PlusJakartaSansFamily,
                color = GrayTextSecondary
            )
        }
    }
}

@Composable
fun CalendarGridDayCell(
    dayNumber: Int,
    record: PrayerRecord?,
    isFuture: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val completed = record?.completedCount ?: 0
    val isAll = completed == 5

    val bgColor = when {
        isFuture -> Color.Transparent
        isAll -> PureBlack
        completed > 0 -> SurfaceCard
        else -> CanvasBackground
    }

    val textColor = when {
        isFuture -> GrayTextMuted.copy(alpha = 0.4f)
        isAll -> OnPureBlack
        else -> PureBlack
    }

    val shape = RoundedCornerShape(11.dp)

    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(shape)
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else if (isToday) 1.5.dp else 0.dp,
                color = if (isSelected) PureBlack else if (isToday) GrayTextPrimary else Color.Transparent,
                shape = shape
            )
            .clickable(enabled = !isFuture) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$dayNumber",
                fontSize = 13.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = if (isAll || isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
            if (completed in 1..4) {
                Spacer(modifier = Modifier.height(1.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    repeat(completed) {
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(PureBlack)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(
    color: Color,
    text: String,
    border: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
                .then(
                    if (border) Modifier.border(
                        1.dp,
                        GrayTextMuted.copy(alpha = 0.4f),
                        RoundedCornerShape(3.dp)
                    ) else Modifier
                )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontFamily = PlusJakartaSansFamily,
            color = GrayTextSecondary
        )
    }
}

@Composable
fun DayHistoryChip(
    record: PrayerRecord,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val completed = record.completedCount
    val isAll = completed == 5
    val dayName = try {
        val date = LocalDate.parse(record.date)
        val today = LocalDate.now()
        when {
            date.isEqual(today) -> "Hari Ini"
            date.isEqual(today.minusDays(1)) -> "Kemarin"
            else -> DateTimeFormatter.ofPattern("EEE", Locale("id", "ID")).format(date)
        }
    } catch (e: Exception) {
        record.date
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) PureBlack else SurfaceCard,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayName,
                fontSize = 11.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) OnPureBlack.copy(alpha = 0.7f) else GrayTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAll) "5/5" else "$completed/5",
                fontSize = 14.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) OnPureBlack else PureBlack
            )
        }
    }
}

// =========================================================================
// CITY SELECTION MODAL BOTTOM SHEET
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectionSheet(
    currentCity: PrayerTimeCalculator.CityCoordinate,
    onSelect: (PrayerTimeCalculator.CityCoordinate) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val allCities = remember { PrayerTimeCalculator.CITIES }

    val filteredCities = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allCities
        } else {
            allCities.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CanvasBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Pilih Kota / Wilayah",
                fontSize = 20.sp,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )
            Text(
                text = "Perhitungan jadwal shalat astronomis offline Kemenag RI",
                fontSize = 12.sp,
                fontFamily = PlusJakartaSansFamily,
                color = GrayTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = GrayTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = PlusJakartaSansFamily,
                            fontSize = 14.sp,
                            color = PureBlack
                        ),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Cari kota atau daerah...",
                                    fontSize = 14.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    color = GrayTextMuted
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hapus",
                            tint = GrayTextSecondary,
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .clickable { searchQuery = "" }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredCities) { city ->
                    val isSelected = city.name == currentCity.name
                    val cityNameParts = city.name.split("/")
                    val displayName = cityNameParts[0].trim()
                    val tz = if (cityNameParts.size > 1) cityNameParts[1].trim() else "WIB"

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) PureBlack else SurfaceCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelect(city) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = displayName,
                                    fontSize = 14.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) OnPureBlack else PureBlack
                                )
                                Text(
                                    text = "$tz • Lat: ${
                                        String.format(
                                            Locale.US,
                                            "%.2f",
                                            city.latitude
                                        )
                                    }, Long: ${String.format(Locale.US, "%.2f", city.longitude)}",
                                    fontSize = 11.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    color = if (isSelected) OnPureBlack.copy(alpha = 0.7f) else GrayTextSecondary
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = OnPureBlack,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// RESET CONFIRMATION DIALOG
// =========================================================================

@Composable
fun ResetConfirmDialog(
    dateLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reset Catatan Shalat?",
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )
        },
        text = {
            Text(
                text = "Semua centang shalat fardhu dan sunnah untuk $dateLabel akan dihapus.",
                fontFamily = PlusJakartaSansFamily,
                fontSize = 13.sp,
                color = GrayTextSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Reset", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = GrayTextSecondary)
            }
        }
    )
}

// =========================================================================
// PRIVACY PLEDGE DIALOG (First-Time User Alert)
// =========================================================================

@Composable
fun PrivacyPledgeDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = SurfaceCard,
            shadowElevation = 20.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CanvasBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = PureBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = CanvasBackground
                    ) {
                        Text(
                            text = "100% OFFLINE & PRIVAT",
                            fontSize = 9.5.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = PureBlack,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ketenangan Tanpa Jejak.",
                    fontSize = 20.sp,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Shalah dibuat sebagai ruang ibadah yang murni—tanpa internet, tanpa akun, dan tanpa pengumpulan data pribadi apa pun.",
                    fontSize = 12.5.sp,
                    fontFamily = PlusJakartaSansFamily,
                    color = GrayTextSecondary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PureBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onDismiss() }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Saya Mengerti & Lanjutkan",
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = OnPureBlack
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// EDIT PRAYER TIME DIALOG & MODEL
// =========================================================================

data class PrayerTimeToEdit(
    val prayerType: PrayerType? = null,
    val sunnahType: SunnahPrayerType? = null,
    val title: String,
    val scheduledTime: LocalTime? = null,
    val initialTimestamp: Long? = null
)

@Composable
fun EditPrayerTimeDialog(
    target: PrayerTimeToEdit,
    onSave: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val initialTime = remember(target) {
        if (target.initialTimestamp != null) {
            val instant = Instant.ofEpochMilli(target.initialTimestamp)
            LocalTime.ofInstant(instant, ZoneId.systemDefault())
        } else if (target.scheduledTime != null) {
            target.scheduledTime
        } else {
            LocalTime.now()
        }
    }

    var selectedHour by remember(target) { mutableIntStateOf(initialTime.hour) }
    var selectedMinute by remember(target) { mutableIntStateOf(initialTime.minute) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = SurfaceCard,
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CanvasBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = PureBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = CanvasBackground,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = GrayTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Sesuaikan Waktu Shalat",
                    fontSize = 18.sp,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${target.title} • Pilih waktu pengerjaan",
                    fontSize = 12.5.sp,
                    fontFamily = PlusJakartaSansFamily,
                    color = GrayTextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive Digital Clock Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Hour Column
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = CanvasBackground,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    selectedHour = if (selectedHour >= 23) 0 else selectedHour + 1
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "+1 Jam",
                                    tint = PureBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = CanvasBackground,
                            modifier = Modifier
                                .width(74.dp)
                                .height(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = String.format(Locale.US, "%02d", selectedHour),
                                    fontSize = 28.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PureBlack
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = CircleShape,
                            color = CanvasBackground,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    selectedHour = if (selectedHour <= 0) 23 else selectedHour - 1
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "-1 Jam",
                                    tint = PureBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = "Jam",
                            fontSize = 10.sp,
                            fontFamily = PlusJakartaSansFamily,
                            color = GrayTextMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Text(
                        text = ":",
                        fontSize = 32.sp,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = PureBlack,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )

                    // Minute Column
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = CanvasBackground,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    selectedMinute = if (selectedMinute >= 59) 0 else selectedMinute + 1
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "+1 Menit",
                                    tint = PureBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = CanvasBackground,
                            modifier = Modifier
                                .width(74.dp)
                                .height(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = String.format(Locale.US, "%02d", selectedMinute),
                                    fontSize = 28.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PureBlack
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = CircleShape,
                            color = CanvasBackground,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    selectedMinute = if (selectedMinute <= 0) 59 else selectedMinute - 1
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "-1 Menit",
                                    tint = PureBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = "Menit",
                            fontSize = 10.sp,
                            fontFamily = PlusJakartaSansFamily,
                            color = GrayTextMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Quick Minute Adjusters (+5m, -5m, +15m, -15m)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickAdjustments = listOf(-15, -5, 5, 15)
                    quickAdjustments.forEach { offset ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = CanvasBackground,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(50))
                                .clickable {
                                    val currentTotalMins = selectedHour * 60 + selectedMinute
                                    val newTotalMins = (currentTotalMins + offset + 1440) % 1440
                                    selectedHour = newTotalMins / 60
                                    selectedMinute = newTotalMins % 60
                                }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (offset > 0) "+$offset" else "$offset",
                                    fontSize = 11.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = PureBlack
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Presets (Adhan Time, Current Time)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (target.scheduledTime != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CanvasBackground,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedHour = target.scheduledTime.hour
                                    selectedMinute = target.scheduledTime.minute
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Waktu Adhan",
                                    fontSize = 10.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    color = GrayTextSecondary
                                )
                                Text(
                                    text = String.format(
                                        Locale.US,
                                        "%02d:%02d",
                                        target.scheduledTime.hour,
                                        target.scheduledTime.minute
                                    ),
                                    fontSize = 12.sp,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = PureBlack
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CanvasBackground,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                val now = LocalTime.now()
                                selectedHour = now.hour
                                selectedMinute = now.minute
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Sekarang",
                                fontSize = 10.sp,
                                fontFamily = PlusJakartaSansFamily,
                                color = GrayTextSecondary
                            )
                            val now = LocalTime.now()
                            Text(
                                text = String.format(Locale.US, "%02d:%02d", now.hour, now.minute),
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.Bold,
                                color = PureBlack
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PureBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSave(selectedHour, selectedMinute) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Simpan Waktu",
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = OnPureBlack
                        )
                    }
                }
            }
        }
    }
}
