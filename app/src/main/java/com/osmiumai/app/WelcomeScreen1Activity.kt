package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.databinding.ActivityWelcomeScreen1Binding

class WelcomeScreen1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreen1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        
        binding = ActivityWelcomeScreen1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinue.setOnClickListener {
            startActivity(Intent(this, WelcomeScreen2Activity::class.java))
            finish()
        }
        
        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this, WelcomeScreen2Activity::class.java))
            finish()
        }
    }
}
