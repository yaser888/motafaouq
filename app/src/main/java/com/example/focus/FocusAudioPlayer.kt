package com.example.focus

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

enum class FocusSoundType(val arabicName: String, val icon: String) {
    RAIN("صوت المطر الهادئ", "water_drop"),
    ALPHA_WAVES("أمواج ألفا للتركيز (432Hz)", "waves"),
    WHITE_NOISE("ضوضاء بيضاء عازلة", "graphic_eq"),
    DEEP_STUDY("همهمة مكتبة ودراسة عميقة", "menu_book"),
    OFF("صامت", "volume_off")
}

class FocusAudioPlayer {
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val sampleRate = 44100

    fun startSound(soundType: FocusSoundType, scope: CoroutineScope) {
        stopSound()
        if (soundType == FocusSoundType.OFF) return

        playbackJob = scope.launch(Dispatchers.Default) {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()

            val buffer = ShortArray(bufferSize / 2)
            var phase1 = 0.0
            var phase2 = 0.0
            var lastSample = 0.0

            while (isActive) {
                for (i in buffer.indices) {
                    val sample = when (soundType) {
                        FocusSoundType.ALPHA_WAVES -> {
                            // 432 Hz carrier + 10 Hz alpha binaural pulse
                            phase1 += 2.0 * Math.PI * 432.0 / sampleRate
                            phase2 += 2.0 * Math.PI * 10.0 / sampleRate
                            val pulse = (1.0 + 0.3 * sin(phase2))
                            (sin(phase1) * pulse * 8000.0).toInt().toShort()
                        }
                        FocusSoundType.RAIN -> {
                            // Pink noise with soft low-pass filter to simulate gentle falling rain
                            val white = (Random.nextDouble() * 2.0 - 1.0) * 12000.0
                            lastSample = (lastSample * 0.94) + (white * 0.06)
                            lastSample.toInt().coerceIn(-32767, 32767).toShort()
                        }
                        FocusSoundType.WHITE_NOISE -> {
                            // Gentle pure white noise
                            ((Random.nextDouble() * 2.0 - 1.0) * 6000.0).toInt().toShort()
                        }
                        FocusSoundType.DEEP_STUDY -> {
                            // Warm brownian noise (rumble / deep study frequency)
                            val white = (Random.nextDouble() * 2.0 - 1.0) * 16000.0
                            lastSample = (lastSample + (0.02 * white)) / 1.02
                            (lastSample * 3.5).toInt().coerceIn(-32767, 32767).toShort()
                        }
                        FocusSoundType.OFF -> 0.toShort()
                    }
                    buffer[i] = sample
                }
                audioTrack?.write(buffer, 0, buffer.size)
            }
        }
    }

    fun stopSound() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
