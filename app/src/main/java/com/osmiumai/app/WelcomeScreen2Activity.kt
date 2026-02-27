package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.osmiumai.app.databinding.ActivityWelcomeScreen2Binding

class WelcomeScreen2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreen2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        
        binding = ActivityWelcomeScreen2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinue.setOnClickListener {
            startActivity(Intent(this, WelcomeScreen3Activity::class.java))
            finish()
        }
        
        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this, WelcomeScreen3Activity::class.java))
            finish()
        }
    }
}
