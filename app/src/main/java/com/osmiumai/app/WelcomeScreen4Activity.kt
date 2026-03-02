package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.osmiumai.app.databinding.ActivityWelcomeScreen4Binding

class WelcomeScreen4Activity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreen4Binding
    private lateinit var googleAuthHelper: GoogleAuthHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        
        binding = ActivityWelcomeScreen4Binding.inflate(layoutInflater)
        setContentView(binding.root)
        
        googleAuthHelper = GoogleAuthHelper(this)

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
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
    
    private fun signInWithGoogle() {
        Toast.makeText(this, "Initiating Google Sign-In...", Toast.LENGTH_SHORT).show()
        
        googleAuthHelper.signInWithGoogle(
            onSuccess = { credential ->
                val email = credential.id
                val displayName = credential.displayName
                val profilePicUrl = credential.profilePictureUri?.toString()
                
                val prefs = getSharedPreferences("OsmiumPrefs", MODE_PRIVATE)
                prefs.edit().apply {
                    putString("user_email", email)
                    putString("user_name", displayName)
                    putString("user_profile_pic", profilePicUrl)
                    putBoolean("is_logged_in", true)
                    putString("login_method", "google")
                    apply()
                }
                
                Toast.makeText(this, "Welcome, $displayName!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },
            onError = { error ->
                Toast.makeText(this, "Sign-in failed: $error\n\nPlease configure Web Client ID in GoogleAuthHelper.kt", Toast.LENGTH_LONG).show()
            }
        )
    }
}
