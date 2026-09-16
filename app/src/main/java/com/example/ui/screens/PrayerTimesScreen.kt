package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CityLocation
import com.example.data.models.PrayerTime
import com.example.data.sources.PrayerTimeCalculator
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    onNavigateToQibla: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCity by remember { mutableStateOf(PrayerTimeCalculator.cities[0]) }
    var showCityDialog by remember { mutableStateOf(false) }

    // Recalculate prayer times whenever city changes
    val schedule = remember(selectedCity) {
        PrayerTimeCalculator.calculateDailySchedule(selectedCity)
    }

    val prayers = remember(schedule) {
        @Suppress("UNCHECKED_CAST")
        schedule["prayers"] as? List<PrayerTime> ?: emptyList()
    }
    val currentWaqt = schedule["currentWaqt"] as? String ?: "ফজর"
    val nextPrayer = schedule["nextPrayer"] as? PrayerTime
    val countdown = schedule["countdown"] as? String ?: ""
    val sehriEnd = schedule["sehriEnd"] as? String ?: ""
    val iftarTime = schedule["iftarTime"] as? String ?: ""
    val dateBn = schedule["dateBn"] as? String ?: ""
    val hijriDateBn = schedule["hijriDateBn"] as? String ?: ""

    Scaffold(
        modifier = modifier.testTag("prayer_times_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "নামাজের সময়সূচী",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = selectedCity.nameBn,
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight
                        )
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = { showCityDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = GoldAccent.copy(alpha = 0.25f),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("change_city_button")
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "শহর বদলান", fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EmeraldPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Card: Current Waqt & Next Countdown
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_prayer_card"),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(EmeraldPrimary, EmeraldDark)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            // Islamic (Hijri) and Gregorian Dual Calendar Header
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("date_badge_header"),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.Black.copy(alpha = 0.22f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = GoldAccent.copy(alpha = 0.2f),
                                            modifier = Modifier.size(38.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.CalendarMonth,
                                                    contentDescription = "Hijri & Gregorian Calendar",
                                                    tint = GoldAccent,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = hijriDateBn,
                                                    color = GoldLight,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    modifier = Modifier.testTag("hijri_date_text")
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = GoldDark.copy(alpha = 0.35f)
                                                ) {
                                                    Text(
                                                        text = "হিজরি",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = GoldLight,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "ইংরেজি: $dateBn",
                                                color = Color.White.copy(alpha = 0.88f),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.testTag("gregorian_date_text")
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = GoldAccent.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = selectedCity.nameBn,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "বর্তমান ওয়াক্ত",
                                        color = Color.White.copy(alpha = 0.75f),
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = currentWaqt,
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (nextPrayer != null) {
                                        Text(
                                            text = "পরবর্তী ওয়াক্ত: ${nextPrayer.nameBn} (${nextPrayer.timeStr})",
                                            color = GoldLight,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }

                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color.Black.copy(alpha = 0.25f),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = GoldAccent,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = countdown,
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = onNavigateToQibla,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = GoldLight
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = Brush.horizontalGradient(listOf(GoldAccent, GoldLight))
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("কিবলা কম্পাস", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Sehri & Iftar Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.NightlightRound, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "সেহরি শেষ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = sehriEnd, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(GoldLight.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.WbSunny, contentDescription = null, tint = GoldDark, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "ইফতার শুরু", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = iftarTime, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            // Section Header
            item {
                Text(
                    text = "দৈনিক ৫ ওয়াক্ত সালাত",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            // List of 5 prayers + Sunrise
            items(prayers) { prayer ->
                val isCurrent = prayer.isWaqtCurrent
                val isNext = prayer.isNext

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prayer_item_${prayer.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) EmeraldPrimary else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrent) 3.dp else 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCurrent) GoldAccent else MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (prayer.id) {
                                        "fajr" -> Icons.Default.Brightness3
                                        "sunrise" -> Icons.Default.WbSunny
                                        "dhuhr" -> Icons.Default.Brightness5
                                        "asr" -> Icons.Default.Brightness6
                                        "maghrib" -> Icons.Default.Brightness4
                                        else -> Icons.Default.NightsStay
                                    },
                                    contentDescription = null,
                                    tint = if (isCurrent) EmeraldDark else MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = prayer.nameBn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isCurrent) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = GoldAccent
                                        ) {
                                            Text(
                                                text = "চলমান",
                                                color = EmeraldDark,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else if (isNext) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = "পরবর্তী",
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = prayer.nameAr,
                                    fontSize = 13.sp,
                                    color = if (isCurrent) GoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = prayer.timeStr,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isCurrent) GoldLight else EmeraldPrimary
                        )
                    }
                }
            }

            // Daily Islamic reminder card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldContainer.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "আজকের কুরআনের বাণী",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EmeraldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "“নিশ্চয়ই নির্ধারিত সময়ে সালাত কায়েম করা মুমিনদের জন্য একটি আবশ্যকীয় ফরজ বিধান।”",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = OnEmeraldContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "— সূরা আন-নিসা, আয়াত: ১০৩",
                            fontSize = 12.sp,
                            color = EmeraldLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // City Selection Dialog
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = {
                Text(text = "আপনার শহর নির্বাচন করুন", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PrayerTimeCalculator.cities.forEach { city ->
                        val isSelected = city.nameEn == selectedCity.nameEn
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCity = city
                                    showCityDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${city.nameBn} (${city.nameEn})",
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary)
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
