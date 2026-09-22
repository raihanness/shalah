package com.shalah.prayer.data

import com.shalah.prayer.data.dao.PrayerDao
import com.shalah.prayer.data.dao.QuranDao
import com.shalah.prayer.data.model.PrayerRecord
import com.shalah.prayer.data.model.PrayerType
import com.shalah.prayer.data.model.QuranDailyLog
import com.shalah.prayer.data.model.QuranData
import com.shalah.prayer.data.model.QuranProgress
import com.shalah.prayer.data.model.SunnahPrayerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrayerRepository(
    private val dao: PrayerDao,
    private val quranDao: QuranDao? = null,
    private val onDataChanged: (() -> Unit)? = null
) {
    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // =========================================================================
    // SHALAH TRACKER OPERATIONS
    // =========================================================================

    fun getRecordFlow(date: String): Flow<PrayerRecord?> {
        return dao.getRecordFlow(date)
    }

    fun getRecentRecordsFlow(limit: Int = 7): Flow<List<PrayerRecord>> {
        return dao.getRecentRecordsFlow(limit)
    }

    fun getFullPrayerDaysCountFlow(): Flow<Int> {
        return dao.getFullPrayerDaysCountFlow()
    }

    fun getRecordsForMonthFlow(monthPrefix: String): Flow<List<PrayerRecord>> {
        return dao.getRecordsForMonthFlow(monthPrefix)
    }

    fun getAllRecordsFlow(): Flow<List<PrayerRecord>> {
        return dao.getAllRecordsFlow()
    }

    suspend fun getAllRecords(): List<PrayerRecord> = withContext(Dispatchers.IO) {
        dao.getAllRecords()
    }

    suspend fun restoreRecords(records: List<PrayerRecord>) = withContext(Dispatchers.IO) {
        dao.upsertRecords(records)
        onDataChanged?.invoke()
    }

    suspend fun getOrCreateTodayRecord(date: String = getTodayDate()): PrayerRecord =
        withContext(Dispatchers.IO) {
            val existing = dao.getRecord(date)
            if (existing != null) {
                existing
            } else {
                val newRecord = PrayerRecord(date = date)
                dao.upsertRecord(newRecord)
                newRecord
            }
        }

    suspend fun togglePrayer(
        date: String = getTodayDate(),
        prayerType: PrayerType
    ): PrayerRecord = withContext(Dispatchers.IO) {
        val currentRecord = dao.getRecord(date) ?: PrayerRecord(date = date)
        val updatedRecord = currentRecord.toggle(prayerType)
        dao.upsertRecord(updatedRecord)
        onDataChanged?.invoke()
        updatedRecord
    }

    suspend fun toggleSunnahPrayer(
        date: String = getTodayDate(),
        sunnahType: SunnahPrayerType
    ): PrayerRecord = withContext(Dispatchers.IO) {
        val currentRecord = dao.getRecord(date) ?: PrayerRecord(date = date)
        val updatedRecord = currentRecord.toggleSunnah(sunnahType)
        dao.upsertRecord(updatedRecord)
        onDataChanged?.invoke()
        updatedRecord
    }

    suspend fun updatePrayerTime(
        date: String = getTodayDate(),
        prayerType: PrayerType,
        timestamp: Long
    ): PrayerRecord = withContext(Dispatchers.IO) {
        val currentRecord = dao.getRecord(date) ?: PrayerRecord(date = date)
        val updatedRecord = currentRecord.updatePrayerTime(prayerType, timestamp)
        dao.upsertRecord(updatedRecord)
        onDataChanged?.invoke()
        updatedRecord
    }

    suspend fun updateSunnahPrayerTime(
        date: String = getTodayDate(),
        sunnahType: SunnahPrayerType,
        timestamp: Long
    ): PrayerRecord = withContext(Dispatchers.IO) {
        val currentRecord = dao.getRecord(date) ?: PrayerRecord(date = date)
        val updatedRecord = currentRecord.updateSunnahPrayerTime(sunnahType, timestamp)
        dao.upsertRecord(updatedRecord)
        onDataChanged?.invoke()
        updatedRecord
    }

    suspend fun setPrayer(
        date: String = getTodayDate(),
        prayerType: PrayerType,
        completed: Boolean
    ): PrayerRecord = withContext(Dispatchers.IO) {
        val current = dao.getRecord(date) ?: PrayerRecord(date = date)
        val isCurrentCompleted = current.isCompleted(prayerType)
        if (isCurrentCompleted == completed) return@withContext current

        val updated = current.toggle(prayerType)
        dao.upsertRecord(updated)
        onDataChanged?.invoke()
        updated
    }

    suspend fun resetTodayRecord(date: String = getTodayDate()): PrayerRecord =
        withContext(Dispatchers.IO) {
            val emptyRecord = PrayerRecord(date = date)
            dao.upsertRecord(emptyRecord)
            onDataChanged?.invoke()
            emptyRecord
        }

    // =========================================================================
    // QURAN READ & KHATAM TRACKER OPERATIONS
    // =========================================================================

    fun getQuranProgressFlow(): Flow<QuranProgress?> {
        return quranDao?.getQuranProgressFlow() ?: flowOf(null)
    }

    suspend fun getOrCreateQuranProgress(): QuranProgress = withContext(Dispatchers.IO) {
        val existing = quranDao?.getQuranProgress()
        if (existing != null) {
            existing
        } else {
            val initial = QuranProgress(
                id = 1,
                lastSurahNumber = 1,
                lastAyahNumber = 1,
                lastPageNumber = 1,
                lastJuzNumber = 1,
                lastUpdatedTimestamp = System.currentTimeMillis(),
                targetKhatamDays = 30,
                targetStartDate = getTodayDate(),
                completedKhatamCount = 0
            )
            quranDao?.upsertQuranProgress(initial)
            initial
        }
    }

    suspend fun updateQuranBookmark(
        surahNumber: Int,
        ayahNumber: Int,
        pageNumber: Int
    ): QuranProgress = withContext(Dispatchers.IO) {
        val current = getOrCreateQuranProgress()
        val clampedPage = pageNumber.coerceIn(1, 604)
        val clampedSurah = surahNumber.coerceIn(1, 114)
        val surahInfo = QuranData.getSurahByNumber(clampedSurah)
        val clampedAyah = ayahNumber.coerceIn(1, surahInfo.totalAyah)
        val computedJuz = QuranData.getJuzByPage(clampedPage)

        val updated = current.copy(
            lastSurahNumber = clampedSurah,
            lastAyahNumber = clampedAyah,
            lastPageNumber = clampedPage,
            lastJuzNumber = computedJuz,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        quranDao?.upsertQuranProgress(updated)

        // Update daily reading log
        val today = getTodayDate()
        val currentLog = quranDao?.getDailyLog(today) ?: QuranDailyLog(date = today, pagesRead = 0, lastPage = current.lastPageNumber)
        val deltaPages = (clampedPage - current.lastPageNumber).coerceAtLeast(0)
        val updatedLog = currentLog.copy(
            pagesRead = currentLog.pagesRead + deltaPages,
            lastPage = clampedPage,
            timestamp = System.currentTimeMillis()
        )
        quranDao?.upsertDailyLog(updatedLog)

        onDataChanged?.invoke()
        updated
    }

    suspend fun addQuranPagesRead(pagesToAdd: Int): QuranProgress = withContext(Dispatchers.IO) {
        val current = getOrCreateQuranProgress()
        val newPage = (current.lastPageNumber + pagesToAdd).coerceIn(1, 604)
        val surah = QuranData.getSurahByPage(newPage)
        val juz = QuranData.getJuzByPage(newPage)

        val updated = current.copy(
            lastSurahNumber = surah.number,
            lastAyahNumber = 1,
            lastPageNumber = newPage,
            lastJuzNumber = juz,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        quranDao?.upsertQuranProgress(updated)

        // Update daily log
        val today = getTodayDate()
        val currentLog = quranDao?.getDailyLog(today) ?: QuranDailyLog(date = today, pagesRead = 0, lastPage = current.lastPageNumber)
        val updatedLog = currentLog.copy(
            pagesRead = currentLog.pagesRead + pagesToAdd.coerceAtLeast(0),
            lastPage = newPage,
            timestamp = System.currentTimeMillis()
        )
        quranDao?.upsertDailyLog(updatedLog)

        onDataChanged?.invoke()
        updated
    }

    suspend fun setQuranKhatamTarget(targetDays: Int): QuranProgress = withContext(Dispatchers.IO) {
        val current = getOrCreateQuranProgress()
        val updated = current.copy(
            targetKhatamDays = targetDays.coerceIn(1, 365),
            targetStartDate = if (current.targetStartDate.isEmpty()) getTodayDate() else current.targetStartDate,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        quranDao?.upsertQuranProgress(updated)
        onDataChanged?.invoke()
        updated
    }

    suspend fun completeQuranKhatam(): QuranProgress = withContext(Dispatchers.IO) {
        val current = getOrCreateQuranProgress()
        val updated = current.copy(
            lastSurahNumber = 1,
            lastAyahNumber = 1,
            lastPageNumber = 1,
            lastJuzNumber = 1,
            completedKhatamCount = current.completedKhatamCount + 1,
            targetStartDate = getTodayDate(),
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        quranDao?.upsertQuranProgress(updated)
        onDataChanged?.invoke()
        updated
    }

    suspend fun resetQuranProgress(): QuranProgress = withContext(Dispatchers.IO) {
        val current = getOrCreateQuranProgress()
        val updated = current.copy(
            lastSurahNumber = 1,
            lastAyahNumber = 1,
            lastPageNumber = 1,
            lastJuzNumber = 1,
            targetStartDate = getTodayDate(),
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        quranDao?.upsertQuranProgress(updated)
        onDataChanged?.invoke()
        updated
    }

    fun getDailyLogFlow(date: String = getTodayDate()): Flow<QuranDailyLog?> {
        return quranDao?.getDailyLogFlow(date) ?: flowOf(null)
    }

    fun getRecentDailyLogsFlow(limit: Int = 7): Flow<List<QuranDailyLog>> {
        return quranDao?.getRecentDailyLogsFlow(limit) ?: flowOf(emptyList())
    }

    fun getAllDailyLogsFlow(): Flow<List<QuranDailyLog>> {
        return quranDao?.getAllDailyLogsFlow() ?: flowOf(emptyList())
    }

    suspend fun getAllDailyLogs(): List<QuranDailyLog> = withContext(Dispatchers.IO) {
        quranDao?.getAllDailyLogs() ?: emptyList()
    }

    suspend fun restoreQuranData(
        progress: QuranProgress?,
        logs: List<QuranDailyLog>
    ) = withContext(Dispatchers.IO) {
        if (progress != null) {
            quranDao?.upsertQuranProgress(progress)
        }
        if (logs.isNotEmpty()) {
            quranDao?.upsertDailyLogs(logs)
        }
        onDataChanged?.invoke()
    }
}
