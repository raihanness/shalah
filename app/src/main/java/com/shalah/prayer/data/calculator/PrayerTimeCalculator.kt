package com.shalah.prayer.data.calculator

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.*

/**
 * 100% Offline astronomical prayer times calculator based on the standard solar algorithm.
 * Default calibration matches Kemenag (Indonesian Ministry of Religious Affairs):
 * - Fajr angle: 20.0 degrees
 * - Isha angle: 18.0 degrees
 * - Asr: Shafi'i (shadow ratio = 1)
 */
object PrayerTimeCalculator {

    data class PrayerSchedule(
        val fajr: LocalTime,
        val sunrise: LocalTime,
        val dhuhr: LocalTime,
        val asr: LocalTime,
        val maghrib: LocalTime,
        val isha: LocalTime
    )

    // Preset coordinates for major Indonesian cities (Defaults to Jakarta)
    data class CityCoordinate(
        val name: String,
        val latitude: Double,
        val longitude: Double
    )

    val DEFAULT_CITY = CityCoordinate(
        name = "Jakarta / WIB",
        latitude = -6.2088,
        longitude = 106.8456
    )

    val CITIES = listOf(
        DEFAULT_CITY,
        // Jabodetabek & Jawa Barat
        CityCoordinate("Bogor / WIB", -6.5971, 106.8060),
        CityCoordinate("Depok / WIB", -6.4025, 106.7942),
        CityCoordinate("Tangerang / WIB", -6.1783, 106.6319),
        CityCoordinate("Tangerang Selatan / WIB", -6.2888, 106.7179),
        CityCoordinate("Bekasi / WIB", -6.2383, 106.9756),
        CityCoordinate("Bandung / WIB", -6.9175, 107.6191),
        CityCoordinate("Cirebon / WIB", -6.7320, 108.5523),
        CityCoordinate("Sukabumi / WIB", -6.9277, 106.9300),
        CityCoordinate("Tasikmalaya / WIB", -7.3274, 108.2207),
        CityCoordinate("Garut / WIB", -7.2278, 107.9086),
        CityCoordinate("Purwakarta / WIB", -6.5569, 107.4433),
        CityCoordinate("Karawang / WIB", -6.3072, 107.3072),

        // Jawa Tengah & D.I. Yogyakarta
        CityCoordinate("Semarang / WIB", -7.0051, 110.4381),
        CityCoordinate("Surakarta (Solo) / WIB", -7.5755, 110.8243),
        CityCoordinate("Yogyakarta / WIB", -7.7956, 110.3695),
        CityCoordinate("Magelang / WIB", -7.4797, 110.2177),
        CityCoordinate("Purwokerto / WIB", -7.4243, 109.2302),
        CityCoordinate("Tegal / WIB", -6.8694, 109.1402),
        CityCoordinate("Pekalongan / WIB", -6.8886, 109.6753),
        CityCoordinate("Kudus / WIB", -6.8052, 110.8405),
        CityCoordinate("Salatiga / WIB", -7.3305, 110.5084),
        CityCoordinate("Cilacap / WIB", -7.7167, 109.0167),

        // Jawa Timur
        CityCoordinate("Surabaya / WIB", -7.2575, 112.7521),
        CityCoordinate("Malang / WIB", -7.9666, 112.6326),
        CityCoordinate("Sidoarjo / WIB", -7.4478, 112.7183),
        CityCoordinate("Gresik / WIB", -7.1566, 112.6555),
        CityCoordinate("Kediri / WIB", -7.8480, 112.0178),
        CityCoordinate("Madiun / WIB", -7.6298, 111.5239),
        CityCoordinate("Jember / WIB", -8.1724, 113.7007),
        CityCoordinate("Banyuwangi / WIB", -8.2192, 114.3692),
        CityCoordinate("Probolinggo / WIB", -7.7543, 113.2159),
        CityCoordinate("Pasuruan / WIB", -7.6453, 112.9075),
        CityCoordinate("Blitar / WIB", -8.0983, 112.1681),
        CityCoordinate("Batu / WIB", -7.8712, 112.5271),

        // Sumatera
        CityCoordinate("Banda Aceh / WIB", 5.5483, 95.3238),
        CityCoordinate("Medan / WIB", 3.5952, 98.6722),
        CityCoordinate("Pematangsiantar / WIB", 2.9590, 99.0687),
        CityCoordinate("Binjai / WIB", 3.6006, 98.4854),
        CityCoordinate("Padang / WIB", -0.9471, 100.4172),
        CityCoordinate("Bukittinggi / WIB", -0.3056, 100.3692),
        CityCoordinate("Pekanbaru / WIB", 0.5071, 101.4478),
        CityCoordinate("Dumai / WIB", 1.6666, 101.4500),
        CityCoordinate("Batam / WIB", 1.1301, 104.0529),
        CityCoordinate("Tanjungpinang / WIB", 0.9167, 104.4500),
        CityCoordinate("Jambi / WIB", -1.6101, 103.6131),
        CityCoordinate("Palembang / WIB", -2.9761, 104.7754),
        CityCoordinate("Lubuklinggau / WIB", -3.2942, 102.8617),
        CityCoordinate("Bengkulu / WIB", -3.8004, 102.2655),
        CityCoordinate("Bandar Lampung / WIB", -5.4500, 105.2667),
        CityCoordinate("Metro / WIB", -5.1136, 105.3067),
        CityCoordinate("Pangkalpinang / WIB", -2.1333, 106.1167),
        CityCoordinate("Tanjung Pandan (Belitung) / WIB", -2.7333, 107.6333),

        // Kalimantan
        CityCoordinate("Pontianak / WIB", -0.0263, 109.3425),
        CityCoordinate("Singkawang / WIB", 0.9073, 108.9863),
        CityCoordinate("Palangkaraya / WIB", -2.2161, 113.9167),
        CityCoordinate("Banjarmasin / WITA", -3.3194, 114.5908),
        CityCoordinate("Banjarbaru / WITA", -3.4402, 114.8304),
        CityCoordinate("Balikpapan / WITA", -1.2379, 116.8529),
        CityCoordinate("Samarinda / WITA", -0.5022, 117.1536),
        CityCoordinate("Bontang / WITA", 0.1333, 117.5000),
        CityCoordinate("Tarakan / WITA", 3.3274, 117.5785),

        // Bali & Nusa Tenggara
        CityCoordinate("Denpasar / WITA", -8.6705, 115.2126),
        CityCoordinate("Singaraja / WITA", -8.1120, 115.0882),
        CityCoordinate("Mataram (Lombok) / WITA", -8.5833, 116.1167),
        CityCoordinate("Bima / WITA", -8.4608, 118.7267),
        CityCoordinate("Kupang / WITA", -10.1772, 123.6070),
        CityCoordinate("Labuan Bajo / WITA", -8.4964, 119.8877),

        // Sulawesi
        CityCoordinate("Makassar / WITA", -5.1477, 119.4327),
        CityCoordinate("Parepare / WITA", -4.0131, 119.6253),
        CityCoordinate("Palopo / WITA", -2.9926, 120.1969),
        CityCoordinate("Manado / WITA", 1.4748, 124.8421),
        CityCoordinate("Bitung / WITA", 1.4404, 125.1824),
        CityCoordinate("Palu / WITA", -0.9003, 119.8779),
        CityCoordinate("Kendari / WITA", -3.9985, 122.5126),
        CityCoordinate("Gorontalo / WITA", 0.5435, 123.0568),
        CityCoordinate("Mamuju / WITA", -2.6775, 118.8894),

        // Maluku & Papua
        CityCoordinate("Ambon / WIT", -3.6954, 128.1814),
        CityCoordinate("Tual / WIT", -5.6297, 132.7489),
        CityCoordinate("Ternate / WIT", 0.7906, 127.3842),
        CityCoordinate("Tidore / WIT", 0.6908, 127.4042),
        CityCoordinate("Jayapura / WIT", -2.5916, 140.6690),
        CityCoordinate("Sorong / WIT", -0.8762, 131.2558),
        CityCoordinate("Manokwari / WIT", -0.8615, 134.0620),
        CityCoordinate("Merauke / WIT", -8.4991, 140.4014),
        CityCoordinate("Timika / WIT", -4.5467, 136.8837)
    )

