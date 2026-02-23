package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.databinding.ActivityWelcomeScreen3Binding

class WelcomeScreen3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreen3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        
        binding = ActivityWelcomeScreen3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinue.setOnClickListener {
            startActivity(Intent(this, WelcomeScreen4Activity::class.java))
            finish()
        }
        
        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this, WelcomeScreen4Activity::class.java))
            finish()
        }
    }
}
