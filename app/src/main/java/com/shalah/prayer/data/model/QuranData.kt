package com.shalah.prayer.data.model

data class Surah(
    val number: Int,
    val nameLatin: String,
    val nameArabic: String,
    val translationId: String,
    val totalAyah: Int,
    val startPage: Int,
    val juz: Int,
    val isMakkiyyah: Boolean
)

data class JuzInfo(
    val number: Int,
    val name: String,
    val startSurahNumber: Int,
    val startPage: Int,
    val endPage: Int
)

object QuranData {
    val SURAHS: List<Surah> = listOf(
        Surah(1, "Al-Fatihah", "الفاتحة", "Pembukaan", 7, 1, 1, true),
        Surah(2, "Al-Baqarah", "البقرة", "Sapi Betina", 286, 2, 1, false),
        Surah(3, "Ali 'Imran", "آل عمران", "Keluarga Imran", 200, 50, 3, false),
        Surah(4, "An-Nisa'", "النساء", "Wanita", 176, 77, 4, false),
        Surah(5, "Al-Ma'idah", "المائدة", "Jamuan Hidangan", 120, 106, 6, false),
        Surah(6, "Al-An'am", "الأنعام", "Hewan Ternak", 165, 128, 7, true),
        Surah(7, "Al-A'raf", "الأعراف", "Tempat Tertinggi", 206, 151, 8, true),
        Surah(8, "Al-Anfal", "الأنفال", "Harta Rampasan Perang", 75, 177, 9, false),
        Surah(9, "At-Taubah", "التوبة", "Pengampunan", 129, 187, 10, false),
        Surah(10, "Yunus", "يونس", "Nabi Yunus", 109, 208, 11, true),
        Surah(11, "Hud", "هود", "Nabi Hud", 123, 221, 11, true),
        Surah(12, "Yusuf", "يوسف", "Nabi Yusuf", 111, 235, 12, true),
        Surah(13, "Ar-Ra'd", "الرعد", "Guruh", 43, 249, 13, false),
        Surah(14, "Ibrahim", "إبراهيم", "Nabi Ibrahim", 52, 255, 13, true),
        Surah(15, "Al-Hijr", "الحجر", "Bukit Hijr", 99, 262, 14, true),
        Surah(16, "An-Nahl", "النحل", "Lebah", 128, 267, 14, true),
        Surah(17, "Al-Isra'", "الإسراء", "Perjalanan Malam", 111, 282, 15, true),
        Surah(18, "Al-Kahf", "الكهف", "Gua", 110, 293, 15, true),
        Surah(19, "Maryam", "مريم", "Siti Maryam", 98, 305, 16, true),
        Surah(20, "Thaha", "طه", "Thaha", 135, 312, 16, true),
        Surah(21, "Al-Anbiya'", "الأنبياء", "Para Nabi", 112, 322, 17, true),
        Surah(22, "Al-Hajj", "الحج", "Haji", 78, 332, 17, false),
        Surah(23, "Al-Mu'minun", "المؤمنون", "Orang-Orang Mukmin", 118, 342, 18, true),
        Surah(24, "An-Nur", "النور", "Cahaya", 64, 350, 18, false),
        Surah(25, "Al-Furqan", "الفرقان", "Pembeda", 77, 359, 18, true),
        Surah(26, "Asy-Syu'ara'", "الشعراء", "Para Penyair", 227, 367, 19, true),
        Surah(27, "An-Naml", "النمل", "Semut", 93, 377, 19, true),
        Surah(28, "Al-Qashas", "القصص", "Kisah-Kisah", 88, 385, 20, true),
        Surah(29, "Al-'Ankabut", "العنكبوت", "Laba-Laba", 69, 396, 20, true),
        Surah(30, "Ar-Rum", "الروم", "Bangsa Romawi", 60, 404, 21, true),
        Surah(31, "Luqman", "لقمان", "Keluarga Luqman", 34, 411, 21, true),
        Surah(32, "As-Sajdah", "السجدة", "Sujud", 30, 415, 21, true),
        Surah(33, "Al-Ahzab", "الأحزاب", "Golongan Yang Bersekutu", 73, 418, 21, false),
        Surah(34, "Saba'", "سبأ", "Kaum Saba'", 54, 428, 22, true),
        Surah(35, "Fathir", "فاطر", "Pencipta", 45, 434, 22, true),
        Surah(36, "Yasin", "يس", "Yasin", 83, 440, 22, true),
        Surah(37, "Ash-Shaffat", "الصافات", "Barisan-Barisan", 182, 446, 23, true),
        Surah(38, "Shad", "ص", "Shad", 88, 453, 23, true),
        Surah(39, "Az-Zumar", "الزمر", "Rombongan", 75, 458, 23, true),
        Surah(40, "Ghafir", "غافر", "Maha Pengampun", 85, 467, 24, true),
        Surah(41, "Fushshilat", "فصلت", "Yang Dijelaskan", 54, 477, 24, true),
        Surah(42, "Asy-Syura", "الشورى", "Musyawarah", 53, 483, 25, true),
        Surah(43, "Az-Zukhruf", "الزخرف", "Perhiasan", 89, 489, 25, true),
        Surah(44, "Ad-Dukhan", "الدخان", "Kabut", 59, 496, 25, true),
        Surah(45, "Al-Jatsiyah", "الجاثية", "Yang Berlutut", 37, 499, 25, true),
        Surah(46, "Al-Ahqaf", "الأحقاف", "Bukit-Bukit Pasir", 35, 502, 26, true),
        Surah(47, "Muhammad", "محمد", "Nabi Muhammad", 38, 507, 26, false),
        Surah(48, "Al-Fath", "الفتح", "Kemenangan", 29, 511, 26, false),
        Surah(49, "Al-Hujurat", "الحجرات", "Kamar-Kamar", 18, 515, 26, false),
        Surah(50, "Qaf", "ق", "Qaf", 45, 518, 26, true),
        Surah(51, "Adz-Dzariyat", "الذاريات", "Angin Yang Menerbangkan", 60, 520, 26, true),
        Surah(52, "Ath-Thur", "الطور", "Bukit Thursina", 49, 523, 27, true),
        Surah(53, "An-Najm", "النجم", "Bintang", 62, 526, 27, true),
        Surah(54, "Al-Qamar", "القمر", "Bulan", 55, 528, 27, true),
        Surah(55, "Ar-Rahman", "الرحمن", "Maha Pemurah", 78, 531, 27, false),
        Surah(56, "Al-Waqi'ah", "الواقعة", "Hari Kiamat", 96, 534, 27, true),
        Surah(57, "Al-Hadid", "الحديد", "Besi", 29, 537, 27, false),
        Surah(58, "Al-Mujadilah", "المجادلة", "Wanita Yang Mengajukan Gugatan", 22, 542, 28, false),
        Surah(59, "Al-Hasyr", "الحشر", "Pengusiran", 24, 545, 28, false),
        Surah(60, "Al-Mumtahanah", "الممتحنة", "Wanita Yang Diuji", 13, 549, 28, false),
        Surah(61, "Ash-Shaff", "الصف", "Barisan", 14, 551, 28, false),
        Surah(62, "Al-Jumu'ah", "الجمعة", "Hari Jum'at", 11, 553, 28, false),
        Surah(63, "Al-Munafiqun", "المنافقون", "Orang-Orang Munafik", 11, 554, 28, false),
        Surah(64, "At-Taghabun", "التغابن", "Hari Ditampakkan Kesalahan", 18, 556, 28, false),
        Surah(65, "Ath-Thalaq", "الطلاق", "Perceraian", 12, 558, 28, false),
        Surah(66, "At-Tahrim", "التحريم", "Pengharaman", 12, 560, 28, false),
        Surah(67, "Al-Mulk", "الملك", "Kerajaan", 30, 562, 29, true),
        Surah(68, "Al-Qalam", "القلم", "Pena", 52, 564, 29, true),
        Surah(69, "Al-Haqqah", "الحاقة", "Hari Kiamat", 52, 566, 29, true),
        Surah(70, "Al-Ma'arij", "المعارج", "Tempat Naik", 44, 568, 29, true),
        Surah(71, "Nuh", "نوح", "Nabi Nuh", 28, 570, 29, true),
        Surah(72, "Al-Jinn", "الجن", "Jin", 28, 572, 29, true),
        Surah(73, "Al-Muzzammil", "المزمل", "Orang Yang Berselimut", 20, 574, 29, true),
        Surah(74, "Al-Muddatstsir", "المدثر", "Orang Yang Berkemul", 56, 575, 29, true),
        Surah(75, "Al-Qiyamah", "القيامة", "Hari Kiamat", 40, 577, 29, true),
        Surah(76, "Al-Insan", "الإنسان", "Manusia", 31, 578, 29, false),
        Surah(77, "Al-Mursalat", "المرسلات", "Malaikat Yang Diutus", 50, 580, 29, true),
        Surah(78, "An-Naba'", "النبأ", "Berita Besar", 40, 582, 30, true),
        Surah(79, "An-Nazi'at", "النازعات", "Malaikat Pencabut Roh", 46, 583, 30, true),
        Surah(80, "'Abasa", "عبس", "Ia Bermuka Masam", 42, 585, 30, true),
        Surah(81, "At-Takwir", "التكوير", "Menggulung Matahari", 29, 586, 30, true),
        Surah(82, "Al-Infithar", "الانفطار", "Terbelah", 19, 587, 30, true),
        Surah(83, "Al-Muthaffifin", "المطففين", "Orang-Orang Curang", 36, 587, 30, true),
        Surah(84, "Al-Insyiqaq", "الانشقاق", "Terbelah", 25, 589, 30, true),
        Surah(85, "Al-Buruj", "البروج", "Gugusan Bintang", 22, 590, 30, true),
        Surah(86, "Ath-Thariq", "الطارق", "Bintang Yang Datang di Malam Hari", 17, 591, 30, true),
        Surah(87, "Al-A'la", "الأعلى", "Maha Tinggi", 19, 591, 30, true),
        Surah(88, "Al-Ghasyiyah", "الغاشية", "Hari Pembalasan", 26, 592, 30, true),
        Surah(89, "Al-Fajr", "الفجر", "Fajar", 30, 593, 30, true),
        Surah(90, "Al-Balad", "البلد", "Negeri", 20, 594, 30, true),
        Surah(91, "Asy-Syams", "الشمس", "Matahari", 15, 595, 30, true),
        Surah(92, "Al-Lail", "الليل", "Malam", 21, 595, 30, true),
        Surah(93, "Adh-Dhuha", "الضحى", "Waktu Dhuha", 11, 596, 30, true),
        Surah(94, "Al-Insyirah", "الشرح", "Kelapangan Dada", 8, 596, 30, true),
        Surah(95, "At-Tin", "التين", "Buah Tin", 8, 597, 30, true),
        Surah(96, "Al-'Alaq", "العلق", "Segumpal Darah", 19, 597, 30, true),
        Surah(97, "Al-Qadr", "القدر", "Kemuliaan", 5, 598, 30, true),
        Surah(98, "Al-Bayyinah", "البينة", "Bukti Nyata", 8, 598, 30, false),
        Surah(99, "Az-Zalzalah", "الزلزلة", "Goncangan", 8, 599, 30, false),
        Surah(100, "Al-'Adiyat", "العاديات", "Kuda Perang", 11, 599, 30, true),
        Surah(101, "Al-Qari'ah", "القارعة", "Hari Kiamat", 11, 600, 30, true),
        Surah(102, "At-Takatsur", "التكاثر", "Bermegah-Megahan", 8, 600, 30, true),
        Surah(103, "Al-'Ashr", "العصر", "Masa / Waktu", 3, 601, 30, true),
        Surah(104, "Al-Humazah", "الهمزة", "Pengumpat", 9, 601, 30, true),
        Surah(105, "Al-Fil", "الفيل", "Gajah", 5, 601, 30, true),
        Surah(106, "Quraisy", "قريش", "Suku Quraisy", 4, 602, 30, true),
        Surah(107, "Al-Ma'un", "الماعون", "Barang Yang Berguna", 7, 602, 30, true),
        Surah(108, "Al-Kautsar", "الكوثر", "Nikmat Yang Banyak", 3, 602, 30, true),
        Surah(109, "Al-Kafirun", "الكافرون", "Orang-Orang Kafir", 6, 603, 30, true),
        Surah(110, "An-Nashr", "النصر", "Pertolongan", 3, 603, 30, false),
        Surah(111, "Al-Lahab", "المسد", "Gejolak Api", 5, 603, 30, true),
        Surah(112, "Al-Ikhlas", "الإخلاص", "Kemurnian Keesaan Allah", 4, 604, 30, true),
        Surah(113, "Al-Falaq", "الفلق", "Waktu Subuh", 5, 604, 30, true),
        Surah(114, "An-Nas", "الناس", "Manusia", 6, 604, 30, true)
    )

