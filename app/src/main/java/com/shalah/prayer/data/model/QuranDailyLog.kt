package com.shalah.prayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores daily Quran reading logs for streaks and consistency metrics.
 * One row per calendar date (yyyy-MM-dd).
 */
@Entity(tableName = "quran_daily_logs")
data class QuranDailyLog(
    @PrimaryKey
    val date: String,             // yyyy-MM-dd
    val pagesRead: Int = 0,       // Total pages read on this date
    val lastPage: Int = 1,        // Last page reached on this date
    val timestamp: Long = System.currentTimeMillis()
)
