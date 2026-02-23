package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.databinding.ActivityWelcomeScreen2Binding

class WelcomeScreen2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreen2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        
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
