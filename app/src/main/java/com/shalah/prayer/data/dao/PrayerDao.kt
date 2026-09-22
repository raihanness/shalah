package com.shalah.prayer.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shalah.prayer.data.model.PrayerRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayer_records WHERE date = :date LIMIT 1")
    fun getRecordFlow(date: String): Flow<PrayerRecord?>

    @Query("SELECT * FROM prayer_records WHERE date = :date LIMIT 1")
    suspend fun getRecord(date: String): PrayerRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecord(record: PrayerRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecords(records: List<PrayerRecord>)

    @Query("SELECT * FROM prayer_records ORDER BY date ASC")
    suspend fun getAllRecords(): List<PrayerRecord>

    @Query("SELECT * FROM prayer_records ORDER BY date DESC LIMIT :limit")
    fun getRecentRecordsFlow(limit: Int): Flow<List<PrayerRecord>>

    @Query("SELECT * FROM prayer_records WHERE date LIKE :monthPrefix || '%' ORDER BY date ASC")
    fun getRecordsForMonthFlow(monthPrefix: String): Flow<List<PrayerRecord>>

    @Query("SELECT * FROM prayer_records ORDER BY date DESC")
    fun getAllRecordsFlow(): Flow<List<PrayerRecord>>

    @Query("SELECT COUNT(*) FROM prayer_records WHERE (fajr = 1 AND dhuhr = 1 AND asr = 1 AND maghrib = 1 AND isha = 1)")
    fun getFullPrayerDaysCountFlow(): Flow<Int>
}
