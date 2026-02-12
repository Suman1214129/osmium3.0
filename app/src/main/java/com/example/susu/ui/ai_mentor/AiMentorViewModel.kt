package com.example.susu.ui.ai_mentor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AiMentorViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is AI Mentor Fragment"
    }
    val text: LiveData<String> = _text
}