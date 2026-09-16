package com.example.data.sources

import com.example.data.models.Hadith
import com.example.data.models.HadithCategory

object HadithDataSource {

    val categories = listOf(
        HadithCategory("all", "সকল হাদিস", "All"),
        HadithCategory("iman", "ঈমান ও তাওহীদ", "Faith"),
        HadithCategory("salat", "সালাত ও নামাজ", "Prayer"),
        HadithCategory("sawm", "রোজা ও রমজান", "Fasting"),
        HadithCategory("zakat", "যাকাত ও সাদাকাহ", "Charity"),
        HadithCategory("akhlaq", "চরিত্র ও সদাচরণ", "Manners"),
        HadithCategory("parents", "পিতা-মাতা ও পরিবার", "Parents"),
        HadithCategory("tawbah", "তাওবা ও ক্ষমা", "Repentance"),
        HadithCategory("dhikr", "দু'আ ও যিকির", "Dhikr & Dua"),
        HadithCategory("ilm", "ইলম ও জ্ঞান", "Knowledge")
    )

    val hadiths = listOf(
        Hadith(
            id = 1,
            categoryId = "iman",
            arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
            banglaTranslation = "নিশ্চয়ই সমস্ত কাজের ফলাফল নিয়তের ওপর নির্ভরশীল। আর প্রত্যেক ব্যক্তি তাই পাবে যার নিয়ত সে করবে।",
            narratorBn = "হযরত উমর ইবনুল খাত্তাব (রাঃ)",
            bookBn = "সহীহ বুখারী",
            hadithNumber = "হাদিস ১"
        ),
        Hadith(
            id = 2,
            categoryId = "iman",
            arabicText = "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
            banglaTranslation = "প্রকৃত মুসলিম সেই ব্যক্তি, যার জিহ্বা ও হাত হতে অপর মুসলিমগণ নিরাপদ থাকে।",
            narratorBn = "হযরত আব্দুল্লাহ ইবনে আমর (রাঃ)",
            bookBn = "সহীহ বুখারী ও সহীহ মুসলিম",
            hadithNumber = "বুখারী ১০, মুসলিম ৪০"
        ),
        Hadith(
            id = 3,
            categoryId = "salat",
            arabicText = "بَيْنَ الرَّجُلِ وَبَيْنَ الشِّرْكِ وَالْكُفْرِ تَرْكُ الصَّلاَةِ",
            banglaTranslation = "ব্যক্তি এবং শিরক ও কুফরের মাঝে একমাত্র পার্থক্য হচ্ছে সালাত পরিত্যাগ করা।",
            narratorBn = "হযরত জাবির ইবনে আব্দুল্লাহ (রাঃ)",
            bookBn = "সহীহ মুসলিম",
            hadithNumber = "হাদিস ৮২"
        ),
        Hadith(
            id = 4,
            categoryId = "salat",
            arabicText = "الصَّلَاةُ عِمَادُ الدِّينِ ، فَمَنْ أَقَامَهَا فَقَدْ أَقَامَ الدِّينَ ، وَمَنْ هَدَمَهَا فَقَدْ هَدَمَ الدِّينَ",
            banglaTranslation = "নামাজ হচ্ছে দ্বীনের স্তম্ভ। যে ব্যক্তি তা কায়েম করল সে দ্বীনকে প্রতিষ্ঠিত করল, আর যে তা বিনষ্ট করল সে দ্বীনকেই ধ্বংস করল।",
            narratorBn = "হযরত ওমর ইবনুল খাত্তাব (রাঃ)",
            bookBn = "বাইহাকী (শুআবুল ঈমান)",
            hadithNumber = "হাদিস ২৮০৭"
        ),
        Hadith(
            id = 5,
            categoryId = "salat",
            arabicText = "أَوَّلُ مَا يُحَاسَبُ بِهِ الْعَبْدُ يَوْمَ الْقِيَامَةِ مِنْ عَمَلِهِ صَلَاتُهُ",
            banglaTranslation = "কেয়ামতের দিন বান্দার আমলসমূহের মধ্যে সর্বপ্রথম যে জিনিসের হিসাব নেওয়া হবে, তা হলো তার সালাত বা নামাজ।",
            narratorBn = "হযরত আবু হুরায়রা (রাঃ)",
            bookBn = "সুনান আত-তিরমিযী",
            hadithNumber = "হাদিস ৪১৩"
        ),
        Hadith(
            id = 6,
            categoryId = "sawm",
            arabicText = "مَنْ صَامَ رَمَضَانَ إِيمَانًا وَاحْتِسَابًا غُفِرَ لَهُ مَا تَقَدَّمَ مِنْ ذَنْبِهِ",
            banglaTranslation = "যে ব্যক্তি ঈমানের সাথে ও সওয়াবের প্রত্যাশায় রমজানের রোজা রাখবে, তার পূর্বের সকল গুনাহ ক্ষমা করে দেওয়া হবে।",
            narratorBn = "হযরত আবু হুরায়রা (রাঃ)",
            bookBn = "সহীহ বুখারী",
            hadithNumber = "হাদিস ৩৮"
        ),
        Hadith(
            id = 7,
            categoryId = "sawm",
            arabicText = "الصِّيَامُ جُنَّةٌ فَلاَ يَرْفُثْ وَلاَ يَجْهَلْ",
            banglaTranslation = "রোজা হচ্ছে (জাহান্নাম ও পাপ থেকে রক্ষার) ঢালস্বরূপ। অতএব রোজা রেখে কেউ যেন অশালীন কথা না বলে এবং জাহেলী আচরণ না করে।",
            narratorBn = "হযরত আবু হুরায়রা (রাঃ)",
            bookBn = "সহীহ বুখারী",
            hadithNumber = "হাদিস ১৮৯৪"
        ),
        Hadith(
            id = 8,
            categoryId = "zakat",
            arabicText = "اتَّقُوا النَّارَ وَلَوْ بِشِقِّ تَمْرَةٍ",
            banglaTranslation = "তোমরা জাহান্নামের আগুন থেকে নিজেদের রক্ষা করো, তা এক টুকরো খেজুর সাদাকাহ করার বিনিময়ে হলেও।",
            narratorBn = "হযরত আদী ইবনে হাতিম (রাঃ)",
            bookBn = "সহীহ বুখারী",
            hadithNumber = "হাদিস ১৪১৭"
        ),
        Hadith(
            id = 9,
            categoryId = "zakat",
            arabicText = "مَا نَقَصَتْ صَدَقَةٌ مِنْ مَالٍ",
            banglaTranslation = "সাদাকাহ প্রদানে কখনো কোনো ধন-সম্পদ হ্রাস পায় না, বরং আল্লাহ বরকত বৃদ্ধি করে দেন।",
            narratorBn = "হযরত আবু হুরায়রা (রাঃ)",
            bookBn = "সহীহ মুসলিম",
            hadithNumber = "হাদিস ২৫৮৮"
        ),
        Hadith(
            id = 10,
            categoryId = "akhlaq",
            arabicText = "إِنَّ مِنْ أَحَبِّكُمْ إِلَيَّ وَأَقْرَبِكُمْ مِنِّي مَجْلِسًا يَوْمَ الْقِيَامَةِ أَحَاسِنَكُمْ أَخْلَاقًا",
            banglaTranslation = "তোমাদের মধ্যে কিয়ামতের দিন আমার নিকট সবচেয়ে প্রিয় ও সর্বাধিক নৈকট্যপ্রাপ্ত ব্যক্তি সে হবে, যার চরিত্র ও আচরণ সবচেয়ে সুন্দর।",
            narratorBn = "হযরত জাবির (রাঃ)",
            bookBn = "সুনান আত-তিরমিযী",
            hadithNumber = "হাদিস ২০১৭"
        ),
        Hadith(
            id = 11,
            categoryId = "akhlaq",
            arabicText = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            banglaTranslation = "তোমার কোনো মুসলিম ভাইয়ের মুখের দিকে তাকিয়ে মুচকি হাসাও তোমার জন্য একটি সাদাকাহ।",
            narratorBn = "হযরত আবু যার (রাঃ)",
            bookBn = "জামে আত-তিরমিযী",
            hadithNumber = "হাদিস ১৯৫৬"
        ),
        Hadith(
            id = 12,
            categoryId = "parents",
            arabicText = "رِضَى الرَّبِّ فِي رِضَى الْوَالِدِ وَسَخَطُ الرَّبِّ فِي سَخَطِ الْوَالِدِ",
            banglaTranslation = "পিতার সন্তুষ্টির মাঝেই প্রতিপালক আল্লাহর সন্তুষ্টি নিহিত, আর পিতার অসন্তুষ্টিতেই আল্লাহর অসন্তুষ্টি নিহিত।",
            narratorBn = "হযরত আব্দুল্লাহ ইবনে আমর (রাঃ)",
            bookBn = "জামে আত-তিরমিযী",
            hadithNumber = "হাদিস ১৮৯৯"
        ),
        Hadith(
            id = 13,
            categoryId = "parents",
            arabicText = "الْجَنَّةُ تَحْتَ أَقْدَامِ الأُمَّهَاتِ",
            banglaTranslation = "মায়ের পদতলেই সন্তানের জান্নাত (মায়ের সেবা ও আনুগত্যের মাধ্যমে জান্নাত লাভ করা যায়)।",
            narratorBn = "হযরত মুয়াবিয়া ইবনে জাহিমা (রাঃ)",
            bookBn = "সুনান আন-নাসায়ী",
            hadithNumber = "হাদিস ৩১০৪"
        ),
        Hadith(
            id = 14,
            categoryId = "tawbah",
            arabicText = "التَّائِبُ مِنَ الذَّنْبِ كَمَنْ لاَ ذَنْبَ لَهُ",
            banglaTranslation = "গুনাহ থেকে খাঁটি অন্তরে তওবাকারী ব্যক্তি এমন নিষ্পাপ হয়ে যায়, যেন সে কখনো কোনো পাপই করেনি।",
            narratorBn = "হযরত আব্দুল্লাহ ইবনে মাসউদ (রাঃ)",
            bookBn = "সুনান ইবনে মাজাহ",
            hadithNumber = "হাদিস ৪২৫০"
        ),
        Hadith(
            id = 15,
            categoryId = "tawbah",
            arabicText = "كُلُّ ابْنِ آدَمَ خَطَّاءٌ وَخَيْرُ الْخَطَّائِينَ التَّوَّابُونَ",
            banglaTranslation = "প্রত্যেক আদম সন্তানই ভুলত্রুটি ও গুনাহগার; আর গুনাহগারদের মধ্যে তারাই সর্বোত্তম যারা আল্লাহর নিকট তওবা করে।",
            narratorBn = "হযরত আনাস ইবনে মালিক (রাঃ)",
            bookBn = "সুনান আত-তিরমিযী",
            hadithNumber = "হাদিস ২৪৯৯"
        ),
        Hadith(
            id = 16,
            categoryId = "dhikr",
            arabicText = "مَثَلُ الَّذِي يَذْكُرُ رَبَّهُ وَالَّذِي لاَ يَذْكُرُ رَبَّهُ مَثَلُ الْحَيِّ وَالْمَيِّتِ",
            banglaTranslation = "যে ব্যক্তি তার প্রতিপালককে স্মরণ করে (যিকির করে) এবং যে স্মরণ করে না, তাদের উদাহরণ জীবিত ও মৃতের মতো।",
            narratorBn = "হযরত আবু মুসা আশয়ারী (রাঃ)",
            bookBn = "সহীহ বুখারী",
            hadithNumber = "হাদিস ৬৪০৭"
        ),
        Hadith(
            id = 17,
            categoryId = "dhikr",
            arabicText = "الدُّعَاءُ هُوَ الْعِبَادَةُ",
            banglaTranslation = "দু'আই হলো সমস্ত ইবাদতের মূল ও সারবস্তু।",
            narratorBn = "হযরত নু'মান ইবনে বাশীর (রাঃ)",
            bookBn = "সুনান আত-তিরমিযী",
            hadithNumber = "হাদিস ২৯৬৯"
        ),
        Hadith(
            id = 18,
            categoryId = "ilm",
            arabicText = "طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ",
            banglaTranslation = "দ্বীনী জ্ঞান বা ইলম অর্জন করা প্রত্যেক মুসলমানের ওপর অবশ্য পালনীয় ফরজ।",
            narratorBn = "হযরত আনাস ইবনে মালিক (রাঃ)",
            bookBn = "সুনান ইবনে মাজাহ",
            hadithNumber = "হাদিস ২২৪"
        ),
        Hadith(
            id = 19,
            categoryId = "ilm",
            arabicText = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            banglaTranslation = "তোমাদের মধ্যে সেই ব্যক্তি সর্বোত্তম, যে নিজে কুরআন শেখে এবং অপরকে তা শিক্ষা দেয়।",
            narratorBn = "হযরত উসমান ইবনে আফফান (রাঃ)",
            bookBn = "সহীহ বুখারী",
            hadithNumber = "হাদিস ৫০২৭"
        )
    )

    fun searchHadith(query: String, categoryId: String): List<Hadith> {
        val filteredByCategory = if (categoryId == "all" || categoryId.isBlank()) {
            hadiths
        } else {
            hadiths.filter { it.categoryId == categoryId }
        }

        if (query.isBlank()) return filteredByCategory

        val cleanQuery = query.trim().lowercase()
        return filteredByCategory.filter {
            it.banglaTranslation.lowercase().contains(cleanQuery) ||
            it.arabicText.contains(cleanQuery) ||
            it.narratorBn.lowercase().contains(cleanQuery) ||
            it.bookBn.lowercase().contains(cleanQuery) ||
            it.hadithNumber.lowercase().contains(cleanQuery)
        }
    }
}
