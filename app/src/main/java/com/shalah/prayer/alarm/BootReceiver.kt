package com.shalah.prayer.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shalah.prayer.widget.CompactPrayerWidgetProvider
import com.shalah.prayer.widget.PrayerWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                PrayerAlarmScheduler.scheduleAllUpcoming(context)

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        PrayerWidgetProvider.updateAllWidgetsDirectly(context)
                        CompactPrayerWidgetProvider.updateAllWidgetsDirectly(context)
                    } finally {
                        pendingResult?.finish()
                    }
                }
            }
        }
    }
}
