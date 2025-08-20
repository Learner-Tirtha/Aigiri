//package com.example.aigiri.service
//
//import android.content.Context
//import android.media.MediaPlayer
//import android.media.AudioManager
//import android.speech.tts.TextToSpeech
//import com.example.aigiri.model.Preset
//import com.example.aigiri.model.VoiceType
//import com.example.aigiri.model.ConversationData
//import com.example.aigiri.model.relationshipType
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import java.util.*
//
//class VoiceConversationService(
//    private val context: Context,
//    private val preset: Preset,
//    private val maxDurationSeconds: Int
//) {
//    private var tts: TextToSpeech? = null
//    private var isInitialized = false
//
//    fun initializeTTS(onReady: () -> Unit) {
//        tts = TextToSpeech(context) { status ->
//            if (status == TextToSpeech.SUCCESS) {
//                setupVoice()
//                isInitialized = true
//                onReady()
//            }
//        }
//    }
//
//    private fun setupVoice() {
//        tts?.let { tts ->
//            tts.language = Locale.US
//            when (preset.voiceType) {
//                VoiceType.MALE -> {
//                    tts.setPitch(0.8f)
//                    tts.setSpeechRate(0.9f)
//                }
//                VoiceType.FEMALE -> {
//                    tts.setPitch(1.2f)
//                    tts.setSpeechRate(1.0f)
//                }
//                VoiceType.CHILD -> {
//                    tts.setPitch(1.5f)
//                    tts.setSpeechRate(1.1f)
//                }
//            }
//        }
//    }
//
//    fun startConversation(onConversationEnd: () -> Unit) {
//        if (!isInitialized) return
//
//        val conversationLines = ConversationData.conversations[preset.relationship]?.ConversationData.conversations[relationshipType.Friend]
//
//        GlobalScope.launch {
//            val startTime = System.currentTimeMillis()
//
//            for ((index, line) in conversationLines.withIndex()) {
//                val currentTime = System.currentTimeMillis()
//                val elapsed = (currentTime - startTime) / 1000
//
//                if (elapsed >= maxDurationSeconds) {
//                    break
//                }
//
//                delay(if (index == 0) 1000 else 0)
//
//                tts?.speak(line.text, TextToSpeech.QUEUE_FLUSH, null, "line_$index")
//                delay(line.delayAfter)
//
//                val newElapsed = (System.currentTimeMillis() - startTime) / 1000
//                if (newElapsed >= maxDurationSeconds) {
//                    break
//                }
//            }
//            onConversationEnd()
//        }
//    }
//
//    fun speak(text: String) {
//        if (isInitialized) {
//            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
//        }
//    }
//
//    fun stop() {
//        tts?.stop()
//    }
//
//    fun shutdown() {
//        tts?.stop()
//        tts?.shutdown()
//        tts = null
//        isInitialized = false
//    }
//}
//
//class AudioService(private val context: Context) {
//    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//
//    fun adjustVolume(isSpeakerOn: Boolean) {
//        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
//        val targetVolume = if (isSpeakerOn) {
//            (maxVolume * 0.9f).toInt()
//        } else {
//            (maxVolume * 0.5f).toInt()
//        }
//
//        audioManager.setStreamVolume(
//            AudioManager.STREAM_VOICE_CALL,
//            targetVolume,
//            0
//        )
//
//        val musicMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
//        val musicTargetVolume = if (isSpeakerOn) {
//            (musicMaxVolume * 0.95f).toInt()
//        } else {
//            (musicMaxVolume * 0.6f).toInt()
//        }
//
//        audioManager.setStreamVolume(
//            AudioManager.STREAM_MUSIC,
//            musicTargetVolume,
//            0
//        )
//    }
//
//    fun playRingtone() {
//        try {
//            val ringtoneUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_RINGTONE)
//            val ringtone = android.media.RingtoneManager.getRingtone(context, ringtoneUri)
//            ringtone?.play()
//
//            GlobalScope.launch {
//                delay(3000)
//                ringtone?.stop()
//            }
//        } catch (e: Exception) {
//            // Handle exception silently
//        }
//    }
//
//    fun resetVolume() {
//        audioManager.setStreamVolume(
//            AudioManager.STREAM_VOICE_CALL,
//            audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) / 2,
//            0
//        )
//    }
//}