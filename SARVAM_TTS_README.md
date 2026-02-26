# Sarvam AI Text-to-Speech Integration

## Overview
This integration adds text-to-speech functionality to the podcast section using Sarvam AI's TTS API.

## Setup Instructions

### 1. Get Your API Key
- Sign up at [Sarvam AI](https://www.sarvam.ai/)
- Get your API subscription key from the dashboard

### 2. Configure API Key
Open `SarvamTTSService.kt` and replace `YOUR_API_KEY` with your actual API key:

```kotlin
private val apiKey = "YOUR_ACTUAL_API_KEY_HERE"
```

### 3. Build and Run
- Sync Gradle dependencies
- Build the project
- Run on your device or emulator

## Features

### Audio Generation
- Converts podcast text to speech using Sarvam AI's Bulbul v3 model
- Supports English (Indian) language
- Uses "shubh" speaker voice
- Configurable pace (default: 1.11x)
- Output format: MP3 at 22050 Hz

### Playback Controls
- **Play/Pause**: Tap the play button in the podcast player
- **Resume**: Automatically resumes from where you paused
- **Auto-completion**: Resets to play state when audio finishes

## How It Works

1. When you open the podcast tab, the app automatically generates audio from the transcript text
2. The audio is streamed from Sarvam AI API and saved to cache
3. Once generated, you can play/pause the audio using the player controls
4. The audio file is cached for quick replay

## API Configuration

You can customize the TTS settings in `SarvamTTSService.kt`:

```kotlin
val payload = JSONObject().apply {
    put("text", text)
    put("target_language_code", "en-IN")  // Language
    put("speaker", "shubh")                // Voice
    put("model", "bulbul:v3")              // Model
    put("pace", 1.11)                      // Speed
    put("speech_sample_rate", 22050)       // Sample rate
    put("output_audio_codec", "mp3")       // Format
    put("enable_preprocessing", true)      // Text preprocessing
}
```

## Available Options

### Speakers
- shubh (male)
- Other speakers available in Sarvam AI documentation

### Languages
- en-IN (English - India)
- hi-IN (Hindi - India)
- Other Indian languages supported by Sarvam AI

### Pace
- Range: 0.5 to 2.0
- Default: 1.11 (slightly faster than normal)

## Troubleshooting

### Audio Not Playing
- Check internet connection
- Verify API key is correct
- Check logcat for error messages

### API Errors
- Ensure you have sufficient API credits
- Check API rate limits
- Verify the API endpoint is accessible

## File Structure

```
app/src/main/java/com/osmiumai/app/
├── SarvamTTSService.kt          # TTS service implementation
└── TopicLearnActivity.kt        # Activity with podcast integration

app/src/main/res/
├── layout/
│   └── content_podcast.xml      # Podcast UI layout
└── drawable/
    └── ic_pause.xml             # Pause icon
```

## Dependencies

The following dependency is added to `build.gradle.kts`:
```kotlin
implementation("com.squareup.okhttp3:okhttp:4.12.0")
```

## Notes

- Audio files are cached in the app's cache directory
- Cache is automatically cleared when the app is uninstalled
- The service releases media player resources when the activity is destroyed
- Internet permission is required (already added in AndroidManifest.xml)
