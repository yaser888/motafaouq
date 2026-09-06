package com.example.focus

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class ArabicTtsPlayer(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var onSpeechDoneCallback: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default locale
                tts?.language = Locale.getDefault()
            }
            tts?.setSpeechRate(1.0f)
            tts?.setPitch(1.0f)
            isInitialized = true

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    onSpeechDoneCallback?.invoke()
                }
                override fun onError(utteranceId: String?) {}
            })
        } else {
            Log.e("ArabicTtsPlayer", "Initialization of TextToSpeech failed")
        }
    }

    fun speak(text: String, speechRate: Float = 1.0f, onDone: (() -> Unit)? = null) {
        if (!isInitialized) return
        onSpeechDoneCallback = onDone
        tts?.stop()
        tts?.setSpeechRate(speechRate)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_CARD")
    }

    fun stop() {
        tts?.stop()
    }

    fun isSpeaking(): Boolean {
        return tts?.isSpeaking ?: false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
