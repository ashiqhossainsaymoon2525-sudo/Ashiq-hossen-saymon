package com.example.data.sources

import com.example.data.models.DhikrPreset

object TasbihDataSource {

    val presets = listOf(
        DhikrPreset(
            id = "subhanallah",
            arabicText = "سُبْحَانَ اللَّهِ",
            banglaPronunciation = "সুবহানাল্লাহ",
            banglaMeaning = "আল্লাহ মহা পবিত্র ও ত্রুটিমুক্ত",
            target = 33,
            virtueBn = "প্রতিবার সুবহানাল্লাহ পাঠে জান্নাতে একটি করে গাছ রোপণ করা হয়।"
        ),
        DhikrPreset(
            id = "alhamdulillah",
            arabicText = "الْحَمْدُ لِلَّهِ",
            banglaPronunciation = "আলহামদুলিল্লাহ",
            banglaMeaning = "সকল প্রশংসা কেবল আল্লাহর জন্য",
            target = 33,
            virtueBn = "আলহামদুলিল্লাহ মিজানের পাল্লাকে নেকিতে পরিপূর্ণ করে দেয়।"
        ),
        DhikrPreset(
            id = "allahu_akbar",
            arabicText = "اللَّهُ أَكْبَرُ",
            banglaPronunciation = "আল্লাহু আকবার",
            banglaMeaning = "আল্লাহ মহান ও সর্বশ্রেষ্ঠ",
            target = 34,
            virtueBn = "আল্লাহর শ্রেষ্ঠত্ব ঘোষণার সর্বোত্তম বাক্য।"
        ),
        DhikrPreset(
            id = "la_ilaha_illallah",
            arabicText = "لَا إِلٰهَ إِلَّا اللَّهُ",
            banglaPronunciation = "লা ইলাহা ইল্লাল্লাহ",
            banglaMeaning = "আল্লাহ ছাড়া সত্য কোনো উপাস্য নেই",
            target = 100,
            virtueBn = "সর্বশ্রেষ্ঠ জিকির হলো 'লা ইলাহা ইল্লাল্লাহ'।"
        ),
        DhikrPreset(
            id = "astaghfirullah",
            arabicText = "أَسْتَغْفِرُ اللَّهَ",
            banglaPronunciation = "আস্তাগফিরুল্লাহ",
            banglaMeaning = "আমি আল্লাহর নিকট ক্ষমা প্রার্থনা করছি",
            target = 100,
            virtueBn = "অধিক ইস্তেগফারে রিজিক ও অন্তরের সকল দুশ্চিন্তা দূর হয়।"
        ),
        DhikrPreset(
            id = "durood",
            arabicText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ",
            banglaPronunciation = "আল্লাহুম্মা সাল্লি আলা মুহাম্মাদ",
            banglaMeaning = "হে আল্লাহ! মুহাম্মদ (সাঃ)-এর ওপর শান্তি ও রহমত বর্ষণ করুন",
            target = 100,
            virtueBn = "একবার দুরুদ পাঠ করলে আল্লাহ তায়ালা ১০টি রহমত বর্ষণ করেন।"
        ),
        DhikrPreset(
            id = "subhanallahi_wa_bihamdihi",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            banglaPronunciation = "সুবহানাল্লাহি ওয়া বিহামদিহী, সুবহানাল্লাহিল আযীম",
            banglaMeaning = "আল্লাহর প্রশংসাসহ পবিত্রতা ঘোষণা করছি, মহান আল্লাহ অতীব পবিত্র",
            target = 100,
            virtueBn = "উভয় বাক্য উচ্চারণে সহজ কিন্তু মিজানের পাল্লায় অত্যন্ত ভারী।"
        ),
        DhikrPreset(
            id = "la_hawla",
            arabicText = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            banglaPronunciation = "লা হাওলা ওয়ালা কুওয়াতা ইল্লা বিল্লাহ",
            banglaMeaning = "আল্লাহর সাহায্য ছাড়া পাপ থেকে ফেরার বা ভালো কাজের কোনো শক্তি নেই",
            target = 100,
            virtueBn = "এটি জান্নাতের অন্যতম অমূল্য রত্নভাণ্ডার।"
        )
    )
}
