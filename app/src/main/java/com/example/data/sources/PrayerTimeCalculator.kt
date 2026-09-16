package com.example.data.sources

import com.example.data.models.CityLocation
import com.example.data.models.PrayerTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.*

object PrayerTimeCalculator {

    val cities = listOf(
        CityLocation("ঢাকা", "Dhaka", 23.8103, 90.4125),
        CityLocation("চট্টগ্রাম", "Chattogram", 22.3569, 91.7832, -5, -4, -4, -4, -5),
        CityLocation("সিলেট", "Sylhet", 24.8949, 91.8687, -6, -5, -5, -6, -6),
        CityLocation("রাজশাহী", "Rajshahi", 24.3745, 88.6042, 6, 5, 5, 6, 6),
        CityLocation("খুলনা", "Khulna", 22.8456, 89.5403, 4, 3, 3, 4, 4),
        CityLocation("বরিশাল", "Barishal", 22.7010, 90.3535, 1, 1, 1, 1, 1),
        CityLocation("রংপুর", "Rangpur", 25.7439, 89.2752, 4, 3, 3, 4, 4),
        CityLocation("ময়মনসিংহ", "Mymensingh", 24.7471, 90.4203, -1, 0, 0, -1, -1),
        CityLocation("মক্কা মুকাররমা", "Makkah", 21.4225, 39.8262),
        CityLocation("মদিনা মুনাওয়ারা", "Madinah", 24.4672, 39.6111)
    )

