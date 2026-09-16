package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DhikrPreset
import com.example.data.sources.PrayerTimeCalculator
import com.example.data.sources.TasbihDataSource
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPreset by remember { mutableStateOf(TasbihDataSource.presets[0]) }
    var currentCount by remember { mutableStateOf(0) }
    var targetCount by remember { mutableStateOf(selectedPreset.target) }
    var completedLaps by remember { mutableStateOf(0) }
    var isVibrationEnabled by remember { mutableStateOf(true) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showPresetDialog by remember { mutableStateOf(false) }

    // Tap button animation
    var isPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "buttonScale"
    )

    fun onCountIncrement() {
        if (isVibrationEnabled) {
            triggerTapHaptic(context)
        }
        val next = currentCount + 1
        if (targetCount > 0 && next >= targetCount) {
            currentCount = 0
            completedLaps += 1
            if (isVibrationEnabled) {
                triggerLapCompletionHaptic(context)
            }
        } else {
            currentCount = next
        }
    }

    fun onCountDecrement() {
        if (currentCount > 0) {
            currentCount -= 1
            if (isVibrationEnabled) {
                triggerTapHaptic(context)
            }
        }
    }

    Scaffold(
        modifier = modifier.testTag("tasbih_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ডিজিটাল তাসবিহ",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "সহজ ও বরকতময় যিকির কাউন্টার",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight
                        )
                    }
                },
                actions = {
                    // Vibration Toggle
                    IconButton(onClick = { isVibrationEnabled = !isVibrationEnabled }) {
                        Icon(
                            imageVector = if (isVibrationEnabled) Icons.Default.Vibration else Icons.Default.Smartphone,
                            contentDescription = "Toggle Vibration",
                            tint = if (isVibrationEnabled) GoldAccent else Color.White.copy(alpha = 0.5f)
                        )
                    }
                    // Reset
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
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
            // Preset Dhikr Selector Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPresetDialog = true }
                    .testTag("dhikr_selector_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedPreset.arabicText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 28.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${selectedPreset.banglaPronunciation} — ${selectedPreset.banglaMeaning}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    FilledTonalButton(
                        onClick = { showPresetDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "পরিবর্তন", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Target selector row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "টার্গেট:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                listOf(33, 99, 100, 0).forEach { target ->
                    val isSelected = targetCount == target
                    val label = if (target == 0) "সীমাহীন" else PrayerTimeCalculator.toBengaliDigits(target.toString())
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            targetCount = target
                            currentCount = 0
                        },
                        label = { Text(text = label, fontSize = 12.sp) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Interactive Tap Counter
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(buttonScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(EmeraldLight, EmeraldPrimary, EmeraldDark)
                        )
                    )
                    .clickable {
                        isPressed = true
                        onCountIncrement()
                        isPressed = false
                    }
                    .testTag("tasbih_tap_button"),
                contentAlignment = Alignment.Center
            ) {
                // Progress circle
                if (targetCount > 0) {
                    val progress = currentCount.toFloat() / targetCount.toFloat()
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(230.dp),
                        color = GoldAccent,
                        trackColor = Color.White.copy(alpha = 0.15f),
                        strokeWidth = 6.dp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = PrayerTimeCalculator.toBengaliDigits(currentCount.toString()),
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    if (targetCount > 0) {
                        Text(
                            text = "/ ${PrayerTimeCalculator.toBengaliDigits(targetCount.toString())}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "গণনা করতে চাপুন",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Counter actions (Decrement & Lap count)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrement button
                OutlinedIconButton(
                    onClick = { onCountDecrement() },
                    enabled = currentCount > 0,
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Minus 1", tint = EmeraldPrimary)
                }

                // Completed Laps badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Loop, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "চক্কর সম্পন্ন: ${PrayerTimeCalculator.toBengaliDigits(completedLaps.toString())}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Reset button
                OutlinedIconButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = EmeraldPrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Virtues / Benefit card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = GoldDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "যিকিরের ফযিলত",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EmeraldPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selectedPreset.virtueBn,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }

    // Preset Selection Dialog
    if (showPresetDialog) {
        AlertDialog(
            onDismissRequest = { showPresetDialog = false },
            title = {
                Text(text = "যিকির নির্বাচন করুন", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    TasbihDataSource.presets.forEach { preset ->
                        val isSelected = preset.id == selectedPreset.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedPreset = preset
                                    targetCount = preset.target
                                    currentCount = 0
                                    showPresetDialog = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) EmeraldContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = preset.arabicText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = EmeraldPrimary
                                )
                                Text(
                                    text = preset.banglaPronunciation,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = preset.banglaMeaning,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPresetDialog = false }) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }

    // Reset Confirm Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("কাউন্টার রিসেট") },
            text = { Text("আপনি কি বর্তমান গণনা এবং চক্কর শূন্য (০) করতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        currentCount = 0
                        completedLaps = 0
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("হ্যাঁ, রিসেট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

private fun triggerTapHaptic(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            @Suppress("DEPRECATION")
            vibrator?.vibrate(30)
        }
    } catch (_: Exception) {}
}

private fun triggerLapCompletionHaptic(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(
                VibrationEffect.createWaveform(longArrayOf(0, 60, 50, 60), -1)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            @Suppress("DEPRECATION")
            vibrator?.vibrate(longArrayOf(0, 60, 50, 60), -1)
        }
    } catch (_: Exception) {}
}
