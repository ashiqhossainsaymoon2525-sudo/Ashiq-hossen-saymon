package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlayerState
import com.example.audio.QuranAudioPlayer
import com.example.data.models.Ayah
import com.example.data.models.Surah
import com.example.data.sources.PrayerTimeCalculator
import com.example.data.sources.QuranDataSource
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    audioPlayer: QuranAudioPlayer,
    modifier: Modifier = Modifier
) {
    var selectedSurahId by remember { mutableStateOf<Int?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val playerState by audioPlayer.state.collectAsState()

    if (selectedSurahId != null) {
        val surah = remember(selectedSurahId) {
            QuranDataSource.getSurahById(selectedSurahId!!)
        }
        SurahDetailView(
            surah = surah,
            audioPlayer = audioPlayer,
            playerState = playerState,
            onBack = { selectedSurahId = null },
            modifier = modifier
        )
    } else {
        SurahListView(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSurahSelected = { id -> selectedSurahId = id },
            playerState = playerState,
            audioPlayer = audioPlayer,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SurahListView(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSurahSelected: (Int) -> Unit,
    playerState: PlayerState,
    audioPlayer: QuranAudioPlayer,
    modifier: Modifier = Modifier
) {
    val allSurahs = QuranDataSource.surahIndexList

    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allSurahs
        } else {
            val q = searchQuery.trim().lowercase()
            allSurahs.filter { (id, meta) ->
                id.toString().contains(q) ||
                meta.first.lowercase().contains(q) ||
                meta.second.contains(q) ||
                PrayerTimeCalculator.toBengaliDigits(id.toString()).contains(q)
            }
        }
    }

    Scaffold(
        modifier = modifier.testTag("quran_list_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "পবিত্র কুরআনুল কারীম",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "তেলাওয়াত ও প্রতিটি আয়াতের বাংলা অর্থ",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldPrimary)
            )
        },
        bottomBar = {
            if (playerState.isPlaying || playerState.isBuffering) {
                CompactAudioPlayerBar(playerState = playerState, audioPlayer = audioPlayer)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("surah_search_input"),
                placeholder = { Text("সূরা খুঁজুন (নাম বা নম্বর দিয়ে)...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Surah List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredSurahs, key = { it.first }) { (id, meta) ->
                    val isPreloaded = QuranDataSource.allSurahs.any { it.id == id }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSurahSelected(id) }
                            .testTag("surah_item_$id"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Number badge
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = PrayerTimeCalculator.toBengaliDigits(id.toString()),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = meta.first,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isPreloaded) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = GoldLight.copy(alpha = 0.6f)
                                            ) {
                                                Text(
                                                    text = "অডিওসহ",
                                                    fontSize = 10.sp,
                                                    color = EmeraldDark,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = meta.third,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = meta.second,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SurahDetailView(
    surah: Surah,
    audioPlayer: QuranAudioPlayer,
    playerState: PlayerState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var copiedAyahMessage by remember { mutableStateOf<String?>(null) }

    // Prepare audio playlist for this Surah
    val playlist = remember(surah) {
        surah.ayahs.map { Pair(it.numberInSurah, it.audioUrl) }
    }

    // Auto-scroll when active ayah plays
    LaunchedEffect(playerState.currentAyahIndex, playerState.currentSurahId) {
        if (playerState.currentSurahId == surah.id && playerState.currentAyahIndex in surah.ayahs.indices) {
            coroutineScope.launch {
                listState.animateScrollToItem(playerState.currentAyahIndex + 1) // +1 for header
            }
        }
    }

    Scaffold(
        modifier = modifier.testTag("surah_detail_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${surah.nameBangla} (${surah.nameArabic})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${surah.revelationType} • ${PrayerTimeCalculator.toBengaliDigits(surah.totalVerses.toString())} আয়াত",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Play whole Surah button
                    IconButton(
                        onClick = {
                            if (playerState.isPlaying && playerState.currentSurahId == surah.id) {
                                audioPlayer.togglePlayPause()
                            } else {
                                audioPlayer.playAyah(surah.id, 0, playlist)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (playerState.isPlaying && playerState.currentSurahId == surah.id) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = "Play Surah",
                            tint = GoldAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldPrimary)
            )
        },
        bottomBar = {
            FullAudioPlayerBar(
                surah = surah,
                playerState = playerState,
                audioPlayer = audioPlayer,
                playlist = playlist
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Surah Header Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = surah.nameArabic,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${surah.nameBangla} — ${surah.banglaMeaning}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnEmeraldContainer,
                                textAlign = TextAlign.Center
                            )

                            if (surah.id != 9) { // Surah At-Tawbah does not have Bismillah
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                ) {
                                    Text(
                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Ayahs with Bengali Translation & Audio Button
                items(surah.ayahs.size) { index ->
                    val ayah = surah.ayahs[index]
                    val isCurrentlyPlaying = playerState.isPlaying &&
                            playerState.currentSurahId == surah.id &&
                            playerState.currentAyahNumber == ayah.numberInSurah

                    AyahCard(
                        ayah = ayah,
                        isPlaying = isCurrentlyPlaying,
                        onPlayClick = {
                            if (isCurrentlyPlaying) {
                                audioPlayer.togglePlayPause()
                            } else {
                                audioPlayer.playAyah(surah.id, index, playlist)
                            }
                        },
                        onCopy = {
                            val copyText = "${ayah.arabicText}\n\nউচ্চারণ: ${ayah.banglaPronunciation}\n\nঅনুবাদ: ${ayah.banglaTranslation}\n[সূরা ${surah.nameBangla}, আয়াত: ${ayah.numberInSurah}]"
                            clipboardManager.setText(AnnotatedString(copyText))
                            copiedAyahMessage = "আয়াত ${ayah.numberInSurah} কপি করা হয়েছে!"
                        }
                    )
                }
            }

            // Snackbar for copy
            AnimatedVisibility(
                visible = copiedAyahMessage != null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldDark,
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = copiedAyahMessage ?: "",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AyahCard(
    ayah: Ayah,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ayah_card_${ayah.numberInSurah}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) EmeraldContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isPlaying) ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GoldAccent, EmeraldPrimary))) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlaying) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Bar of Ayah: Ayah number & Action icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ayah badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "আয়াত: ${PrayerTimeCalculator.toBengaliDigits(ayah.numberInSurah.toString())}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPlaying) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Copy Ayah
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Play Ayah button
                    IconButton(
                        onClick = onPlayClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = "Play Audio",
                            tint = if (isPlaying) EmeraldPrimary else GoldDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic text (Right-aligned, bold, large calligraphy style)
            Text(
                text = ayah.arabicText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                lineHeight = 38.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            // Bengali pronunciation
            Text(
                text = "উচ্চারণ: ${ayah.banglaPronunciation}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Bengali translation
            Text(
                text = "অনুবাদ: ${ayah.banglaTranslation}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun FullAudioPlayerBar(
    surah: Surah,
    playerState: PlayerState,
    audioPlayer: QuranAudioPlayer,
    playlist: List<Pair<Int, String>>
) {
    if (!playerState.isPlaying && !playerState.isBuffering) return

    Surface(
        color = EmeraldDark,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "তেলাওয়াত চলছে: ${surah.nameBangla}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "কারী মিশারি রাশিদ আল-আফাসী • আয়াত ${PrayerTimeCalculator.toBengaliDigits(playerState.currentAyahNumber.toString())}/${PrayerTimeCalculator.toBengaliDigits(playerState.totalAyahsInSurah.toString())}",
                        color = GoldLight,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { audioPlayer.playPrevious() }) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White)
                    }

                    if (playerState.isBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = GoldAccent,
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { audioPlayer.togglePlayPause() }) {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                                contentDescription = "Play/Pause",
                                tint = GoldAccent,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }

                    IconButton(onClick = { audioPlayer.playNext() }) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White)
                    }

                    IconButton(onClick = { audioPlayer.stop() }) {
                        Icon(Icons.Default.Close, contentDescription = "Stop", tint = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactAudioPlayerBar(
    playerState: PlayerState,
    audioPlayer: QuranAudioPlayer
) {
    Surface(
        color = EmeraldDark,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "সূরা ${playerState.currentSurahId} • আয়াত ${PrayerTimeCalculator.toBengaliDigits(playerState.currentAyahNumber.toString())}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { audioPlayer.togglePlayPause() }) {
                    Icon(
                        imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = GoldAccent
                    )
                }
                IconButton(onClick = { audioPlayer.stop() }) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }
}
