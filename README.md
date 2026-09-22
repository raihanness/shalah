# Shalah: Offline Islamic Prayer Tracker & Quran Companion

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android_8.0+_(API_26+)-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Room Database](https://img.shields.io/badge/Room_SQLite-2.6.1-FFCA28?style=for-the-badge&logo=sqlite&logoColor=black)
![Privacy First](https://img.shields.io/badge/Privacy-100%25_Offline_(Zero_Internet)-00E676?style=for-the-badge&logo=shield&logoColor=black)

<br/>

**A modern, private, and beautifully crafted native Android application for tracking daily prayers, Sunnah worship, Quran Khatam progress, and finding the Qibla. Built with zero internet permissions, reactive Jetpack Compose, and instant Home Screen Widgets.**

[Features](#-key-features) • [Architecture](#-architecture--tech-stack) • [Directory Structure](#-project-structure) • [Algorithms](#-astronomical--navigation-algorithms) • [Building & Running](#-how-to-build-and-run) • [Widget Guide](#-home-screen-widgets) • [Backup Schema](#-data-backup--portability)

</div>

---

## Overview

**Shalah** is engineered from the ground up to be an intimate, distraction-free spiritual companion. Shalah operates **100% offline**:
- **Zero Internet Permissions:** `android.permission.INTERNET` is completely absent from `AndroidManifest.xml`.
- **Complete Data Sovereignty:** All prayer records, Quran bookmarks, daily reading logs, and settings live securely in your device's local SQLite database via Android Room.
- **Zero-Latency Logging:** 1-tap toggling directly from your Android Home Screen with native widgets without ever opening the app.

---

## Key Features

### 1. The Five Core Navigation Pillars

#### Tab 1: Prayer Tracker (Pusat Ibadah)
- **Celestial Arc Hero Card:** Dynamic visual illustration representing the sun's position across dawn, zenith, twilight, and night, highlighting the currently active prayer window.
- **Interactive 7-Day Week Strip:** Quick day switcher with visual completion status dots, streak indicators, and seamless historical date navigation.
- **Fardhu Prayer Cards:** Instant 1-tap completion toggles for **Fajr (Subuh)**, **Dhuhr (Dzuhur)**, **Asr (Ashar)**, **Maghrib**, and **Isha (Isya)**. Displays timestamps (e.g. *"Selesai pukul 05:12 WIB"*) with full custom time editing support.
- **Sunnah Prayer Suite:** Collapsible dedicated tracker for voluntary prayers:
  - **Tahajjud** (Sepertiga Malam)
  - **Dhuha** (Pagi Hari)
  - **Rawatib** (Qabliyah & Ba'diyah)
  - **Witir** (Penutup Shalat Malam)
- **Motivational Wisdom & Hadith:** Contextual Islamic reminders and daily encouragement tailored to your completion progress.
- **Streak & Perfect Day Counter:** Real-time metrics celebrating full 5/5 daily fardhu completions and continuous daily streaks.

#### Tab 2: Quran & Khatam Companion
- **Khatam Progress Ring:** High-contrast radial gauge tracking your journey through the standard 604-page Madani Mushaf.
- **Last Read Bookmark:** Live tracking of your current Surah, Ayah, Page, and Juz with a 1-tap quick edit bottom sheet.
- **1-Tap Quick Pages Log:** Log reading sessions with `+1`, `+2`, `+5`, or `+10` page increment shortcuts.
- **Surah & Juz Directory:** Complete offline catalog of all **114 Surahs** and **30 Juz** with English translations, Arabic typography, Ayah counts, and Makkiyah/Madaniyah classifications.
- **Adaptive Khatam Pacing:** Set custom completion targets (e.g., 30-day Khatam plan) with automated daily pace calculations and per-prayer targets (e.g., 4 pages after each fardhu prayer).
- **Daily Quran Reading Streaks:** Dedicated consistency tracker and lifetime Khatam milestone counter.

#### Tab 3: Offline Qibla Compass (Arah Kiblat)
- **Mathematical Great-Circle Direction:** Computes the exact bearing towards the Holy Kaaba in Mecca (`21.4225° N, 39.8262° E`) relative to your current location.
- **Hardware Sensor Fusion:** Smooth low-pass filtered orientation combining the device's 3-axis magnetometer and accelerometer.
- **Tactile Alignment & Haptics:** Subtle vibration trigger and visual glow when the compass aligns within ±3° of the Qibla.
- **3D Kaaba Badge:** High-precision compass dial with real-time azimuth and heading degrees.

#### Tab 4: Kalender & Prayer Trends
- **Monthly Consistency Heatmap:** Color-graded calendar matrix visualizing daily prayer completion intensity across any chosen month.
- **Prayer Trends Matrix:** Comprehensive breakdown of completion percentages for each individual prayer type across all historical data.
- **Time Capsule Date Navigation:** Select any date in history to inspect or record past worship logs.

#### Tab 5: Settings, Privacy & Customization
- **Discreet Prayer Alarms & Notifications:** Multi-interval alerts (30 minutes before, 10 minutes before, and at prayer time) powered by `AlarmManager` with automatic reboot resilience (`BootReceiver`).
- **50+ Preset Indonesian Cities:** Calibrated coordinates covering all Indonesian timezones (WIB, WITA, WIT) plus custom latitude/longitude inputs.
- **Theme Customization:** Seamlessly switch between **Dark Emerald**, **Warm Canvas Light**, and **System Default**.
- **100% Offline JSON Backup & Restore:** Export full history to a clean JSON document via Android Storage Access Framework (SAF) and restore it anytime.

---

### 📱 2. Interactive Home Screen Widgets

Shalah includes two high-performance, battery-efficient Android AppWidgets built with `AppWidgetProvider` and `RemoteViews`:

| Widget Type | Size | Features |
| :--- | :---: | :--- |
| **Full Prayer Card** | `4x2` | Displays today's date, completion score (e.g. `5/5`), individual prayer buttons with check badges, and calculated prayer schedules. Tapping any prayer button immediately updates the local database in the background without opening the app. |
| **Compact Slim Bar** | `4x1` | Ultra-minimalist single-row design showing all 5 prayers, checkmarks, and timestamps in a space-efficient form factor. |

---

## Architecture & Tech Stack

Shalah follows modern Android Clean Architecture and the **Unidirectional Data Flow (UDF)** reactive MVVM pattern:

```
┌────────────────────────────────────────────────────────┐
│               Jetpack Compose UI Layer                 │
│   (HomeScreen, QuranTabContent, Widgets, Dialogs)     │
└───────────────────────────▲────────────────────────────┘
                            │ StateFlow / Events
┌───────────────────────────┴────────────────────────────┐
│                    PrayerViewModel                     │
│    (Business Logic, Schedules, Streaks, State Flow)   │
└───────────────────────────▲────────────────────────────┘
                            │ Coroutines
┌───────────────────────────┴────────────────────────────┐
│                   PrayerRepository                     │
│  (Data aggregation, calculation bridges, backups)      │
└─────────────▲──────────────────────────────▲───────────┘
              │                              │
┌─────────────┴─────────────┐  ┌─────────────┴───────────┐
│     AppDatabase (Room)    │  │  PrayerTimeCalculator   │
│  - PrayerDao              │  │  - Solar Algorithm     │
│  - QuranDao               │  │  - 50+ City Presets     │
└───────────────────────────┘  └─────────────────────────┘
```

### Core Technologies
- **Language:** [Kotlin 1.9.22](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose (BOM 2024.02.02)](https://developer.android.com/jetpack/compose) + [Material 3](https://m3.material.io/)
- **Persistence:** [Android Room 2.6.1](https://developer.android.com/training/data-storage/room) (SQLite) with [Google KSP](https://github.com/google/ksp)
- **Asynchronous & Reactive:** Kotlin Coroutines 1.7.3 & `StateFlow` / `SharedFlow`
- **Alarm Scheduling:** Android `AlarmManager` with exact idle wakeups (`SCHEDULE_EXACT_ALARM`)
- **Sensors:** Android Sensor Framework (Accelerometer + Magnetic Field Vector)
- **Target OS:** Min SDK 26 (Android 8.0 Oreo) • Target SDK 34 (Android 14)

---

## Astronomical & Navigation Algorithms

### 1. Offline Solar Prayer Calculation
Calculated directly on-device in pure Kotlin using standard astronomical ephemeris calibrated to the **Indonesian Ministry of Religious Affairs (Kemenag)** standards:
- **Fajr (Subuh):** Solar depression angle $\alpha = 20.0^\circ$
- **Isha (Isya):** Solar depression angle $\alpha = 18.0^\circ$
- **Asr (Ashar):** Shafi'i / Maliki / Hanbali jurisprudence (Shadow length ratio $m = 1$)
- **Dhuhr (Dzuhur):** Solar noon with standard safety margin ($+2\text{ minutes}$)
- **Maghrib:** Sunset with atmospheric refraction correction

### 2. Great-Circle Qibla Calculation
The compass calculates the Great-Circle bearing towards Mecca ($21.4225^\circ\text{ N}, 39.8262^\circ\text{ E}$) from device coordinates $(\phi, \lambda)$:

$$\theta = \text{atan2}\Big(\sin(\Delta\lambda)\cos(\phi_K), \; \cos(\phi)\sin(\phi_K) - \sin(\phi)\cos(\phi_K)\cos(\Delta\lambda)\Big)$$

Where:
- $\phi, \lambda$ = User's latitude and longitude in radians
- $\phi_K, \lambda_K$ = Kaaba latitude ($21.4225^\circ$) and longitude ($39.8262^\circ$) in radians
- $\Delta\lambda = \lambda_K - \lambda$

---

## How to Build and Run

### Prerequisites
- **JDK 17** or higher
- **Android SDK** (API level 26 to 34)
- **Gradle 8.3+** (bundled via `gradlew`)

### 1. Build the Debug APK
Open a terminal in the root project folder:

```powershell
# Windows (PowerShell / Command Prompt)
.\gradlew.bat assembleDebug

# Linux / macOS
./gradlew assembleDebug
```

The compiled APK will be available at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 2. Install on Device or Emulator
Ensure USB debugging is enabled on your Android phone, then run:

```powershell
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Home Screen Widgets

<div align="center">

### Adding the Widget to Your Home Screen
1. Long-press any empty space on your Android Home Screen.
2. Tap **Widgets** in the popup menu.
3. Scroll down and locate **Shalah**.
4. Drag either the **Prayer Tracker (4x2)** or **Compact Prayer Bar (4x1)** to your desired screen position.
5. Tap any prayer directly from the widget to toggle completion instantly!

</div>

---

## Data Backup & Portability

Shalah provides a 100% offline, human-readable JSON backup mechanism. You can export and import your complete history at any time without network connectivity.

### Backup JSON Schema (Version 2)
```json
{
  "app": "Shalah",
  "version": 2,
  "exportedAt": 1726998000000,
  "recordsCount": 365,
  "records": [
    {
      "date": "2026-09-22",
      "fajr": true,
      "fajrTime": 1726956720000,
      "dhuhr": true,
      "dhuhrTime": 1726982400000,
      "asr": false,
      "asrTime": null,
      "maghrib": false,
      "maghribTime": null,
      "isha": false,
      "ishaTime": null,
      "tahajjud": true,
      "tahajjudTime": 1726951200000,
      "dhuha": false,
      "dhuhaTime": null,
      "rawatib": true,
      "rawatibTime": 1726982700000,
      "witir": false,
      "witirTime": null
    }
  ],
  "quranProgress": {
    "lastSurahNumber": 2,
    "lastAyahNumber": 255,
    "lastPageNumber": 42,
    "lastJuzNumber": 3,
    "lastUpdatedTimestamp": 1726998000000,
    "targetKhatamDays": 30,
    "targetStartDate": "2026-09-01",
    "completedKhatamCount": 1
  },
  "quranDailyLogs": [
    {
      "date": "2026-09-22",
      "pagesRead": 4,
      "lastPage": 42,
      "timestamp": 1726998000000
    }
  ]
}
```

---

## Privacy & Permissions

| Permission | Reason |
| :--- | :--- |
| `POST_NOTIFICATIONS` | Deliver optional 30m / 10m / at-prayer-time notifications. |
| `SCHEDULE_EXACT_ALARM` | Ensure exact, timely delivery of prayer reminders during device idle states. |
| `RECEIVE_BOOT_COMPLETED` | Automatically reschedule prayer alarms and update widgets after device restart. |
| `VIBRATE` | Gentle haptic feedback on Qibla compass alignment and button presses. |
| **`INTERNET`** | ❌ **NOT REQUESTED / NOT USED** (100% offline). |

---
