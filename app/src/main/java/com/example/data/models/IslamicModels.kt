package com.example.data.models

data class PrayerTime(
    val id: String,
    val nameBn: String,
    val nameAr: String,
    val timeStr: String,
    val isWaqtCurrent: Boolean = false,
    val isNext: Boolean = false
)

data class CityLocation(
    val nameBn: String,
    val nameEn: String,
    val latitude: Double,
    val longitude: Double,
    val fajrOffsetMin: Int = 0,
    val dhuhrOffsetMin: Int = 0,
    val asrOffsetMin: Int = 0,
    val maghribOffsetMin: Int = 0,
    val ishaOffsetMin: Int = 0
)

data class Ayah(
    val numberInSurah: Int,
    val arabicText: String,
    val banglaPronunciation: String,
    val banglaTranslation: String,
    val audioUrl: String = ""
)

data class Surah(
    val id: Int,
    val nameArabic: String,
    val nameBangla: String,
    val banglaMeaning: String,
    val totalVerses: Int,
    val revelationType: String, // মক্কী বা মাদানী
    val ayahs: List<Ayah> = emptyList()
)

data class HadithCategory(
    val id: String,
    val titleBn: String,
    val englishName: String
)

data class Hadith(
    val id: Int,
    val categoryId: String,
    val arabicText: String,
    val banglaTranslation: String,
    val narratorBn: String,
    val bookBn: String,
    val hadithNumber: String,
    val gradeBn: String = "সহীহ"
)

data class DhikrPreset(
    val id: String,
    val arabicText: String,
    val banglaPronunciation: String,
    val banglaMeaning: String,
    val target: Int,
    val virtueBn: String
)

data class DuaCategory(
    val id: String,
    val titleBn: String,
    val iconName: String
)

data class Dua(
    val id: Int,
    val categoryId: String,
    val titleBn: String,
    val situationBn: String,
    val arabicText: String,
    val banglaPronunciation: String,
    val banglaMeaning: String,
    val significanceBn: String,
    val referenceBn: String
)
