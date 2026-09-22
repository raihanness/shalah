package com.shalah.prayer.data.backup

import android.content.Context
import android.net.Uri
import com.shalah.prayer.data.model.PrayerRecord
import com.shalah.prayer.data.model.QuranDailyLog
import com.shalah.prayer.data.model.QuranProgress
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * 100% Offline, privacy-first Backup & Restore manager for Shalah.
 * Serializes and deserializes PrayerRecord and Quran tracking data to and from clean JSON.
 */
object BackupManager {

    private const val APP_IDENTIFIER = "Shalah"
    private const val BACKUP_SCHEMA_VERSION = 2

    data class ShalahBackupData(
        val records: List<PrayerRecord>,
        val quranProgress: QuranProgress? = null,
        val quranDailyLogs: List<QuranDailyLog> = emptyList()
    )

    /**
     * Serializes all prayer records and Quran data into a formatted JSON string.
     */
    fun exportToJson(
        records: List<PrayerRecord>,
        quranProgress: QuranProgress? = null,
        quranDailyLogs: List<QuranDailyLog> = emptyList()
    ): String {
        val root = JSONObject().apply {
            put("app", APP_IDENTIFIER)
            put("version", BACKUP_SCHEMA_VERSION)
            put("exportedAt", System.currentTimeMillis())
            put("recordsCount", records.size)

            val array = JSONArray()
            records.forEach { record ->
                val obj = JSONObject().apply {
                    put("date", record.date)
                    // Fardhu prayers
                    put("fajr", record.fajr)
                    if (record.fajrTime != null) put("fajrTime", record.fajrTime) else put("fajrTime", JSONObject.NULL)
                    put("dhuhr", record.dhuhr)
                    if (record.dhuhrTime != null) put("dhuhrTime", record.dhuhrTime) else put("dhuhrTime", JSONObject.NULL)
                    put("asr", record.asr)
                    if (record.asrTime != null) put("asrTime", record.asrTime) else put("asrTime", JSONObject.NULL)
                    put("maghrib", record.maghrib)
                    if (record.maghribTime != null) put("maghribTime", record.maghribTime) else put("maghribTime", JSONObject.NULL)
                    put("isha", record.isha)
                    if (record.ishaTime != null) put("ishaTime", record.ishaTime) else put("ishaTime", JSONObject.NULL)

                    // Sunnah prayers
                    put("tahajjud", record.tahajjud)
                    if (record.tahajjudTime != null) put("tahajjudTime", record.tahajjudTime) else put("tahajjudTime", JSONObject.NULL)
                    put("dhuha", record.dhuha)
                    if (record.dhuhaTime != null) put("dhuhaTime", record.dhuhaTime) else put("dhuhaTime", JSONObject.NULL)
                    put("rawatib", record.rawatib)
                    if (record.rawatibTime != null) put("rawatibTime", record.rawatibTime) else put("rawatibTime", JSONObject.NULL)
                    put("witir", record.witir)
                    if (record.witirTime != null) put("witirTime", record.witirTime) else put("witirTime", JSONObject.NULL)
                }
                array.put(obj)
            }
            put("records", array)

            // Quran Progress
            if (quranProgress != null) {
                val quranObj = JSONObject().apply {
                    put("lastSurahNumber", quranProgress.lastSurahNumber)
                    put("lastAyahNumber", quranProgress.lastAyahNumber)
                    put("lastPageNumber", quranProgress.lastPageNumber)
                    put("lastJuzNumber", quranProgress.lastJuzNumber)
                    put("lastUpdatedTimestamp", quranProgress.lastUpdatedTimestamp)
                    put("targetKhatamDays", quranProgress.targetKhatamDays)
                    put("targetStartDate", quranProgress.targetStartDate)
                    put("completedKhatamCount", quranProgress.completedKhatamCount)
                }
                put("quranProgress", quranObj)
            }

            // Quran Daily Logs
            if (quranDailyLogs.isNotEmpty()) {
                val logsArray = JSONArray()
                quranDailyLogs.forEach { log ->
                    val logObj = JSONObject().apply {
                        put("date", log.date)
                        put("pagesRead", log.pagesRead)
                        put("lastPage", log.lastPage)
                        put("timestamp", log.timestamp)
                    }
                    logsArray.put(logObj)
                }
                put("quranDailyLogs", logsArray)
            }
        }
        return root.toString(2)
    }

