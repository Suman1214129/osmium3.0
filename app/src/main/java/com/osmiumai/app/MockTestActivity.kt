package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.osmiumai.app.databinding.ActivityMockTestBinding

class MockTestActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMockTestBinding
    private var countDownTimer: CountDownTimer? = null
    private val testDurationMillis = 3 * 60 * 60 * 1000L // 3 hours in milliseconds
    private var selectedOption: Int = -1 // -1 means no option selected, 0=A, 1=B, 2=C, 3=D
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMockTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Hide action bar
        supportActionBar?.hide()
        
        setupUI()
        setupTabClickListeners()
        setupOptionClickListeners()
        startTimer()
    }
    
    private fun setupUI() {
        binding.submitButton.setOnClickListener {
            startActivity(Intent(this, TestAnalyticsActivity::class.java))
        }
        
        binding.previousButton.setOnClickListener {
            // Handle previous question
        }
        
        binding.nextButton.setOnClickListener {
            // Handle next question
        }
        
        binding.menuIcon.setOnClickListener {
            toggleSidebar()
        }
        
        // Close sidebar when clicking outside (on the root layout)
        binding.root.setOnClickListener {
            if (binding.sidebar.translationX == 0f) {
                closeSidebar()
            }
        }
    }
    
    private fun toggleSidebar() {
        val sidebar = binding.sidebar
        val isVisible = sidebar.translationX == 0f
        
        if (isVisible) {
            closeSidebar()
        } else {
            openSidebar()
        }
    }
    
    private fun openSidebar() {
        binding.sidebar.animate()
            .translationX(0f)
            .setDuration(300)
            .start()
    }
    
    private fun closeSidebar() {
        binding.sidebar.animate()
            .translationX(320f * resources.displayMetrics.density)
            .setDuration(300)
            .start()
    }
    
    private fun setupOptionClickListeners() {
        binding.optionA.setOnClickListener { selectOption(0) }
        binding.optionB.setOnClickListener { selectOption(1) }
        binding.optionC.setOnClickListener { selectOption(2) }
        binding.optionD.setOnClickListener { selectOption(3) }
    }
    
    private fun selectOption(optionIndex: Int) {
        // Reset all options
        binding.optionACircle.setBackgroundResource(R.drawable.bg_option_circle)
        binding.optionBCircle.setBackgroundResource(R.drawable.bg_option_circle)
        binding.optionCCircle.setBackgroundResource(R.drawable.bg_option_circle)
        binding.optionDCircle.setBackgroundResource(R.drawable.bg_option_circle)
        binding.optionACircle.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        binding.optionBCircle.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        binding.optionCCircle.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        binding.optionDCircle.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        
        // Set selected option
        selectedOption = optionIndex
        when (optionIndex) {
            0 -> {
                binding.optionACircle.setBackgroundResource(R.drawable.bg_option_selected)
                binding.optionACircle.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            1 -> {
                binding.optionBCircle.setBackgroundResource(R.drawable.bg_option_selected)
                binding.optionBCircle.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            2 -> {
                binding.optionCCircle.setBackgroundResource(R.drawable.bg_option_selected)
                binding.optionCCircle.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            3 -> {
                binding.optionDCircle.setBackgroundResource(R.drawable.bg_option_selected)
                binding.optionDCircle.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
        }
    }
    
    private fun startTimer() {
        countDownTimer = object : CountDownTimer(testDurationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60)
                val seconds = (millisUntilFinished % (1000 * 60)) / 1000
                
                val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                binding.timerText.text = timeString
            }
            
            override fun onFinish() {
                binding.timerText.text = "00:00:00"
                // Auto-submit test when timer ends
            }
        }.start()
    }
    
    private fun setupTabClickListeners() {
        binding.physicsTab.setOnClickListener { selectTab(0) }
        binding.chemistryTab.setOnClickListener { selectTab(1) }
        binding.mathsTab.setOnClickListener { selectTab(2) }
    }
    
    private fun selectTab(tabIndex: Int) {
        // Reset all tabs
        binding.physicsText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        binding.chemistryText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        binding.mathsText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        binding.physicsText.setTypeface(null, android.graphics.Typeface.NORMAL)
        binding.chemistryText.setTypeface(null, android.graphics.Typeface.NORMAL)
        binding.mathsText.setTypeface(null, android.graphics.Typeface.NORMAL)
        binding.physicsUnderline.visibility = View.INVISIBLE
        binding.chemistryUnderline.visibility = View.INVISIBLE
        binding.mathsUnderline.visibility = View.INVISIBLE
        
        // Set active tab
        when (tabIndex) {
            0 -> {
                binding.physicsText.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.physicsText.setTypeface(null, android.graphics.Typeface.BOLD)
                binding.physicsUnderline.visibility = View.VISIBLE
            }
            1 -> {
                binding.chemistryText.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.chemistryText.setTypeface(null, android.graphics.Typeface.BOLD)
                binding.chemistryUnderline.visibility = View.VISIBLE
            }
            2 -> {
                binding.mathsText.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.mathsText.setTypeface(null, android.graphics.Typeface.BOLD)
                binding.mathsUnderline.visibility = View.VISIBLE
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