    fun calculateDailySchedule(
        city: CityLocation = cities[0],
        date: Calendar = Calendar.getInstance()
    ): Map<String, Any> {
        val dayOfYear = date.get(Calendar.DAY_OF_YEAR)
        val lat = city.latitude
        val lng = city.longitude
        val timezone = 6.0 // GMT+6 for Bangladesh

        // Astronomical calculations
        val d = dayOfYear.toDouble()
        val b = 2 * Math.PI * (d - 81) / 365.0
        val eot = 9.87 * sin(2 * b) - 7.53 * cos(b) - 1.5 * sin(b) // Equation of time in minutes
        val declination = 23.45 * sin(Math.toRadians((360.0 / 365.0) * (d - 81))) // Solar declination in degrees

        // Solar Noon in hours (UTC)
        val solarNoonUtc = 12.0 - (lng / 15.0) - (eot / 60.0)
        val solarNoonLocal = solarNoonUtc + timezone

        // Hour angle helper
        fun hourAngle(altitude: Double): Double? {
            val latRad = Math.toRadians(lat)
            val decRad = Math.toRadians(declination)
            val altRad = Math.toRadians(altitude)
            val cosHA = (sin(altRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
            return if (cosHA in -1.0..1.0) {
                Math.toDegrees(acos(cosHA)) / 15.0
            } else {
                null
            }
        }

        // Calculation angles:
        // Fajr: -18 degrees (Islamic University of Karachi / Bangladesh Islamic Foundation)
        // Sunrise: -0.833 degrees (refraction and sun disk)
        // Asr: Hanafi method (shadow length = 2 * object + noon shadow)
        // Maghrib: -0.833 degrees
        // Isha: -18 degrees

        val fajrHA = hourAngle(-18.0) ?: 1.8
        val sunriseHA = hourAngle(-0.833) ?: 1.5
        val maghribHA = hourAngle(-0.833) ?: 1.5
        val ishaHA = hourAngle(-18.0) ?: 1.8

        // Asr angle calculation (Hanafi standard in Bangladesh)
        val latDecDiff = abs(lat - declination)
        val noonShadowRatio = tan(Math.toRadians(latDecDiff))
        val asrAltitude = Math.toDegrees(atan(1.0 / (2.0 + noonShadowRatio)))
        val asrHA = hourAngle(asrAltitude) ?: 2.2

        val fajrTimeHour = solarNoonLocal - fajrHA + (city.fajrOffsetMin / 60.0)
        val sunriseTimeHour = solarNoonLocal - sunriseHA
        val dhuhrTimeHour = solarNoonLocal + (city.dhuhrOffsetMin / 60.0)
        val asrTimeHour = solarNoonLocal + asrHA + (city.asrOffsetMin / 60.0)
        val maghribTimeHour = solarNoonLocal + maghribHA + (city.maghribOffsetMin / 60.0)
        val ishaTimeHour = solarNoonLocal + ishaHA + (city.ishaOffsetMin / 60.0)

        fun formatHours(hours: Double): String {
            var totalMinutes = (hours * 60).roundToInt()
            while (totalMinutes < 0) totalMinutes += 24 * 60
            totalMinutes %= (24 * 60)
            val h24 = totalMinutes / 60
            val m = totalMinutes % 60
            val h12 = if (h24 % 12 == 0) 12 else h24 % 12
            val amPm = if (h24 < 12) "AM" else "PM"
            return String.format(Locale.getDefault(), "%02d:%02d %s", h12, m, amPm)
        }

        fun toMinuteOfDay(hours: Double): Int {
            var mins = (hours * 60).roundToInt()
            while (mins < 0) mins += 24 * 60
            return mins % (24 * 60)
        }

        val fajrMinutes = toMinuteOfDay(fajrTimeHour)
        val sunriseMinutes = toMinuteOfDay(sunriseTimeHour)
        val dhuhrMinutes = toMinuteOfDay(dhuhrTimeHour)
        val asrMinutes = toMinuteOfDay(asrTimeHour)
        val maghribMinutes = toMinuteOfDay(maghribTimeHour)
        val ishaMinutes = toMinuteOfDay(ishaTimeHour)

        // Sehri ends 5 minutes before Fajr
        val sehriEndMinutes = fajrMinutes - 5
        val sehriEndHour = sehriEndMinutes / 60.0

        // Current time in minutes
        val now = Calendar.getInstance()
        val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        val rawList = listOf(
            Triple("fajr", "ফজর", "الفجر") to fajrMinutes,
            Triple("sunrise", "সূর্যোদয়", "الشروق") to sunriseMinutes,
            Triple("dhuhr", "যোহর", "الظهر") to dhuhrMinutes,
            Triple("asr", "আসর", "العصر") to asrMinutes,
            Triple("maghrib", "মাগরিব", "المغرب") to maghribMinutes,
            Triple("isha", "ইশা", "العشاء") to ishaMinutes
        )

        // Find current and next prayer
        var currentWaqt = "ফজর"
        var nextPrayerTime: PrayerTime? = null
        var minutesUntilNext = 0

        val prayerTimesList = mutableListOf<PrayerTime>()

        for (i in rawList.indices) {
            val (meta, mins) = rawList[i]
            val isCurrent = if (i < rawList.size - 1) {
                nowMinutes in mins until rawList[i + 1].second
            } else {
                nowMinutes >= mins || nowMinutes < rawList[0].second
            }

            if (isCurrent && meta.first != "sunrise") {
                currentWaqt = meta.second
            }

            prayerTimesList.add(
                PrayerTime(
                    id = meta.first,
                    nameBn = meta.second,
                    nameAr = meta.third,
                    timeStr = formatHours(mins / 60.0),
                    isWaqtCurrent = isCurrent
                )
            )
        }

        // Determine next waqt
        val sortedUpcoming = rawList.filter { it.first.first != "sunrise" }
        val nextOne = sortedUpcoming.firstOrNull { it.second > nowMinutes }
            ?: sortedUpcoming.first() // Tomorrow's Fajr

        val nextDiff = if (nextOne.second > nowMinutes) {
            nextOne.second - nowMinutes
        } else {
            (24 * 60 - nowMinutes) + nextOne.second
        }
        minutesUntilNext = nextDiff

        nextPrayerTime = PrayerTime(
            id = nextOne.first.first,
            nameBn = nextOne.first.second,
            nameAr = nextOne.first.third,
            timeStr = formatHours(nextOne.second / 60.0),
            isNext = true
        )

        val hoursLeft = minutesUntilNext / 60
        val minsLeft = minutesUntilNext % 60
        val countdownStr = if (hoursLeft > 0) {
            "$hoursLeft ঘণ্টা $minsLeft মিনিট বাকি"
        } else {
            "$minsLeft মিনিট বাকি"
        }

        return mapOf(
            "prayers" to prayerTimesList,
            "currentWaqt" to currentWaqt,
            "nextPrayer" to nextPrayerTime,
            "countdown" to countdownStr,
            "sehriEnd" to formatHours(sehriEndHour),
            "iftarTime" to formatHours(maghribTimeHour),
            "dateBn" to getBengaliDate(now),
            "hijriDateBn" to getHijriDateBengali(now)
        )
    }

    private fun getBengaliDate(calendar: Calendar): String {
        val daysBn = listOf("রবিবার", "সোমবার", "মঙ্গলবার", "বুধবার", "বৃহস্পতিবার", "শুক্রবার", "শনিবার")
        val monthsBn = listOf("জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন", "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর")
        
        val dayOfWeek = daysBn[calendar.get(Calendar.DAY_OF_WEEK) - 1]
        val day = toBengaliDigits(calendar.get(Calendar.DAY_OF_MONTH).toString())
        val month = monthsBn[calendar.get(Calendar.MONTH)]
        val year = toBengaliDigits(calendar.get(Calendar.YEAR).toString())

        return "$dayOfWeek, $day $month $year"
    }

    private fun getHijriDateBengali(calendar: Calendar): String {
        // Approximate Hijri computation based on Julian day
        val cal = calendar.clone() as Calendar
        var y = cal.get(Calendar.YEAR)
        var m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)

        if (m < 3) {
            y -= 1
            m += 12
        }

        val a = y / 100
        val b = 2 - a + a / 4
        val jd = (365.25 * (y + 4716)).toLong() + (30.6001 * (m + 1)).toLong() + d + b - 1524.5

        val l = jd - 1948440 + 10632
        val n = ((l - 1) / 10631).toInt()
        val l1 = l - 10631 * n + 354
        val j = (((10985 - l1) / 5316).toInt()) * ((50 * l1 / 17719).toInt()) + ((l1 / 5670).toInt()) * ((43 * l1 / 15238).toInt())
        val l2 = l1 - (((30 - j) / 15).toInt()) * ((17719 * j / 50).toInt()) - ((j / 16).toInt()) * ((15238 * j / 43).toInt()) + 29
        val mHijri = ((24 * l2) / 709).toInt()
        val dHijri = (l2 - ((709 * mHijri) / 24)).toInt()
        val yHijri = 30 * n + j - 30

        val hijriMonthsBn = listOf(
            "মুহাররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি", "জমাদিউল আউয়াল",
            "জমাদিউস সানি", "রজব", "শাবান", "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ্জ"
        )

        val monthName = if (mHijri in 1..12) hijriMonthsBn[mHijri - 1] else "রমজান"
        return "${toBengaliDigits(dHijri.toString())} $monthName ${toBengaliDigits(yHijri.toString())} হিজরি"
    }

    fun toBengaliDigits(input: String): String {
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(bnDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
