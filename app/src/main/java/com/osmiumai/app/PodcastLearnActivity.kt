package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PodcastLearnActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Redirect to TopicLearnActivity with podcast tab selected
        val intent = Intent(this, TopicLearnActivity::class.java)
        intent.putExtra("SELECTED_TAB", "podcast")
        startActivity(intent)
        finish()
    }
}
