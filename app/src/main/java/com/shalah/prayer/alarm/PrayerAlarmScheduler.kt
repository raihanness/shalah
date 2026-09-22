package com.shalah.prayer.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.shalah.prayer.data.calculator.PrayerTimeCalculator
import com.shalah.prayer.data.model.PrayerType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Manages 100% offline scheduling of discreet prayer reminders 30 minutes and 10 minutes before each prayer time.
 */
object PrayerAlarmScheduler {

    const val PREFS_NAME = "shalah_alarm_prefs"
    const val KEY_REMINDER_ENABLED = "reminder_30min_enabled"
    const val KEY_SELECTED_CITY_NAME = "reminder_selected_city"

    const val EXTRA_PRAYER_NAME = "extra_prayer_name"
    const val EXTRA_PRAYER_TIME = "extra_prayer_time"
    const val EXTRA_PRAYER_ORDINAL = "extra_prayer_ordinal"
    const val EXTRA_MINUTES_BEFORE = "extra_minutes_before"

    val INTERVALS = listOf(30, 10, 0)

    fun isReminderEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_REMINDER_ENABLED, false)
    }

    fun setReminderEnabled(context: Context, enabled: Boolean, city: PrayerTimeCalculator.CityCoordinate = PrayerTimeCalculator.DEFAULT_CITY) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_REMINDER_ENABLED, enabled)
            .putString(KEY_SELECTED_CITY_NAME, city.name)
            .apply()

        if (enabled) {
            scheduleAllUpcoming(context, city)
        } else {
            cancelAll(context)
        }
    }

    fun getSavedCity(context: Context): PrayerTimeCalculator.CityCoordinate {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cityName = prefs.getString(KEY_SELECTED_CITY_NAME, PrayerTimeCalculator.DEFAULT_CITY.name)
        return PrayerTimeCalculator.CITIES.firstOrNull { it.name == cityName } ?: PrayerTimeCalculator.DEFAULT_CITY
    }

    fun scheduleAllUpcoming(context: Context, city: PrayerTimeCalculator.CityCoordinate = getSavedCity(context)) {
        if (!isReminderEnabled(context)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val now = LocalDateTime.now()
        val zoneId = ZoneId.systemDefault()
        val timeFormatter = DateTimeFormatter.ofPattern("HH.mm")

        // Schedule for today and tomorrow to guarantee zero gaps
        listOf(LocalDate.now(), LocalDate.now().plusDays(1)).forEach { date ->
            val schedule = PrayerTimeCalculator.calculate(
                date = date,
                latitude = city.latitude,
                longitude = city.longitude,
                zoneId = zoneId
            )

            val prayerTimes = mapOf(
                PrayerType.FAJR to schedule.fajr,
                PrayerType.DHUHR to schedule.dhuhr,
                PrayerType.ASR to schedule.asr,
                PrayerType.MAGHRIB to schedule.maghrib,
                PrayerType.ISHA to schedule.isha
            )

            INTERVALS.forEach { minutesBefore ->
                prayerTimes.forEach { (type, prayerTime) ->
                    // Calculate target alarm time (30m or 10m prior)
                    val targetDateTime = LocalDateTime.of(date, prayerTime).minusMinutes(minutesBefore.toLong())

                    // Only schedule if target time is in the future
                    if (targetDateTime.isAfter(now)) {
                        val epochMillis = targetDateTime.atZone(zoneId).toInstant().toEpochMilli()
                        val requestCode = generateRequestCode(date, type, minutesBefore)

                        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                            putExtra(EXTRA_PRAYER_NAME, type.indonesianName)
                            putExtra(EXTRA_PRAYER_TIME, prayerTime.format(timeFormatter))
                            putExtra(EXTRA_PRAYER_ORDINAL, type.ordinal)
                            putExtra(EXTRA_MINUTES_BEFORE, minutesBefore)
                        }

                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            requestCode,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                if (alarmManager.canScheduleExactAlarms()) {
                                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
                                } else {
                                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
                                }
                            } else {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
                            }
                        } catch (e: SecurityException) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
                        }
                    }
                }
            }
        }
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        listOf(LocalDate.now(), LocalDate.now().plusDays(1)).forEach { date ->
            PrayerType.entries.forEach { type ->
                INTERVALS.forEach { minutesBefore ->
                    val requestCode = generateRequestCode(date, type, minutesBefore)
                    val intent = Intent(context, PrayerAlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                    )
                    if (pendingIntent != null) {
                        alarmManager.cancel(pendingIntent)
                        pendingIntent.cancel()
                    }
                }
            }
        }
    }

    private fun generateRequestCode(date: LocalDate, type: PrayerType, minutesBefore: Int): Int {
        return (date.dayOfYear * 1000) + (type.ordinal * 100) + minutesBefore
    }
}
