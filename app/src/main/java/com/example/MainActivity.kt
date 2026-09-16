package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AdhanAudioPlayer
import com.example.audio.QuranAudioPlayer
import com.example.sensors.QiblaManager
import com.example.ui.screens.DailyDuaScreen
import com.example.ui.screens.HadithScreen
import com.example.ui.screens.PrayerTimesScreen
import com.example.ui.screens.QiblaScreen
import com.example.ui.screens.QuranScreen
import com.example.ui.screens.TasbihScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MyApplicationTheme

enum class IslamicTab(val titleBn: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    PRAYER("সময়সূচী", Icons.Default.AccessTime),
    QURAN("কুরআন", Icons.Default.MenuBook),
    DUA("দোয়া", Icons.Default.VolunteerActivism),
    HADITH("হাদিস", Icons.Default.LibraryBooks),
    QIBLA("কিবলা", Icons.Default.Explore),
    TASBIH("তাসবিহ", Icons.Default.Fingerprint)
}

class MainActivity : ComponentActivity() {

    private lateinit var audioPlayer: QuranAudioPlayer
    private lateinit var adhanPlayer: AdhanAudioPlayer
    private lateinit var qiblaManager: QiblaManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        audioPlayer = QuranAudioPlayer(applicationContext)
        adhanPlayer = AdhanAudioPlayer(applicationContext)
        qiblaManager = QiblaManager(applicationContext)

        setContent {
            MyApplicationTheme {
                IslamicAppRoot(
                    audioPlayer = audioPlayer,
                    adhanPlayer = adhanPlayer,
                    qiblaManager = qiblaManager
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.release()
        adhanPlayer.release()
        qiblaManager.stopListening()
    }
}

@Composable
fun IslamicAppRoot(
    audioPlayer: QuranAudioPlayer,
    adhanPlayer: AdhanAudioPlayer,
    qiblaManager: QiblaManager,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(IslamicTab.PRAYER) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("islamic_app_root"),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                IslamicTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.titleBn
                            )
                        },
                        label = {
                            Text(
                                text = tab.titleBn,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        when (currentTab) {
            IslamicTab.PRAYER -> PrayerTimesScreen(
                onNavigateToQibla = { currentTab = IslamicTab.QIBLA },
                adhanPlayer = adhanPlayer,
                modifier = screenModifier
            )
            IslamicTab.QURAN -> QuranScreen(
                audioPlayer = audioPlayer,
                modifier = screenModifier
            )
            IslamicTab.DUA -> DailyDuaScreen(
                modifier = screenModifier
            )
            IslamicTab.HADITH -> HadithScreen(
                modifier = screenModifier
            )
            IslamicTab.QIBLA -> QiblaScreen(
                qiblaManager = qiblaManager,
                modifier = screenModifier
            )
            IslamicTab.TASBIH -> TasbihScreen(
                modifier = screenModifier
            )
        }
    }
}

// Retained for tests and previews
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