    val JUZ_START_PAGES: List<Int> = listOf(
        1,   // Juz 1
        22,  // Juz 2
        42,  // Juz 3
        62,  // Juz 4
        82,  // Juz 5
        102, // Juz 6
        122, // Juz 7
        142, // Juz 8
        162, // Juz 9
        182, // Juz 10
        202, // Juz 11
        222, // Juz 12
        242, // Juz 13
        262, // Juz 14
        282, // Juz 15
        302, // Juz 16
        322, // Juz 17
        342, // Juz 18
        362, // Juz 19
        382, // Juz 20
        402, // Juz 21
        422, // Juz 22
        442, // Juz 23
        462, // Juz 24
        482, // Juz 25
        502, // Juz 26
        522, // Juz 27
        542, // Juz 28
        562, // Juz 29
        582  // Juz 30
    )

    fun getSurahByNumber(number: Int): Surah {
        return SURAHS.getOrElse(number - 1) { SURAHS.first() }
    }

    fun getSurahByPage(page: Int): Surah {
        val clamped = page.coerceIn(1, 604)
        for (i in SURAHS.indices.reversed()) {
            if (clamped >= SURAHS[i].startPage) {
                return SURAHS[i]
            }
        }
        return SURAHS.first()
    }

    fun getJuzByPage(page: Int): Int {
        val clamped = page.coerceIn(1, 604)
        for (i in JUZ_START_PAGES.indices.reversed()) {
            if (clamped >= JUZ_START_PAGES[i]) {
                return i + 1
            }
        }
        return 1
    }
}
