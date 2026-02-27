package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.osmiumai.app.databinding.ActivityTestAnalyticsBinding

class TestAnalyticsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTestAnalyticsBinding
    private var isQuestion1Expanded = true
    private var isQuestion2Expanded = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        
        binding.backButton.setOnClickListener {
            finish()
        }
        
        binding.askMentorButton1.setOnClickListener {
            navigateToAiMentor()
        }
        
        binding.askMentorButton2.setOnClickListener {
            navigateToAiMentor()
        }
        
        setupDropdownListeners()
    }
    
    private fun setupDropdownListeners() {
        binding.question1Header.setOnClickListener {
            toggleQuestion1Details()
        }
        
        binding.question2Header.setOnClickListener {
            toggleQuestion2Details()
        }
    }
    
    private fun toggleQuestion1Details() {
        if (isQuestion1Expanded) {
            binding.question1Details.visibility = View.GONE
            rotateIcon(binding.question1DropdownIcon, 180f, 0f)
        } else {
            binding.question1Details.visibility = View.VISIBLE
            rotateIcon(binding.question1DropdownIcon, 0f, 180f)
        }
        isQuestion1Expanded = !isQuestion1Expanded
    }
    
    private fun toggleQuestion2Details() {
        if (isQuestion2Expanded) {
            binding.question2Details.visibility = View.GONE
            rotateIcon(binding.question2DropdownIcon, 180f, 0f)
        } else {
            binding.question2Details.visibility = View.VISIBLE
            rotateIcon(binding.question2DropdownIcon, 0f, 180f)
        }
        isQuestion2Expanded = !isQuestion2Expanded
    }
    
    private fun rotateIcon(view: View, fromDegrees: Float, toDegrees: Float) {
        val rotateAnimation = RotateAnimation(
            fromDegrees, toDegrees,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 200
        rotateAnimation.fillAfter = true
        view.startAnimation(rotateAnimation)
    }
    
    private fun navigateToAiMentor() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("navigate_to", "ai_mentor")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}
