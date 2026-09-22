# 🕌 Shalah - Offline Islamic Prayer Tracker with Interactive Home Widget

A lightweight, 100% offline, privacy-first Android prayer tracker built natively with **Kotlin**, **Jetpack Compose**, **Room SQLite**, and an **Interactive Home Screen Widget**.

---

## ✨ Features

- **100% Offline & Private:** Zero internet permissions required (`android.permission.INTERNET` is not even declared). All your prayer records are safely stored locally on your device in SQLite via Room.
- **⚡ Interactive Home Screen Widget:**
  - Placed right on your Android Home Screen.
  - Displays today's 5 prayers (Fajr, Dhuhr, Asr, Maghrib, Isha) with visual indicators.
  - **1-Tap Instant Logging:** Tap any prayer directly from your Home Screen to mark it done. The widget toggles the SQLite database in the background and updates its appearance instantly without needing to open the app!
  - Tap the widget header to open the full app.
- **Modern Jetpack Compose UI:**
  - Built with Material 3 and custom Islamic Emerald & Gold dark styling.
  - Daily completion circular progress card with motivational Hadith.
  - Interactive prayer cards with time stamps (e.g., "Done at 5:12 AM").
  - 7-Day history strip with visual daily consistency dots.
  - Perfect prayer days counter.

---

## 🏗️ Architecture & Tech Stack

- **Platform:** Android (Min SDK 26 / Android 8.0, Target SDK 34 / Android 14)
- **Language:** Kotlin 1.9.22
- **UI Framework:** Jetpack Compose + Material 3
- **Local Persistence:** Android Room 2.6.1 (SQLite) with reactive `StateFlow`
- **Widget:** `AppWidgetProvider` + `RemoteViews` + background `BroadcastReceiver`
- **Architecture Pattern:** Clean MVVM (ViewModel + Repository + Room DAO)

---

## 🚀 How to Build and Run

### 1. Build the Debug APK
Open a terminal in the project directory and run:
```powershell
.\gradlew.bat assembleDebug
```
The resulting APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 2. Install on Device or Emulator
Connect your Android device with USB debugging enabled, or start an Android emulator, then run:
```powershell
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📱 How to Add the Home Screen Widget

1. Return to your Android device's Home Screen.
2. Long-press on an empty space on the home screen.
3. Tap **Widgets**.
4. Scroll to **Shalah** and drag the **Prayer Tracker** widget onto your home screen.
5. Resize it to your preferred width (works great at 4x2 or 4x1).
6. Tap any prayer (Fajr, Dhuhr, Asr, Maghrib, Isha) on the widget to toggle its completed state immediately!
