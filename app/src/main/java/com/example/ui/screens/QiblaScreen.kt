package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CityLocation
import com.example.data.sources.PrayerTimeCalculator
import com.example.sensors.QiblaManager
import com.example.ui.theme.*
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    qiblaManager: QiblaManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val qiblaState by qiblaManager.state.collectAsState()
    var selectedCity by remember { mutableStateOf(PrayerTimeCalculator.cities[0]) }
    var showCityDialog by remember { mutableStateOf(false) }

    // Start/Stop listening to sensors
    DisposableEffect(Unit) {
        qiblaManager.startListening()
        onDispose {
            qiblaManager.stopListening()
        }
    }

    // Update location when selected city changes
    LaunchedEffect(selectedCity) {
        qiblaManager.updateLocation(selectedCity.latitude, selectedCity.longitude)
    }

    // Haptic vibration when facing Qibla
    var lastVibratedState by remember { mutableStateOf(false) }
    LaunchedEffect(qiblaState.isFacingQibla) {
        if (qiblaState.isFacingQibla && !lastVibratedState) {
            triggerHapticFeedback(context)
        }
        lastVibratedState = qiblaState.isFacingQibla
    }

    Scaffold(
        modifier = modifier.testTag("qibla_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "কিবলা কম্পাস",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "পবিত্র কাবা শরীফের দিক নির্ণয়",
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
                        modifier = Modifier.testTag("qibla_city_button")
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = selectedCity.nameBn, fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Banner
            val statusBgColor by animateColorAsState(
                targetValue = if (qiblaState.isFacingQibla) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                label = "statusBg"
            )
            val statusTextColor = if (qiblaState.isFacingQibla) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("qibla_status_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = statusBgColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (qiblaState.isFacingQibla) Icons.Default.CheckCircle else Icons.Default.Explore,
                        contentDescription = null,
                        tint = if (qiblaState.isFacingQibla) GoldAccent else EmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (qiblaState.isFacingQibla) "মাশাআল্লাহ! আপনি এখন সরাসরি কিবলামুখী।" else "কিবলামুখী হতে তীর চিহ্নের দিকে ফোন ঘোরান",
                        color = statusTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Compass Dial Box
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .testTag("compass_dial_box"),
                contentAlignment = Alignment.Center
            ) {
                // Background outer ring and cardinal markers (Rotates opposite to heading)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(-qiblaState.azimuth)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.width / 2f - 16.dp.toPx()

                    // Outer circle
                    drawCircle(
                        color = EmeraldDark.copy(alpha = 0.15f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Degree ticks
                    for (deg in 0 until 360 step 15) {
                        val rad = Math.toRadians(deg.toDouble())
                        val isMajor = deg % 90 == 0
                        val tickLen = if (isMajor) 14.dp.toPx() else 8.dp.toPx()
                        val p1 = Offset(
                            x = center.x + (radius - tickLen) * sin(rad).toFloat(),
                            y = center.y - (radius - tickLen) * cos(rad).toFloat()
                        )
                        val p2 = Offset(
                            x = center.x + radius * sin(rad).toFloat(),
                            y = center.y - radius * cos(rad).toFloat()
                        )
                        drawLine(
                            color = if (isMajor) EmeraldPrimary else Color.Gray.copy(alpha = 0.5f),
                            start = p1,
                            end = p2,
                            strokeWidth = if (isMajor) 2.5.dp.toPx() else 1.dp.toPx()
                        )
                    }
                }

                // Kaaba Qibla Indicator (Rotates to relativeAngle)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(qiblaState.relativeAngle)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.width / 2f - 16.dp.toPx()

                    // Glowing Qibla needle pointing upward
                    val path = Path().apply {
                        moveTo(center.x, center.y - radius + 10.dp.toPx())
                        lineTo(center.x - 14.dp.toPx(), center.y - 30.dp.toPx())
                        lineTo(center.x + 14.dp.toPx(), center.y - 30.dp.toPx())
                        close()
                    }
                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            listOf(
                                if (qiblaState.isFacingQibla) EmeraldPrimary else GoldAccent,
                                if (qiblaState.isFacingQibla) EmeraldLight else GoldDark
                            )
                        )
                    )

                    // Opposite tail needle
                    val tailPath = Path().apply {
                        moveTo(center.x, center.y + radius - 10.dp.toPx())
                        lineTo(center.x - 8.dp.toPx(), center.y + 25.dp.toPx())
                        lineTo(center.x + 8.dp.toPx(), center.y + 25.dp.toPx())
                        close()
                    }
                    drawPath(
                        path = tailPath,
                        color = Color.LightGray.copy(alpha = 0.7f)
                    )
                }

                // Center Kaaba Emblem & Alignment Circle
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            if (qiblaState.isFacingQibla) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Kaaba icon representation
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black,
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(GoldAccent, GoldLight))
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .background(GoldAccent)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ক্বাবা",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Angle details & Location Info Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "কিবলার দিক", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${PrayerTimeCalculator.toBengaliDigits(qiblaState.qiblaBearing.roundToInt().toString())}°",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                        Text(text = "উত্তর থেকে পশ্চিম", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "ডিভাইসের অভিমুখ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${PrayerTimeCalculator.toBengaliDigits(qiblaState.azimuth.roundToInt().toString())}°",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (qiblaState.isFacingQibla) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = qiblaState.accuracyStatus,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guidance / Calibration helper card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "নির্ভুল দিক পেতে ফোনটি সমতল স্থানে রাখুন এবং ধাতব বস্তু বা চুম্বক থেকে দূরে রাখুন। সেন্সর ক্যালিব্রেট করতে ফোনটি বাতাসে ৮ (figure-8) আকৃতিতে ঘোরান।",
                        fontSize = 12.sp,
                        color = OnEmeraldContainer,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    // City Selection Dialog
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = {
                Text(text = "কিবলার শহর নির্বাচন করুন", fontWeight = FontWeight.Bold)
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

private fun triggerHapticFeedback(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            @Suppress("DEPRECATION")
            vibrator?.vibrate(80)
        }
    } catch (_: Exception) {
    }
}
