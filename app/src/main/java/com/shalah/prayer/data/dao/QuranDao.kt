package com.shalah.prayer.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shalah.prayer.data.model.QuranDailyLog
import com.shalah.prayer.data.model.QuranProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {

    // --- Quran Progress / Khatam State ---

    @Query("SELECT * FROM quran_progress WHERE id = 1 LIMIT 1")
    fun getQuranProgressFlow(): Flow<QuranProgress?>

    @Query("SELECT * FROM quran_progress WHERE id = 1 LIMIT 1")
    suspend fun getQuranProgress(): QuranProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuranProgress(progress: QuranProgress)

    // --- Daily Reading Logs ---

    @Query("SELECT * FROM quran_daily_logs WHERE date = :date LIMIT 1")
    fun getDailyLogFlow(date: String): Flow<QuranDailyLog?>

    @Query("SELECT * FROM quran_daily_logs WHERE date = :date LIMIT 1")
    suspend fun getDailyLog(date: String): QuranDailyLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyLog(log: QuranDailyLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyLogs(logs: List<QuranDailyLog>)

    @Query("SELECT * FROM quran_daily_logs ORDER BY date DESC LIMIT :limit")
    fun getRecentDailyLogsFlow(limit: Int): Flow<List<QuranDailyLog>>

    @Query("SELECT * FROM quran_daily_logs ORDER BY date ASC")
    suspend fun getAllDailyLogs(): List<QuranDailyLog>

    @Query("SELECT * FROM quran_daily_logs ORDER BY date DESC")
    fun getAllDailyLogsFlow(): Flow<List<QuranDailyLog>>

    @Query("DELETE FROM quran_daily_logs")
    suspend fun clearDailyLogs()
}
