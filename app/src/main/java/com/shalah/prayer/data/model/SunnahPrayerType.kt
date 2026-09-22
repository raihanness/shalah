package com.shalah.prayer.data.model

import com.shalah.prayer.R

enum class SunnahPrayerType(
    val id: String,
    val displayName: String,
    val indonesianName: String,
    val arabicName: String,
    val description: String,
    val iconResId: Int
) {
    TAHAJJUD(
        id = "tahajjud",
        displayName = "Tahajjud",
        indonesianName = "Tahajjud",
        arabicName = "تهجد",
        description = "Shalat sunnah malam hari / sepertiga malam",
        iconResId = R.drawable.ic_prayer_tahajjud
    ),
    DHUHA(
        id = "dhuha",
        displayName = "Dhuha",
        indonesianName = "Dhuha",
        arabicName = "ضحى",
        description = "Pagi hari setelah matahari terbit naik",
        iconResId = R.drawable.ic_prayer_dhuha
    ),
    RAWATIB(
        id = "rawatib",
        displayName = "Rawatib",
        indonesianName = "Rawatib",
        arabicName = "رواتب",
        description = "Sunnah pengiring fardhu (Qabliyah & Ba'diyah)",
        iconResId = R.drawable.ic_prayer_rawatib
    ),
    WITIR(
        id = "witir",
        displayName = "Witir",
        indonesianName = "Witir",
        arabicName = "وتر",
        description = "Penutup shalat malam rakaat ganjil",
        iconResId = R.drawable.ic_prayer_witir
    );

    companion object {
        fun fromId(id: String): SunnahPrayerType? {
            return entries.find { it.id.equals(id, ignoreCase = true) }
        }
    }
}
