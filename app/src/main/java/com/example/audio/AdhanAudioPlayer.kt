package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdhanPlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentWaqtBn: String = "",
    val reciterNameBn: String = "মক্কা মুকাররমা",
    val errorMessage: String? = null
)

class AdhanAudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    private val _state = MutableStateFlow(AdhanPlayerState())
    val state: StateFlow<AdhanPlayerState> = _state.asStateFlow()

    // Authentic High Quality Adhan Audio Stream URLs (Makkah & Madinah Adhan)
    private val makkahAdhanUrl = "https://cdn.aladhan.com/audio/adhans/makkah.mp3"
    private val madinahAdhanUrl = "https://cdn.aladhan.com/audio/adhans/madinah.mp3"
    private val fajrAdhanUrl = "https://cdn.aladhan.com/audio/adhans/fajr.mp3"

    fun playAdhan(waqtBn: String, isFajr: Boolean = false, reciterName: String = "মক্কা মুকাররমা") {
        stop()

        val audioUrl = when {
            isFajr || waqtBn.contains("ফজর") -> fajrAdhanUrl
            reciterName.contains("মদিনা") -> madinahAdhanUrl
            else -> makkahAdhanUrl
        }

        _state.value = _state.value.copy(
            isPlaying = false,
            isBuffering = true,
            currentWaqtBn = waqtBn,
            reciterNameBn = reciterName,
            errorMessage = null
        )

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
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
                    stop()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AdhanAudioPlayer", "MediaPlayer error: what=$what, extra=$extra")
                    _state.value = _state.value.copy(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "আজান বাজাতে সমস্যা হয়েছে। ইন্টারনেট সংযোগ পরীক্ষা করুন।"
                    )
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("AdhanAudioPlayer", "Failed to start Adhan player", e)
            _state.value = _state.value.copy(
                isPlaying = false,
                isBuffering = false,
                errorMessage = "আজান লোড করতে সমস্যা হয়েছে।"
            )
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
            Log.e("AdhanAudioPlayer", "Error stopping adhan player", e)
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
