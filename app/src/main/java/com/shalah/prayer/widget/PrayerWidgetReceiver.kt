package com.shalah.prayer.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shalah.prayer.ShalahApp
import com.shalah.prayer.data.AppDatabase
import com.shalah.prayer.data.PrayerRepository
import com.shalah.prayer.data.model.PrayerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrayerWidgetReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_TOGGLE_PRAYER) {
            val prayerId = intent.getStringExtra(EXTRA_PRAYER_ID) ?: return
            val prayerType = PrayerType.fromId(prayerId) ?: return

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val repository = (context.applicationContext as? ShalahApp)?.repository
                        ?: AppDatabase.getDatabase(context).let { db -> PrayerRepository(db.prayerDao(), db.quranDao()) }

                    repository.togglePrayer(
                        date = repository.getTodayDate(),
                        prayerType = prayerType
                    )

                    // Refresh all widgets immediately and await completion before releasing broadcast
                    PrayerWidgetProvider.updateAllWidgetsDirectly(context)
                    CompactPrayerWidgetProvider.updateAllWidgetsDirectly(context)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    companion object {
        const val ACTION_TOGGLE_PRAYER = "com.shalah.prayer.ACTION_TOGGLE_PRAYER"
        const val EXTRA_PRAYER_ID = "extra_prayer_id"

        fun createToggleIntent(context: Context, prayerType: PrayerType): Intent {
            return Intent(context, PrayerWidgetReceiver::class.java).apply {
                action = ACTION_TOGGLE_PRAYER
                putExtra(EXTRA_PRAYER_ID, prayerType.id)
            }
        }
    }
}
