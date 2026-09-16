package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentSurahId: Int = 1,
    val currentAyahIndex: Int = 0,
    val currentAyahNumber: Int = 1,
    val totalAyahsInSurah: Int = 7,
    val errorMessage: String? = null
)

class QuranAudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    private var currentPlaylist: List<Pair<Int, String>> = emptyList() // List of (ayahNumber, audioUrl)

    fun playAyah(
        surahId: Int,
        ayahIndex: Int,
        playlist: List<Pair<Int, String>>
    ) {
        if (playlist.isEmpty()) return

        currentPlaylist = playlist
        val validIndex = ayahIndex.coerceIn(0, playlist.size - 1)
        val (ayahNumber, audioUrl) = playlist[validIndex]

        stop()

        _state.value = _state.value.copy(
            isPlaying = false,
            isBuffering = true,
            currentSurahId = surahId,
            currentAyahIndex = validIndex,
            currentAyahNumber = ayahNumber,
            totalAyahsInSurah = playlist.size,
            errorMessage = null
        )

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    _state.value = _state.value.copy(
                        isPlaying = true,
                        isBuffering = false
                    )
                    mp.start()
                }
                setOnCompletionListener {
                    // Auto play next Ayah
                    playNext()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("QuranAudioPlayer", "MediaPlayer error: what=$what, extra=$extra")
                    _state.value = _state.value.copy(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "অডিও প্লে করা সম্ভব হয়নি। ইন্টারনেট সংযোগ পরীক্ষা করুন।"
                    )
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("QuranAudioPlayer", "Failed to start player", e)
            _state.value = _state.value.copy(
                isPlaying = false,
                isBuffering = false,
                errorMessage = "অডিও লোড করতে ত্রুটি ঘটেছে।"
            )
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
            _state.value = _state.value.copy(isPlaying = false)
        } else {
            player.start()
            _state.value = _state.value.copy(isPlaying = true)
        }
    }

    fun playNext() {
        val currentIndex = _state.value.currentAyahIndex
        if (currentIndex + 1 < currentPlaylist.size) {
            playAyah(_state.value.currentSurahId, currentIndex + 1, currentPlaylist)
        } else {
            // Reached end of Surah
            stop()
        }
    }

    fun playPrevious() {
        val currentIndex = _state.value.currentAyahIndex
        if (currentIndex > 0) {
            playAyah(_state.value.currentSurahId, currentIndex - 1, currentPlaylist)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
        } catch (e: Exception) {
            Log.e("QuranAudioPlayer", "Error stopping player", e)
        } finally {
            mediaPlayer = null
            _state.value = _state.value.copy(
                isPlaying = false,
                isBuffering = false
            )
        }
    }

    fun release() {
        stop()
    }
}