    fun calculate(
        date: LocalDate,
        latitude: Double = DEFAULT_CITY.latitude,
        longitude: Double = DEFAULT_CITY.longitude,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): PrayerSchedule {
        val timeZoneOffset = zoneId.rules.getOffset(date.atStartOfDay(zoneId).toInstant()).totalSeconds / 3600.0

        val julianDay = toJulianDay(date.year, date.monthValue, date.dayOfMonth) - longitude / (15.0 * 24.0)

        val d = julianDay - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))

        val e = 23.439 - 0.00000036 * d
        val ra = fixAngle(Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l))))) / 15.0
        val dhuhrTransit = 12.0 + timeZoneOffset - longitude / 15.0 - (q / 15.0 - ra)

        val declination = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))

        // Standard 1-minute safety buffer (ihtiyat) commonly used by Indonesian prayer engines (e.g. Pillars / Kemenag)
        val ihtiyatBuffer = 1.0 / 60.0

        // Fajr (20 degrees below horizon)
        val fajrDiff = hourAngle(-20.0, latitude, declination)
        val fajrHours = dhuhrTransit - fajrDiff + ihtiyatBuffer

        // Sunrise (0.833 degrees below horizon for refraction)
        val sunriseDiff = hourAngle(-0.833, latitude, declination)
        val sunriseHours = dhuhrTransit - sunriseDiff

        // Asr (Shafi'i shadow = 1, sun altitude is positive above the horizon)
        val asrAngle = Math.toDegrees(atan(1.0 / (1.0 + tan(Math.toRadians(abs(latitude - declination))))))
        val asrDiff = hourAngle(asrAngle, latitude, declination)
        val asrHours = dhuhrTransit + asrDiff + ihtiyatBuffer

        // Maghrib (Sunset 0.833 degrees below horizon)
        val maghribHours = dhuhrTransit + sunriseDiff + ihtiyatBuffer

        // Isha (18 degrees below horizon)
        val ishaDiff = hourAngle(-18.0, latitude, declination)
        val ishaHours = dhuhrTransit + ishaDiff + ihtiyatBuffer

        return PrayerSchedule(
            fajr = hoursToLocalTime(fajrHours),
            sunrise = hoursToLocalTime(sunriseHours),
            dhuhr = hoursToLocalTime(dhuhrTransit + ihtiyatBuffer),
            asr = hoursToLocalTime(asrHours),
            maghrib = hoursToLocalTime(maghribHours),
            isha = hoursToLocalTime(ishaHours)
        )
    }

    private fun hourAngle(altitude: Double, latitude: Double, declination: Double): Double {
        val cosHourAngle = (sin(Math.toRadians(altitude)) - sin(Math.toRadians(latitude)) * sin(Math.toRadians(declination))) /
                (cos(Math.toRadians(latitude)) * cos(Math.toRadians(declination)))
        val clamped = cosHourAngle.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clamped)) / 15.0
    }

    private fun fixAngle(angle: Double): Double {
        var a = angle - 360.0 * floor(angle / 360.0)
        if (a < 0) a += 360.0
        return a
    }

    private fun toJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun hoursToLocalTime(hoursDecimal: Double): LocalTime {
        var h = hoursDecimal
        while (h < 0) h += 24.0
        while (h >= 24) h -= 24.0

        val hour = h.toInt()
        val minute = ((h - hour) * 60.0).roundToInt()
        return if (minute >= 60) {
            LocalTime.of((hour + 1) % 24, 0)
        } else {
            LocalTime.of(hour, minute)
        }
    }

    /**
     * Calculates the Great Circle initial forward bearing towards the Kaaba (Mecca).
     * Kaaba coordinates: 21.422487° N, 39.826206° E.
     * Returns azimuth in degrees [0..360).
     */
    fun calculateQiblaBearing(latitude: Double, longitude: Double): Double {
        val phi1 = Math.toRadians(latitude)
        val phi2 = Math.toRadians(21.422487)
        val deltaLambda = Math.toRadians(39.826206 - longitude)

        val y = sin(deltaLambda)
        val x = cos(phi1) * tan(phi2) - sin(phi1) * cos(deltaLambda)

        val bearingRad = atan2(y, x)
        var bearingDeg = Math.toDegrees(bearingRad)
        bearingDeg = (bearingDeg + 360.0) % 360.0
        return bearingDeg
    }
}
