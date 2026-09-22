package com.shalah.prayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persists the active Khatam plan and last-read bookmark state.
 * Stored as a single row in Room SQLite (id = 1).
 */
@Entity(tableName = "quran_progress")
data class QuranProgress(
    @PrimaryKey
    val id: Int = 1,
    val lastSurahNumber: Int = 1,      // 1..114 (Defaults to Al-Fatihah)
    val lastAyahNumber: Int = 1,       // 1..286
    val lastPageNumber: Int = 1,       // 1..604 (Standard Madani Mushaf)
    val lastJuzNumber: Int = 1,        // 1..30
    val lastUpdatedTimestamp: Long = System.currentTimeMillis(),
    val targetKhatamDays: Int = 30,    // Target duration in days (e.g. 30 for 1 juz/day)
    val targetStartDate: String = "",  // yyyy-MM-dd (When current khatam cycle started)
    val completedKhatamCount: Int = 0  // Total lifetime completed khatams
) {
    /**
     * Percentage completed of the 604-page Quran (0 to 100%).
     */
    val progressPercent: Float
        get() = ((lastPageNumber.toFloat() / 604f) * 100f).coerceIn(0f, 100f)

    /**
     * Pages remaining to complete current Khatam.
     */
    val remainingPages: Int
        get() = (604 - lastPageNumber).coerceAtLeast(0)

    /**
     * Recommended pages per day based on target days.
     */
    val pagesPerDayTarget: Int
        get() {
            if (targetKhatamDays <= 0) return 20
            return (604 / targetKhatamDays).coerceAtLeast(1)
        }

    /**
     * Recommended pages to read after each of the 5 daily fardhu prayers.
     */
    val pagesPerPrayerTarget: Int
        get() = ((pagesPerDayTarget + 4) / 5).coerceAtLeast(1)
}
