package com.shalah.prayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_records")
data class PrayerRecord(
    @PrimaryKey
    val date: String, // Format: yyyy-MM-dd
    val fajr: Boolean = false,
    val fajrTime: Long? = null,
    val dhuhr: Boolean = false,
    val dhuhrTime: Long? = null,
    val asr: Boolean = false,
    val asrTime: Long? = null,
    val maghrib: Boolean = false,
    val maghribTime: Long? = null,
    val isha: Boolean = false,
    val ishaTime: Long? = null,

    // Sunnah prayers
    val tahajjud: Boolean = false,
    val tahajjudTime: Long? = null,
    val dhuha: Boolean = false,
    val dhuhaTime: Long? = null,
    val rawatib: Boolean = false,
    val rawatibTime: Long? = null,
    val witir: Boolean = false,
    val witirTime: Long? = null
) {
    val completedCount: Int
        get() {
            var count = 0
            if (fajr) count++
            if (dhuhr) count++
            if (asr) count++
            if (maghrib) count++
            if (isha) count++
            return count
        }

    val completedSunnahCount: Int
        get() {
            var count = 0
            if (tahajjud) count++
            if (dhuha) count++
            if (rawatib) count++
            if (witir) count++
            return count
        }

    val completionPercentage: Float
        get() = completedCount / 5.0f

    val isAllCompleted: Boolean
        get() = completedCount == 5

    fun isCompleted(type: PrayerType): Boolean {
        return when (type) {
            PrayerType.FAJR -> fajr
            PrayerType.DHUHR -> dhuhr
            PrayerType.ASR -> asr
            PrayerType.MAGHRIB -> maghrib
            PrayerType.ISHA -> isha
        }
    }

    fun getTime(type: PrayerType): Long? {
        return when (type) {
            PrayerType.FAJR -> fajrTime
            PrayerType.DHUHR -> dhuhrTime
            PrayerType.ASR -> asrTime
            PrayerType.MAGHRIB -> maghribTime
            PrayerType.ISHA -> ishaTime
        }
    }

    fun isSunnahCompleted(type: SunnahPrayerType): Boolean {
        return when (type) {
            SunnahPrayerType.TAHAJJUD -> tahajjud
            SunnahPrayerType.DHUHA -> dhuha
            SunnahPrayerType.RAWATIB -> rawatib
            SunnahPrayerType.WITIR -> witir
        }
    }

    fun getSunnahTime(type: SunnahPrayerType): Long? {
        return when (type) {
            SunnahPrayerType.TAHAJJUD -> tahajjudTime
            SunnahPrayerType.DHUHA -> dhuhaTime
            SunnahPrayerType.RAWATIB -> rawatibTime
            SunnahPrayerType.WITIR -> witirTime
        }
    }

    fun toggle(type: PrayerType, timestamp: Long = System.currentTimeMillis()): PrayerRecord {
        return when (type) {
            PrayerType.FAJR -> {
                val newState = !fajr
                copy(fajr = newState, fajrTime = if (newState) timestamp else null)
            }
            PrayerType.DHUHR -> {
                val newState = !dhuhr
                copy(dhuhr = newState, dhuhrTime = if (newState) timestamp else null)
            }
            PrayerType.ASR -> {
                val newState = !asr
                copy(asr = newState, asrTime = if (newState) timestamp else null)
            }
            PrayerType.MAGHRIB -> {
                val newState = !maghrib
                copy(maghrib = newState, maghribTime = if (newState) timestamp else null)
            }
            PrayerType.ISHA -> {
                val newState = !isha
                copy(isha = newState, ishaTime = if (newState) timestamp else null)
            }
        }
    }

    fun toggleSunnah(type: SunnahPrayerType, timestamp: Long = System.currentTimeMillis()): PrayerRecord {
        return when (type) {
            SunnahPrayerType.TAHAJJUD -> {
                val newState = !tahajjud
                copy(tahajjud = newState, tahajjudTime = if (newState) timestamp else null)
            }
            SunnahPrayerType.DHUHA -> {
                val newState = !dhuha
                copy(dhuha = newState, dhuhaTime = if (newState) timestamp else null)
            }
            SunnahPrayerType.RAWATIB -> {
                val newState = !rawatib
                copy(rawatib = newState, rawatibTime = if (newState) timestamp else null)
            }
            SunnahPrayerType.WITIR -> {
                val newState = !witir
                copy(witir = newState, witirTime = if (newState) timestamp else null)
            }
        }
    }

    fun updatePrayerTime(type: PrayerType, timestamp: Long): PrayerRecord {
        return when (type) {
            PrayerType.FAJR -> copy(fajr = true, fajrTime = timestamp)
            PrayerType.DHUHR -> copy(dhuhr = true, dhuhrTime = timestamp)
            PrayerType.ASR -> copy(asr = true, asrTime = timestamp)
            PrayerType.MAGHRIB -> copy(maghrib = true, maghribTime = timestamp)
            PrayerType.ISHA -> copy(isha = true, ishaTime = timestamp)
        }
    }

    fun updateSunnahPrayerTime(type: SunnahPrayerType, timestamp: Long): PrayerRecord {
        return when (type) {
            SunnahPrayerType.TAHAJJUD -> copy(tahajjud = true, tahajjudTime = timestamp)
            SunnahPrayerType.DHUHA -> copy(dhuha = true, dhuhaTime = timestamp)
            SunnahPrayerType.RAWATIB -> copy(rawatib = true, rawatibTime = timestamp)
            SunnahPrayerType.WITIR -> copy(witir = true, witirTime = timestamp)
        }
    }
}