    /**
     * Parses a JSON string and returns a ShalahBackupData object.
     * Backward-compatible with Version 1 schema.
     * Throws IllegalArgumentException if the file is invalid.
     */
    fun importFromJson(jsonString: String): ShalahBackupData {
        val root = JSONObject(jsonString)
        val app = root.optString("app", "")
        if (app != APP_IDENTIFIER && !root.has("records")) {
            throw IllegalArgumentException("Format file tidak valid. Pastikan file adalah cadangan resmi Shalah.")
        }

        val array = root.getJSONArray("records")
        val records = mutableListOf<PrayerRecord>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val date = obj.getString("date")

            val record = PrayerRecord(
                date = date,
                fajr = obj.optBoolean("fajr", false),
                fajrTime = if (obj.isNull("fajrTime")) null else obj.optLong("fajrTime"),
                dhuhr = obj.optBoolean("dhuhr", false),
                dhuhrTime = if (obj.isNull("dhuhrTime")) null else obj.optLong("dhuhrTime"),
                asr = obj.optBoolean("asr", false),
                asrTime = if (obj.isNull("asrTime")) null else obj.optLong("asrTime"),
                maghrib = obj.optBoolean("maghrib", false),
                maghribTime = if (obj.isNull("maghribTime")) null else obj.optLong("maghribTime"),
                isha = obj.optBoolean("isha", false),
                ishaTime = if (obj.isNull("ishaTime")) null else obj.optLong("ishaTime"),
                tahajjud = obj.optBoolean("tahajjud", false),
                tahajjudTime = if (obj.isNull("tahajjudTime")) null else obj.optLong("tahajjudTime"),
                dhuha = obj.optBoolean("dhuha", false),
                dhuhaTime = if (obj.isNull("dhuhaTime")) null else obj.optLong("dhuhaTime"),
                rawatib = obj.optBoolean("rawatib", false),
                rawatibTime = if (obj.isNull("rawatibTime")) null else obj.optLong("rawatibTime"),
                witir = obj.optBoolean("witir", false),
                witirTime = if (obj.isNull("witirTime")) null else obj.optLong("witirTime")
            )
            records.add(record)
        }

        var quranProgress: QuranProgress? = null
        if (root.has("quranProgress") && !root.isNull("quranProgress")) {
            val qObj = root.getJSONObject("quranProgress")
            quranProgress = QuranProgress(
                id = 1,
                lastSurahNumber = qObj.optInt("lastSurahNumber", 1),
                lastAyahNumber = qObj.optInt("lastAyahNumber", 1),
                lastPageNumber = qObj.optInt("lastPageNumber", 1),
                lastJuzNumber = qObj.optInt("lastJuzNumber", 1),
                lastUpdatedTimestamp = qObj.optLong("lastUpdatedTimestamp", System.currentTimeMillis()),
                targetKhatamDays = qObj.optInt("targetKhatamDays", 30),
                targetStartDate = qObj.optString("targetStartDate", ""),
                completedKhatamCount = qObj.optInt("completedKhatamCount", 0)
            )
        }

        val quranDailyLogs = mutableListOf<QuranDailyLog>()
        if (root.has("quranDailyLogs") && !root.isNull("quranDailyLogs")) {
            val logsArray = root.getJSONArray("quranDailyLogs")
            for (i in 0 until logsArray.length()) {
                val lObj = logsArray.getJSONObject(i)
                val log = QuranDailyLog(
                    date = lObj.getString("date"),
                    pagesRead = lObj.optInt("pagesRead", 0),
                    lastPage = lObj.optInt("lastPage", 1),
                    timestamp = lObj.optLong("timestamp", System.currentTimeMillis())
                )
                quranDailyLogs.add(log)
            }
        }

        return ShalahBackupData(
            records = records,
            quranProgress = quranProgress,
            quranDailyLogs = quranDailyLogs
        )
    }

    /**
     * Writes text to an Android SAF Uri.
     */
    fun writeToUri(context: Context, uri: Uri, content: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { os ->
                OutputStreamWriter(os).use { writer ->
                    writer.write(content)
                    writer.flush()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Reads text from an Android SAF Uri.
     */
    fun readFromUri(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
