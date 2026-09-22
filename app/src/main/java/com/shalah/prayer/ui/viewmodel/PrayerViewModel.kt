package com.shalah.prayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shalah.prayer.data.PrayerRepository
import com.shalah.prayer.data.backup.BackupManager
import com.shalah.prayer.data.calculator.PrayerTimeCalculator
import com.shalah.prayer.data.model.PrayerRecord
import com.shalah.prayer.data.model.PrayerType
import com.shalah.prayer.data.model.QuranDailyLog
import com.shalah.prayer.data.model.QuranProgress
import com.shalah.prayer.data.model.SunnahPrayerType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class PrayerViewModel(
    private val repository: PrayerRepository
) : ViewModel() {

    private val localeId = Locale("id", "ID")
    private val isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", localeId)
    private val shortDateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", localeId)

    // Current selected date (defaults to today)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Selected city for offline prayer calculations
    private val _selectedCity = MutableStateFlow(PrayerTimeCalculator.DEFAULT_CITY)
    val selectedCity: StateFlow<PrayerTimeCalculator.CityCoordinate> = _selectedCity.asStateFlow()

    // Collapsible Sunnah Prayer Section state
    private val _isSunnahExpanded = MutableStateFlow(false)
    val isSunnahExpanded: StateFlow<Boolean> = _isSunnahExpanded.asStateFlow()

    // Monthly Calendar & Heatmap State
    private val _calendarYearMonth = MutableStateFlow(YearMonth.now())
    val calendarYearMonth: StateFlow<YearMonth> = _calendarYearMonth.asStateFlow()

    val calendarMonthName: StateFlow<String> = _calendarYearMonth.map { ym ->
        DateTimeFormatter.ofPattern("MMMM yyyy", localeId).format(ym)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        DateTimeFormatter.ofPattern("MMMM yyyy", localeId).format(YearMonth.now())
    )

    val monthRecordsMap: StateFlow<Map<String, PrayerRecord>> = _calendarYearMonth
        .flatMapLatest { ym ->
            val prefix = ym.format(DateTimeFormatter.ofPattern("yyyy-MM"))
            repository.getRecordsForMonthFlow(prefix).map { list ->
                list.associateBy { it.date }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Formatted selected date: yyyy-MM-dd
    val selectedDateString: StateFlow<String> = _selectedDate.map {
        it.format(isoFormatter)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, repository.getTodayDate())

    // Check if the selected date is today
    val isSelectedDateToday: StateFlow<Boolean> = _selectedDate.map {
        it.isEqual(LocalDate.now())
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    // Formatted full date string for selected day
    val selectedFullDateText: StateFlow<String> = _selectedDate.map {
        it.format(fullDateFormatter)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalDate.now().format(fullDateFormatter)
    )

    // Relative day label: "Hari Ini", "Kemarin", or "10 Sep 2026"
    val relativeDateLabel: StateFlow<String> = _selectedDate.map { date ->
        val today = LocalDate.now()
        when {
            date.isEqual(today) -> "Hari Ini"
            date.isEqual(today.minusDays(1)) -> "Kemarin"
            else -> date.format(shortDateFormatter)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "Hari Ini")

    // Hijri date calculation
    val selectedHijriDateText: StateFlow<String> = _selectedDate.map { date ->
        try {
            val hijrah = HijrahDate.from(date)
            val day = hijrah.get(ChronoField.DAY_OF_MONTH)
            val month = hijrah.get(ChronoField.MONTH_OF_YEAR)
            val year = hijrah.get(ChronoField.YEAR)
            val monthNames = arrayOf(
                "Muharram", "Safar", "Rabi'ul Awwal", "Rabi'ul Akhir",
                "Jumadil Awwal", "Jumadil Akhir", "Rajab", "Sya'ban",
                "Ramadhan", "Syawwal", "Dzulqa'dah", "Dzulhijjah"
            )
            val monthName = if (month in 1..12) monthNames[month - 1] else ""
            "$day $monthName $year H"
        } catch (e: Exception) {
            "1448 H"
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "1448 H")

    // Reactive 100% offline calculated prayer schedule
    val prayerSchedule: StateFlow<PrayerTimeCalculator.PrayerSchedule> = combine(_selectedDate, _selectedCity) { date, city ->
        PrayerTimeCalculator.calculate(
            date = date,
            latitude = city.latitude,
            longitude = city.longitude
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        PrayerTimeCalculator.calculate(LocalDate.now())
    )

    // Active prayer window (only computed when viewing today)
    val activePrayerType: StateFlow<PrayerType?> = combine(isSelectedDateToday, prayerSchedule) { isToday, schedule ->
        if (!isToday) return@combine null

        val now = LocalTime.now()
        when {
            now >= schedule.fajr && now < schedule.sunrise -> PrayerType.FAJR
            now >= schedule.dhuhr && now < schedule.asr -> PrayerType.DHUHR
            now >= schedule.asr && now < schedule.maghrib -> PrayerType.ASR
            now >= schedule.maghrib && now < schedule.isha -> PrayerType.MAGHRIB
            now >= schedule.isha || now < schedule.fajr -> PrayerType.ISHA
            else -> null // Morning Dhuha period
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Reactive Flow for the currently selected day's PrayerRecord
    val currentRecord: StateFlow<PrayerRecord> = selectedDateString
        .flatMapLatest { dateStr ->
            repository.getRecordFlow(dateStr).map { it ?: PrayerRecord(date = dateStr) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PrayerRecord(date = repository.getTodayDate())
        )

    // Last 7 days history
    val recentRecords: StateFlow<List<PrayerRecord>> = repository.getRecentRecordsFlow(7)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Count of full 5/5 prayer days
    val fullDaysCompletedCount: StateFlow<Int> = repository.getFullPrayerDaysCountFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Total recorded days count
    val totalRecordsCount: StateFlow<Int> = repository.getAllRecordsFlow()
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // All records for Prayer Trends heatmap matrix
    val allRecords: StateFlow<List<PrayerRecord>> = repository.getAllRecordsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current continuous streak of days with completed prayers
    val currentStreak: StateFlow<Int> = repository.getAllRecordsFlow().map { records ->
        val recordMap = records.associateBy { it.date }
        var streak = 0
        var checkDate = LocalDate.now()
        val todayStr = checkDate.format(isoFormatter)
        val todayRecord = recordMap[todayStr]

        if (todayRecord != null && todayRecord.completedCount > 0) {
            streak++
            checkDate = checkDate.minusDays(1)
        } else {
            checkDate = checkDate.minusDays(1)
        }

        while (true) {
            val rec = recordMap[checkDate.format(isoFormatter)]
            if (rec != null && rec.completedCount > 0) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }
        streak
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // =========================================================================
    // QURAN READ & KHATAM TRACKER STATE FLOWS
    // =========================================================================

    val quranProgress: StateFlow<QuranProgress> = repository.getQuranProgressFlow()
        .map { it ?: QuranProgress() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QuranProgress()
        )

    val todayQuranLog: StateFlow<QuranDailyLog?> = repository.getDailyLogFlow(repository.getTodayDate())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val recentQuranLogs: StateFlow<List<QuranDailyLog>> = repository.getRecentDailyLogsFlow(7)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val quranStreak: StateFlow<Int> = repository.getAllDailyLogsFlow().map { logs ->
        val logMap = logs.associateBy { it.date }
        var streak = 0
        var checkDate = LocalDate.now()
        val todayStr = checkDate.format(isoFormatter)
        val todayLog = logMap[todayStr]

        if (todayLog != null && todayLog.pagesRead > 0) {
            streak++
            checkDate = checkDate.minusDays(1)
        } else {
            checkDate = checkDate.minusDays(1)
        }

        while (true) {
            val log = logMap[checkDate.format(isoFormatter)]
            if (log != null && log.pagesRead > 0) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }
        streak
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.getOrCreateTodayRecord(repository.getTodayDate())
            repository.getOrCreateQuranProgress()
        }
    }

    // =========================================================================
    // SHALAH ACTIONS
    // =========================================================================

    fun previousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    fun nextDay() {
        val next = _selectedDate.value.plusDays(1)
        if (!next.isAfter(LocalDate.now())) {
            _selectedDate.value = next
        }
    }

    fun goToToday() {
        _selectedDate.value = LocalDate.now()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun selectDate(dateString: String) {
        try {
            _selectedDate.value = LocalDate.parse(dateString, isoFormatter)
        } catch (e: Exception) {
            // ignore parse error
        }
    }

    fun selectCity(city: PrayerTimeCalculator.CityCoordinate) {
        _selectedCity.value = city
    }

    fun togglePrayer(prayerType: PrayerType) {
        val dateStr = selectedDateString.value
        viewModelScope.launch {
            repository.togglePrayer(dateStr, prayerType)
        }
    }

    fun updatePrayerTime(prayerType: PrayerType, hour: Int, minute: Int) {
        val dateStr = selectedDateString.value
        val localDateTime = _selectedDate.value.atTime(hour.coerceIn(0, 23), minute.coerceIn(0, 59))
        val timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        viewModelScope.launch {
            repository.updatePrayerTime(dateStr, prayerType, timestamp)
        }
    }

    fun toggleSunnahExpanded() {
        _isSunnahExpanded.value = !_isSunnahExpanded.value
    }

    fun toggleSunnahPrayer(sunnahType: SunnahPrayerType) {
        val dateStr = selectedDateString.value
        viewModelScope.launch {
            repository.toggleSunnahPrayer(dateStr, sunnahType)
        }
    }

    fun updateSunnahPrayerTime(sunnahType: SunnahPrayerType, hour: Int, minute: Int) {
        val dateStr = selectedDateString.value
        val localDateTime = _selectedDate.value.atTime(hour.coerceIn(0, 23), minute.coerceIn(0, 59))
        val timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        viewModelScope.launch {
            repository.updateSunnahPrayerTime(dateStr, sunnahType, timestamp)
        }
    }

    fun resetCurrentDay() {
        val dateStr = selectedDateString.value
        viewModelScope.launch {
            repository.resetTodayRecord(dateStr)
        }
    }

    fun previousCalendarMonth() {
        _calendarYearMonth.value = _calendarYearMonth.value.minusMonths(1)
    }

    fun nextCalendarMonth() {
        val next = _calendarYearMonth.value.plusMonths(1)
        if (!next.isAfter(YearMonth.now())) {
            _calendarYearMonth.value = next
        }
    }

    fun resetCalendarMonth() {
        _calendarYearMonth.value = YearMonth.now()
    }

    fun syncCalendarToSelectedDate() {
        _calendarYearMonth.value = YearMonth.from(_selectedDate.value)
    }

    // =========================================================================
    // QURAN ACTIONS
    // =========================================================================

    fun updateBookmark(surahNumber: Int, ayahNumber: Int, pageNumber: Int) {
        viewModelScope.launch {
            repository.updateQuranBookmark(surahNumber, ayahNumber, pageNumber)
        }
    }

    fun addPagesRead(pagesToAdd: Int) {
        viewModelScope.launch {
            repository.addQuranPagesRead(pagesToAdd)
        }
    }

    fun setKhatamTarget(targetDays: Int) {
        viewModelScope.launch {
            repository.setQuranKhatamTarget(targetDays)
        }
    }

    fun completeKhatam() {
        viewModelScope.launch {
            repository.completeQuranKhatam()
        }
    }

    fun resetQuranProgress() {
        viewModelScope.launch {
            repository.resetQuranProgress()
        }
    }

    // =========================================================================
    // BACKUP & RESTORE
    // =========================================================================

    suspend fun getBackupJson(): String {
        val records = repository.getAllRecords()
        val qProgress = repository.getOrCreateQuranProgress()
        val qLogs = repository.getAllDailyLogs()
        return BackupManager.exportToJson(records, qProgress, qLogs)
    }

    suspend fun restoreBackup(jsonString: String): Result<Int> {
        return try {
            val backupData = BackupManager.importFromJson(jsonString)
            repository.restoreRecords(backupData.records)
            repository.restoreQuranData(backupData.quranProgress, backupData.quranDailyLogs)
            Result.success(backupData.records.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getScheduledTimeForPrayer(prayerType: PrayerType, schedule: PrayerTimeCalculator.PrayerSchedule): String {
        val time = when (prayerType) {
            PrayerType.FAJR -> schedule.fajr
            PrayerType.DHUHR -> schedule.dhuhr
            PrayerType.ASR -> schedule.asr
            PrayerType.MAGHRIB -> schedule.maghrib
            PrayerType.ISHA -> schedule.isha
        }
        return DateTimeFormatter.ofPattern("HH.mm").format(time)
    }
}
