package com.shalah.prayer

import android.app.Application
import com.shalah.prayer.data.AppDatabase
import com.shalah.prayer.data.PrayerRepository
import com.shalah.prayer.widget.CompactPrayerWidgetProvider
import com.shalah.prayer.widget.PrayerWidgetProvider

class ShalahApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: PrayerRepository by lazy {
        PrayerRepository(database.prayerDao(), database.quranDao()) {
            // Callback when data changes -> refresh home widgets
            PrayerWidgetProvider.updateAllWidgets(this)
            CompactPrayerWidgetProvider.updateAllWidgets(this)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ShalahApp
            private set
    }
}
