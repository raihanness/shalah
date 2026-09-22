package com.shalah.prayer.data.model

enum class PrayerType(
    val id: String,
    val displayName: String,
    val indonesianName: String,
    val arabicName: String,
    val description: String
) {
    FAJR(
        id = "fajr",
        displayName = "Fajr",
        indonesianName = "Subuh",
        arabicName = "الفجر",
        description = "Fajar menjelang pagi"
    ),
    DHUHR(
        id = "dhuhr",
        displayName = "Dhuhr",
        indonesianName = "Dzuhur",
        arabicName = "الظهر",
        description = "Tengah hari setelah matahari tergelincir"
    ),
    ASR(
        id = "asr",
        displayName = "Asr",
        indonesianName = "Ashar",
        arabicName = "العصر",
        description = "Sore hari menjelang petang"
    ),
    MAGHRIB(
        id = "maghrib",
        displayName = "Maghrib",
        indonesianName = "Maghrib",
        arabicName = "المغرب",
        description = "Saat matahari terbenam"
    ),
    ISHA(
        id = "isha",
        displayName = "Isha",
        indonesianName = "Isya",
        arabicName = "العشاء",
        description = "Malam hari setelah mega merah hilang"
    );

    companion object {
        fun fromId(id: String): PrayerType? {
            return entries.find { it.id.equals(id, ignoreCase = true) }
        }
    }
}
