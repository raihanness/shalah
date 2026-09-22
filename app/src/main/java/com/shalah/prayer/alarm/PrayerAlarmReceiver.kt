package com.shalah.prayer.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.shalah.prayer.MainActivity
import com.shalah.prayer.R

class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "prayer_reminder_alerts_v2"
        const val CHANNEL_NAME = "Pengingat Waktu Shalat"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_NAME) ?: "Shalat"
        val prayerTime = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_TIME) ?: ""
        val ordinal = intent.getIntExtra(PrayerAlarmScheduler.EXTRA_PRAYER_ORDINAL, 0)
        val minutesBefore = intent.getIntExtra(PrayerAlarmScheduler.EXTRA_MINUTES_BEFORE, 30)

        createNotificationChannel(context)

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            (ordinal * 10) + (if (minutesBefore == 30) 1 else 2),
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        val title = when (minutesBefore) {
            0 -> "Waktu $prayerName Telah Tiba"
            10 -> "10 Menit Menuju $prayerName"
            else -> "30 Menit Menuju $prayerName"
        }

        val contentText = when (minutesBefore) {
            0 -> "Waktu shalat $prayerName telah masuk ($prayerTime WIB). Hayya 'alash Shalah."
            10 -> "Waktu $prayerName segera tiba pukul $prayerTime WIB. Segera bersiap untuk shalat."
            else -> "Waktu $prayerName akan masuk pukul $prayerTime WIB. Bersiaplah untuk mengambil wudhu."
        }

        val bigText = when (minutesBefore) {
            0 -> "Allahu Akbar, Allahu Akbar. Waktu shalat $prayerName telah masuk ($prayerTime WIB). Mari segera tunaikan kewajiban shalat dan raih keutamaan shalat di awal waktu."
            10 -> "Waktu $prayerName segera tiba pukul $prayerTime WIB (10 menit lagi). Segera selesaikan aktivitas dan bersiap mendirikan shalat."
            else -> "Waktu $prayerName akan masuk pukul $prayerTime WIB (30 menit lagi). Mari bersiap, membersihkan diri, dan mengambil wudhu."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mosque)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(activityPendingIntent)
            .setSound(soundUri)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .setOnlyAlertOnce(false)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1000 + ordinal, notification)

        // Reschedule to ensure rolling 48-hour coverage
        PrayerAlarmScheduler.scheduleAllUpcoming(context)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Delete legacy silent channel if exists
            try {
                notificationManager.deleteNotificationChannel("prayer_reminder_30min")
            } catch (_: Exception) {}

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pengingat 30 dan 10 menit sebelum waktu shalat fardhu tiba"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
