package com.osmiumai.app

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class SarvamTTSService(private val context: Context) {
    
    private val apiKey = "sk_fc981jv2_3450PdOltskQKdBH5pHcW4qx"
    private val apiUrl = "https://api.sarvam.ai/text-to-speech/stream"
    private var mediaPlayer: MediaPlayer? = null
    
    suspend fun generateAndPlayAudio(text: String, speaker: String = "shubh", onProgress: (Int) -> Unit = {}): Result<File> = withContext(Dispatchers.IO) {
        try {
            val connection = URL(apiUrl).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("api-subscription-key", apiKey)
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            val payload = JSONObject().apply {
                put("text", text)
                put("target_language_code", "en-IN")
                put("speaker", speaker)
                put("model", "bulbul:v3")
                put("pace", if (speaker == "roopa") 1.1 else 1.11)
                put("speech_sample_rate", 22050)
                put("output_audio_codec", "mp3")
                put("enable_preprocessing", true)
            }
            
            connection.outputStream.use { it.write(payload.toString().toByteArray()) }
            
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val outputFile = File(context.cacheDir, "podcast_${speaker}_${System.currentTimeMillis()}.mp3")
                val totalSize = connection.contentLength
                var downloaded = 0
                
                FileOutputStream(outputFile).use { output ->
                    connection.inputStream.use { input ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloaded += bytesRead
                            if (totalSize > 0) {
                                withContext(Dispatchers.Main) {
                                    onProgress((downloaded * 100 / totalSize))
                                }
                            }
                        }
                    }
                }
                Result.success(outputFile)
            } else {
                Result.failure(Exception("API Error: ${connection.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun playAudio(file: File, onCompletion: () -> Unit = {}) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            setOnCompletionListener { onCompletion() }
            start()
        }
    }
    
    fun pauseAudio() {
        mediaPlayer?.pause()
    }
    
    fun resumeAudio() {
        mediaPlayer?.start()
    }
    
    fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
    
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false
    
    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0
    
    fun getDuration(): Int = mediaPlayer?.duration ?: 0
    
    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }
    
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
