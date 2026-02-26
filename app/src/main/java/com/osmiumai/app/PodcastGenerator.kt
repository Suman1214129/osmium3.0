package com.osmiumai.app

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class PodcastSegment(val text: String, val speaker: String)

class PodcastGenerator(private val context: Context, private val ttsService: SarvamTTSService) {
    
    val segmentDurations = mutableListOf<Int>()
    
    suspend fun generatePodcast(segments: List<PodcastSegment>): Result<File> = withContext(Dispatchers.IO) {
        try {
            val audioFiles = mutableListOf<File>()
            segmentDurations.clear()
            
            for (segment in segments) {
                val result = ttsService.generateAndPlayAudio(segment.text, segment.speaker)
                result.onSuccess { file ->
                    audioFiles.add(file)
                    // Get duration of this segment
                    val mp = MediaPlayer()
                    mp.setDataSource(file.absolutePath)
                    mp.prepare()
                    segmentDurations.add(mp.duration)
                    mp.release()
                }.onFailure { error ->
                    return@withContext Result.failure(error)
                }
            }
            
            val combinedFile = combineAudioFiles(audioFiles)
            Result.success(combinedFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun combineAudioFiles(files: List<File>): File {
        val outputFile = File(context.cacheDir, "podcast_combined_${System.currentTimeMillis()}.mp3")
        FileOutputStream(outputFile).use { output ->
            files.forEach { file ->
                file.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
        }
        return outputFile
    }
}
