package com.shalah.prayer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shalah.prayer.data.dao.PrayerDao
import com.shalah.prayer.data.dao.QuranDao
import com.shalah.prayer.data.model.PrayerRecord
import com.shalah.prayer.data.model.QuranDailyLog
import com.shalah.prayer.data.model.QuranProgress

@Database(
    entities = [
        PrayerRecord::class,
        QuranProgress::class,
        QuranDailyLog::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prayerDao(): PrayerDao
    abstract fun quranDao(): QuranDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE prayer_records ADD COLUMN tahajjud INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE prayer_records ADD COLUMN tahajjudTime INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE prayer_records ADD COLUMN dhuha INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE prayer_records ADD COLUMN dhuhaTime INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE prayer_records ADD COLUMN rawatib INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE prayer_records ADD COLUMN rawatibTime INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE prayer_records ADD COLUMN witir INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE prayer_records ADD COLUMN witirTime INTEGER DEFAULT NULL")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS quran_progress (
                        id INTEGER PRIMARY KEY NOT NULL,
                        lastSurahNumber INTEGER NOT NULL,
                        lastAyahNumber INTEGER NOT NULL,
                        lastPageNumber INTEGER NOT NULL,
                        lastJuzNumber INTEGER NOT NULL,
                        lastUpdatedTimestamp INTEGER NOT NULL,
                        targetKhatamDays INTEGER NOT NULL,
                        targetStartDate TEXT NOT NULL,
                        completedKhatamCount INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS quran_daily_logs (
                        date TEXT PRIMARY KEY NOT NULL,
                        pagesRead INTEGER NOT NULL,
                        lastPage INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shalah_prayer_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
