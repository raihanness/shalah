package com.shalah.prayer.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.shalah.prayer.MainActivity
import com.shalah.prayer.R
import com.shalah.prayer.ShalahApp
import com.shalah.prayer.alarm.PrayerAlarmScheduler
import com.shalah.prayer.data.AppDatabase
import com.shalah.prayer.data.PrayerRepository
import com.shalah.prayer.data.calculator.PrayerTimeCalculator
import com.shalah.prayer.data.model.PrayerRecord
import com.shalah.prayer.data.model.PrayerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class PrayerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateWidgetsInternalSuspend(context, appWidgetManager, appWidgetIds)
            } finally {
                pendingResult?.finish()
            }
        }
    }

    companion object {
        fun updateAllWidgets(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                updateAllWidgetsDirectly(context)
            }
        }

        suspend fun updateAllWidgetsDirectly(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, PrayerWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                updateWidgetsInternalSuspend(context, appWidgetManager, appWidgetIds)
            }
        }

        private suspend fun updateWidgetsInternalSuspend(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            try {
                val repository = (context.applicationContext as? ShalahApp)?.repository
                    ?: AppDatabase.getDatabase(context).let { db -> PrayerRepository(db.prayerDao(), db.quranDao()) }

                val todayDate = repository.getTodayDate()
                val record = repository.getOrCreateTodayRecord(todayDate)

                val views = buildRemoteViews(context, record)
                for (widgetId in appWidgetIds) {
                    appWidgetManager.updateAppWidget(widgetId, views)
                }
            } catch (e: Exception) {
                android.util.Log.e("PrayerWidgetProvider", "Error updating widget", e)
            }
        }

        fun isDarkTheme(context: Context): Boolean {
            val prefs = context.getSharedPreferences("shalah_user_prefs", Context.MODE_PRIVATE)
            val mode = prefs.getString("app_theme_mode", "system") ?: "system"
            return when (mode) {
                "dark" -> true
                "light" -> false
                else -> {
                    val nightModeFlags = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
                    nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
                }
            }
        }

        private fun buildRemoteViews(context: Context, record: PrayerRecord): RemoteViews {
            val isDark = isDarkTheme(context)
            val views = RemoteViews(context.packageName, R.layout.widget_prayer_tracker)

            // Dynamic card background according to theme
            val cardBg = if (isDark) R.drawable.widget_card_bg_dark else R.drawable.widget_card_background
            views.setInt(R.id.widget_root, "setBackgroundResource", cardBg)

            // Title & Date text colors
            val titleColor = if (isDark) 0xFFF0FDF4.toInt() else 0xFF191C1A.toInt()
            val dateColor = if (isDark) 0xFF94A89E.toInt() else 0xFF5A6660.toInt()
            views.setTextColor(R.id.tv_widget_title, titleColor)
            views.setTextColor(R.id.tv_widget_date, dateColor)

            // Date text
            val dateDisplayFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
            val formattedDate = dateDisplayFormat.format(Date())
            views.setTextViewText(R.id.tv_widget_date, formattedDate)

            // Progress text and bar
            val completed = record.completedCount
            views.setTextViewText(R.id.tv_widget_progress, "$completed / 5")
            val progressBadgeBg = if (isDark) R.drawable.widget_prayer_btn_done_dark else R.drawable.widget_prayer_btn_done
            val progressBadgeText = if (isDark) 0xFF0B100E.toInt() else 0xFFFFFFFF.toInt()
            views.setInt(R.id.tv_widget_progress, "setBackgroundResource", progressBadgeBg)
            views.setTextColor(R.id.tv_widget_progress, progressBadgeText)
            views.setProgressBar(R.id.pb_widget_prayer, 5, completed, false)

            // Header click opens MainActivity
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_header, openAppPendingIntent)

            // Calculate today's astronomical prayer schedule
            val city = PrayerAlarmScheduler.getSavedCity(context)
            val schedule = PrayerTimeCalculator.calculate(
                date = LocalDate.now(),
                latitude = city.latitude,
                longitude = city.longitude
            )
            val timeFormatter = DateTimeFormatter.ofPattern("HH.mm")

            // Configure each prayer item with vector icons & scheduled / checked hour
            bindPrayer(
                context = context,
                views = views,
                buttonId = R.id.btn_prayer_fajr,
                iconId = R.id.iv_fajr_icon,
                iconRes = R.drawable.ic_prayer_fajr,
                nameId = R.id.tv_fajr_name,
                timeId = R.id.tv_fajr_time,
                checkId = R.id.iv_fajr_check,
                prayerType = PrayerType.FAJR,
                scheduledTime = schedule.fajr.format(timeFormatter),
                completedTime = record.fajrTime,
                isCompleted = record.fajr,
                isDark = isDark,
                requestCode = 101
            )

            bindPrayer(
                context = context,
                views = views,
                buttonId = R.id.btn_prayer_dhuhr,
                iconId = R.id.iv_dhuhr_icon,
                iconRes = R.drawable.ic_prayer_dhuhr,
                nameId = R.id.tv_dhuhr_name,
                timeId = R.id.tv_dhuhr_time,
                checkId = R.id.iv_dhuhr_check,
                prayerType = PrayerType.DHUHR,
                scheduledTime = schedule.dhuhr.format(timeFormatter),
                completedTime = record.dhuhrTime,
                isCompleted = record.dhuhr,
                isDark = isDark,
                requestCode = 102
            )

            bindPrayer(
                context = context,
                views = views,
                buttonId = R.id.btn_prayer_asr,
                iconId = R.id.iv_asr_icon,
                iconRes = R.drawable.ic_prayer_asr,
                nameId = R.id.tv_asr_name,
                timeId = R.id.tv_asr_time,
                checkId = R.id.iv_asr_check,
                prayerType = PrayerType.ASR,
                scheduledTime = schedule.asr.format(timeFormatter),
                completedTime = record.asrTime,
                isCompleted = record.asr,
                isDark = isDark,
                requestCode = 103
            )

            bindPrayer(
                context = context,
                views = views,
                buttonId = R.id.btn_prayer_maghrib,
                iconId = R.id.iv_maghrib_icon,
                iconRes = R.drawable.ic_prayer_maghrib,
                nameId = R.id.tv_maghrib_name,
                timeId = R.id.tv_maghrib_time,
                checkId = R.id.iv_maghrib_check,
                prayerType = PrayerType.MAGHRIB,
                scheduledTime = schedule.maghrib.format(timeFormatter),
                completedTime = record.maghribTime,
                isCompleted = record.maghrib,
                isDark = isDark,
                requestCode = 104
            )

            bindPrayer(
                context = context,
                views = views,
                buttonId = R.id.btn_prayer_isha,
                iconId = R.id.iv_isha_icon,
                iconRes = R.drawable.ic_prayer_isha,
                nameId = R.id.tv_isha_name,
                timeId = R.id.tv_isha_time,
                checkId = R.id.iv_isha_check,
                prayerType = PrayerType.ISHA,
                scheduledTime = schedule.isha.format(timeFormatter),
                completedTime = record.ishaTime,
                isCompleted = record.isha,
                isDark = isDark,
                requestCode = 105
            )

            return views
        }

        private fun bindPrayer(
            context: Context,
            views: RemoteViews,
            buttonId: Int,
            iconId: Int,
            iconRes: Int,
            nameId: Int,
            timeId: Int,
            checkId: Int,
            prayerType: PrayerType,
            scheduledTime: String,
            completedTime: Long?,
            isCompleted: Boolean,
            isDark: Boolean,
            requestCode: Int
        ) {
            val bgRes = if (isCompleted) {
                if (isDark) R.drawable.widget_prayer_btn_done_dark else R.drawable.widget_prayer_btn_done
            } else {
                if (isDark) R.drawable.widget_prayer_btn_pending_dark else R.drawable.widget_prayer_btn_pending
            }

            val checkRes = if (isCompleted) {
                R.drawable.ic_check_circle_done
            } else {
                R.drawable.ic_circle_pending
            }

            val textColor = if (isDark) {
                if (isCompleted) 0xFF0B100E.toInt() else 0xFFF0FDF4.toInt()
            } else {
                ContextCompat.getColor(
                    context,
                    if (isCompleted) R.color.widget_text_done else R.color.widget_text_pending
                )
            }

            val timeColor = if (isDark) {
                if (isCompleted) 0xFF134E3E.toInt() else 0xFF94A89E.toInt()
            } else {
                ContextCompat.getColor(
                    context,
                    if (isCompleted) R.color.widget_time_done else R.color.widget_time_pending
                )
            }

            val timeText = if (isCompleted && completedTime != null) {
                val sdf = SimpleDateFormat("HH.mm", Locale.getDefault())
                sdf.format(Date(completedTime))
            } else {
                scheduledTime
            }

            views.setInt(buttonId, "setBackgroundResource", bgRes)
            views.setImageViewResource(checkId, checkRes)
            views.setImageViewResource(iconId, iconRes)
            views.setInt(iconId, "setColorFilter", textColor)
            views.setTextColor(nameId, textColor)
            views.setTextViewText(timeId, timeText)
            views.setTextColor(timeId, timeColor)
            if (isDark && isCompleted) {
                views.setInt(checkId, "setColorFilter", 0xFF0B100E.toInt())
            } else {
                views.setInt(checkId, "setColorFilter", textColor)
            }

            // Click listener
            val intent = PrayerWidgetReceiver.createToggleIntent(context, prayerType)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(buttonId, pendingIntent)
        }
    }
}
