package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.osmiumai.app.databinding.ActivityWelcomeScreen4Binding

class WelcomeScreen4Activity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreen4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        
        binding = ActivityWelcomeScreen4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoogle.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
        
        binding.btnEmail.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
        
        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
