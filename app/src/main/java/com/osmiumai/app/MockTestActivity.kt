package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.osmiumai.app.databinding.ActivityMockTestBinding

data class MockQuestion(val id: Int, val subject: String, val questionText: String, val optionA: String, val optionB: String, val optionC: String, val optionD: String)

class MockTestActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMockTestBinding
    private var countDownTimer: CountDownTimer? = null
    private var currentSubject = 0
    private var currentQuestionIndex = 0
    private val userAnswers = mutableMapOf<Int, Int>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMockTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupUI()
        setupTabClickListeners()
        setupOptionClickListeners()
        startTimer()
        loadQuestion()
    }
    
    private fun getQuestion(subject: Int, index: Int): MockQuestion {
        val id = subject * 25 + index + 1
        return when(subject) {
            0 -> MockQuestion(id, "Physics", "Question ${index+1}: A particle moves with constant acceleration. Find the acceleration.", "1 m/s²", "2 m/s²", "3 m/s²", "4 m/s²")
            1 -> MockQuestion(id, "Chemistry", "Question ${index+1}: What is the atomic number of Carbon?", "4", "6", "8", "12")
            else -> MockQuestion(id, "Mathematics", "Question ${index+1}: What is the value of π (pi)?", "3.14", "2.71", "1.41", "1.73")
        }
    }
    
    private fun setupUI() {
        binding.submitButton.setOnClickListener {
            startActivity(Intent(this, TestAnalyticsActivity::class.java))
        }
        binding.previousButton.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                loadQuestion()
            }
        }
        binding.nextButton.setOnClickListener {
            if (currentQuestionIndex < 24) {
                currentQuestionIndex++
                loadQuestion()
            }
        }
        binding.menuIcon.setOnClickListener { toggleSidebar() }
        binding.root.setOnClickListener {
            if (binding.sidebar.translationX == 0f) {
                closeSidebar()
            }
        }
    }
    
    private fun loadQuestion() {
        val question = getQuestion(currentSubject, currentQuestionIndex)
        val scrollView = binding.root.getChildAt(0) as? android.view.ViewGroup
        val mainLayout = scrollView?.getChildAt(1) as? android.widget.ScrollView
        val contentLayout = mainLayout?.getChildAt(0) as? android.widget.LinearLayout
        (contentLayout?.getChildAt(0) as? TextView)?.text = question.questionText
        val optionsContainer = contentLayout?.getChildAt(1) as? android.widget.LinearLayout
        ((optionsContainer?.getChildAt(0) as? android.widget.LinearLayout)?.getChildAt(1) as? TextView)?.text = question.optionA
        ((optionsContainer?.getChildAt(2) as? android.widget.LinearLayout)?.getChildAt(1) as? TextView)?.text = question.optionB
        ((optionsContainer?.getChildAt(4) as? android.widget.LinearLayout)?.getChildAt(1) as? TextView)?.text = question.optionC
        ((optionsContainer?.getChildAt(6) as? android.widget.LinearLayout)?.getChildAt(1) as? TextView)?.text = question.optionD
        userAnswers[question.id]?.let { selectOption(it) } ?: clearOptionSelection()
    }
    
    private fun toggleSidebar() {
        if (binding.sidebar.translationX == 0f) {
            closeSidebar()
        } else {
            openSidebar()
        }
    }
    
    private fun openSidebar() {
        binding.sidebar.animate().translationX(0f).setDuration(300).start()
    }
    
    private fun closeSidebar() {
        binding.sidebar.animate().translationX(320f * resources.displayMetrics.density).setDuration(300).start()
    }
    
    private fun setupOptionClickListeners() {
        binding.optionA.setOnClickListener { 
            userAnswers[getQuestion(currentSubject, currentQuestionIndex).id] = 0
            selectOption(0) 
        }
        binding.optionB.setOnClickListener { 
            userAnswers[getQuestion(currentSubject, currentQuestionIndex).id] = 1
            selectOption(1) 
        }
        binding.optionC.setOnClickListener { 
            userAnswers[getQuestion(currentSubject, currentQuestionIndex).id] = 2
            selectOption(2) 
        }
        binding.optionD.setOnClickListener { 
            userAnswers[getQuestion(currentSubject, currentQuestionIndex).id] = 3
            selectOption(3) 
        }
    }
    
    private fun clearOptionSelection() {
        binding.optionACircle.setBackgroundResource(R.drawable.bg_option_circle)
        binding.optionBCircle.setBackgroundResource(R.drawable.bg_option_circle)
        binding.optionCCircle.setBackgroundResource(R.drawable.bg_option_circle)
        binding.optionDCircle.setBackgroundResource(R.drawable.bg_option_circle)
        binding.optionACircle.setTextColor(android.graphics.Color.parseColor("#6F6F6F"))
        binding.optionBCircle.setTextColor(android.graphics.Color.parseColor("#6F6F6F"))
        binding.optionCCircle.setTextColor(android.graphics.Color.parseColor("#6F6F6F"))
        binding.optionDCircle.setTextColor(android.graphics.Color.parseColor("#6F6F6F"))
    }
    
    private fun selectOption(optionIndex: Int) {
        clearOptionSelection()
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
        countDownTimer = object : CountDownTimer(3 * 60 * 60 * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60)
                val seconds = (millisUntilFinished % (1000 * 60)) / 1000
                binding.timerText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }
            override fun onFinish() { binding.timerText.text = "00:00:00" }
        }.start()
    }
    
    private fun setupTabClickListeners() {
        binding.physicsTab.setOnClickListener { 
            currentSubject = 0
            currentQuestionIndex = 0
            selectTab(0)
            loadQuestion()
        }
        binding.chemistryTab.setOnClickListener { 
            currentSubject = 1
            currentQuestionIndex = 0
            selectTab(1)
            loadQuestion()
        }
        binding.mathsTab.setOnClickListener { 
            currentSubject = 2
            currentQuestionIndex = 0
            selectTab(2)
            loadQuestion()
        }
    }
    
    private fun selectTab(tabIndex: Int) {
        binding.physicsText.setTextColor(android.graphics.Color.parseColor("#8B8B8B"))
        binding.chemistryText.setTextColor(android.graphics.Color.parseColor("#8B8B8B"))
        binding.mathsText.setTextColor(android.graphics.Color.parseColor("#8B8B8B"))
        binding.physicsText.setTypeface(null, android.graphics.Typeface.NORMAL)
        binding.chemistryText.setTypeface(null, android.graphics.Typeface.NORMAL)
        binding.mathsText.setTypeface(null, android.graphics.Typeface.NORMAL)
        binding.physicsUnderline.visibility = View.INVISIBLE
        binding.chemistryUnderline.visibility = View.INVISIBLE
        binding.mathsUnderline.visibility = View.INVISIBLE
        when (tabIndex) {
            0 -> {
                binding.physicsText.setTextColor(android.graphics.Color.parseColor("#1C1C1C"))
                binding.physicsText.setTypeface(null, android.graphics.Typeface.BOLD)
                binding.physicsUnderline.visibility = View.VISIBLE
            }
            1 -> {
                binding.chemistryText.setTextColor(android.graphics.Color.parseColor("#1C1C1C"))
                binding.chemistryText.setTypeface(null, android.graphics.Typeface.BOLD)
                binding.chemistryUnderline.visibility = View.VISIBLE
            }
            2 -> {
                binding.mathsText.setTextColor(android.graphics.Color.parseColor("#1C1C1C"))
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
