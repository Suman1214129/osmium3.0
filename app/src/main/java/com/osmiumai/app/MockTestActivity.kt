package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.osmiumai.app.databinding.ActivityMockTestBinding
import org.json.JSONObject

data class MockQuestion(val id: Int, val subject: String, val questionText: String, val optionA: String, val optionB: String, val optionC: String, val optionD: String, val imageUrl: String? = null)

class MockTestActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMockTestBinding
    private var countDownTimer: CountDownTimer? = null
    private var questionTimer: CountDownTimer? = null
    private var currentSubject = 0
    private var currentQuestionIndex = 0
    private val userAnswers = mutableMapOf<Int, Int>()
    private val bookmarkedQuestions = mutableSetOf<Int>()
    private val questionTimes = mutableMapOf<Int, Long>()
    private var questionStartTime = 0L
    private val allQuestions = mutableMapOf<String, List<MockQuestion>>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMockTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        loadQuestionsFromJSON()
        setupUI()
        setupTabClickListeners()
        setupOptionClickListeners()
        startTimer()
        loadQuestion()
    }
    
    private fun loadQuestionsFromJSON() {
        val json = assets.open("mock_test_questions.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(json)
        
        val physicsArray = jsonObject.getJSONArray("physics")
        val physicsList = mutableListOf<MockQuestion>()
        for (i in 0 until physicsArray.length()) {
            val q = physicsArray.getJSONObject(i)
            val imageUrl = if (q.has("imageUrl")) q.getString("imageUrl") else null
            physicsList.add(MockQuestion(i + 1, "Physics", q.getString("question"), 
                q.getString("optionA"), q.getString("optionB"), 
                q.getString("optionC"), q.getString("optionD"), imageUrl))
        }
        
        val chemistryArray = jsonObject.getJSONArray("chemistry")
        val chemistryList = mutableListOf<MockQuestion>()
        for (i in 0 until chemistryArray.length()) {
            val q = chemistryArray.getJSONObject(i)
            val imageUrl = if (q.has("imageUrl")) q.getString("imageUrl") else null
            chemistryList.add(MockQuestion(26 + i, "Chemistry", q.getString("question"), 
                q.getString("optionA"), q.getString("optionB"), 
                q.getString("optionC"), q.getString("optionD"), imageUrl))
        }
        
        val mathsArray = jsonObject.getJSONArray("mathematics")
        val mathsList = mutableListOf<MockQuestion>()
        for (i in 0 until mathsArray.length()) {
            val q = mathsArray.getJSONObject(i)
            val imageUrl = if (q.has("imageUrl")) q.getString("imageUrl") else null
            mathsList.add(MockQuestion(51 + i, "Mathematics", q.getString("question"), 
                q.getString("optionA"), q.getString("optionB"), 
                q.getString("optionC"), q.getString("optionD"), imageUrl))
        }
        
        allQuestions["physics"] = physicsList
        allQuestions["chemistry"] = chemistryList
        allQuestions["mathematics"] = mathsList
    }
    
    private fun getQuestion(subject: Int, index: Int): MockQuestion {
        return when(subject) {
            0 -> allQuestions["physics"]?.get(index) ?: MockQuestion(1, "Physics", "Loading...", "", "", "", "", null)
            1 -> allQuestions["chemistry"]?.get(index) ?: MockQuestion(26, "Chemistry", "Loading...", "", "", "", "", null)
            else -> allQuestions["mathematics"]?.get(index) ?: MockQuestion(51, "Mathematics", "Loading...", "", "", "", "", null)
        }
    }
    
    private fun setupUI() {
        binding.submitButton.setOnClickListener {
            val intent = Intent(this, TestAnalyticsActivity::class.java)
            intent.putExtra("userAnswers", HashMap(userAnswers))
            intent.putExtra("questionTimes", HashMap(questionTimes))
            startActivity(intent)
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
        binding.bookmarkIcon.setOnClickListener {
            val questionId = getQuestion(currentSubject, currentQuestionIndex).id
            if (bookmarkedQuestions.contains(questionId)) {
                bookmarkedQuestions.remove(questionId)
            } else {
                bookmarkedQuestions.add(questionId)
            }
            updateBookmarkIcon()
        }
        setupGridClickListeners()
    }
    
    private fun setupGridClickListeners() {
        val sidebar = binding.sidebar
        for (subject in 0..2) {
            for (q in 0..24) {
                val gridId = resources.getIdentifier("q${subject}_${q+1}", "id", packageName)
                if (gridId != 0) {
                    sidebar.findViewById<TextView>(gridId)?.setOnClickListener {
                        currentSubject = subject
                        currentQuestionIndex = q
                        selectTab(subject)
                        loadQuestion()
                        closeSidebar()
                    }
                }
            }
        }
    }
    
    private fun loadQuestion() {
        stopQuestionTimer()
        val question = getQuestion(currentSubject, currentQuestionIndex)
        binding.questionNumber.text = (currentQuestionIndex + 1).toString()
        binding.questionText.text = question.questionText
        
        val questionImage = binding.root.findViewById<WebView>(R.id.questionImage)
        if (!question.imageUrl.isNullOrEmpty()) {
            questionImage?.visibility = View.VISIBLE
            questionImage?.settings?.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
            }
            val html = """<html><body style='margin:0;padding:30px;background:#F7F5F3;text-align:center;display:flex;justify-content:center;align-items:center;'><img src='${question.imageUrl}' style='max-width:100%;height:auto;display:block;'/></body></html>"""
            questionImage?.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        } else {
            questionImage?.visibility = View.GONE
        }
        
        // Update options
        val scrollView = binding.root.getChildAt(0) as? android.view.ViewGroup
        val mainLayout = scrollView?.getChildAt(1) as? android.widget.ScrollView
        val contentLayout = mainLayout?.getChildAt(0) as? android.widget.LinearLayout
        val optionsContainer = contentLayout?.getChildAt(2) as? android.widget.LinearLayout
        ((optionsContainer?.getChildAt(0) as? android.widget.LinearLayout)?.getChildAt(1) as? TextView)?.text = question.optionA
        ((optionsContainer?.getChildAt(2) as? android.widget.LinearLayout)?.getChildAt(1) as? TextView)?.text = question.optionB
        ((optionsContainer?.getChildAt(4) as? android.widget.LinearLayout)?.getChildAt(1) as? TextView)?.text = question.optionC
        ((optionsContainer?.getChildAt(6) as? android.widget.LinearLayout)?.getChildAt(1) as? TextView)?.text = question.optionD
        
        userAnswers[question.id]?.let { selectOption(it) } ?: clearOptionSelection()
        updateBookmarkIcon()
        updateGridState()
        startQuestionTimer()
    }
    
    private fun startQuestionTimer() {
        val questionId = getQuestion(currentSubject, currentQuestionIndex).id
        val previousTime = questionTimes[questionId] ?: 0L
        questionStartTime = System.currentTimeMillis()
        questionTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsed = previousTime + (System.currentTimeMillis() - questionStartTime)
                val totalSeconds = elapsed / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                binding.questionTimer.text = String.format("%d:%02d", minutes, seconds)
            }
            override fun onFinish() {}
        }.start()
    }
    
    private fun stopQuestionTimer() {
        questionTimer?.cancel()
        if (questionStartTime > 0) {
            val questionId = getQuestion(currentSubject, currentQuestionIndex).id
            val elapsed = System.currentTimeMillis() - questionStartTime
            questionTimes[questionId] = (questionTimes[questionId] ?: 0L) + elapsed
        }
    }
    
    private fun updateBookmarkIcon() {
        val questionId = getQuestion(currentSubject, currentQuestionIndex).id
        if (bookmarkedQuestions.contains(questionId)) {
            binding.bookmarkIcon.setImageResource(R.drawable.ic_bookmark_filled)
        } else {
            binding.bookmarkIcon.setImageResource(R.drawable.ic_bookmark_border)
        }
        updateGridState()
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
            val questionId = getQuestion(currentSubject, currentQuestionIndex).id
            if (userAnswers[questionId] == 0) {
                userAnswers.remove(questionId)
                clearOptionSelection()
            } else {
                userAnswers[questionId] = 0
                selectOption(0)
            }
            updateGridState()
        }
        binding.optionB.setOnClickListener { 
            val questionId = getQuestion(currentSubject, currentQuestionIndex).id
            if (userAnswers[questionId] == 1) {
                userAnswers.remove(questionId)
                clearOptionSelection()
            } else {
                userAnswers[questionId] = 1
                selectOption(1)
            }
            updateGridState()
        }
        binding.optionC.setOnClickListener { 
            val questionId = getQuestion(currentSubject, currentQuestionIndex).id
            if (userAnswers[questionId] == 2) {
                userAnswers.remove(questionId)
                clearOptionSelection()
            } else {
                userAnswers[questionId] = 2
                selectOption(2)
            }
            updateGridState()
        }
        binding.optionD.setOnClickListener { 
            val questionId = getQuestion(currentSubject, currentQuestionIndex).id
            if (userAnswers[questionId] == 3) {
                userAnswers.remove(questionId)
                clearOptionSelection()
            } else {
                userAnswers[questionId] = 3
                selectOption(3)
            }
            updateGridState()
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
                binding.optionACircle.setBackgroundResource(R.drawable.bg_option_selected_mock)
                binding.optionACircle.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            1 -> {
                binding.optionBCircle.setBackgroundResource(R.drawable.bg_option_selected_mock)
                binding.optionBCircle.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            2 -> {
                binding.optionCCircle.setBackgroundResource(R.drawable.bg_option_selected_mock)
                binding.optionCCircle.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            3 -> {
                binding.optionDCircle.setBackgroundResource(R.drawable.bg_option_selected_mock)
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
        questionTimer?.cancel()
    }
    
    private fun updateGridState() {
        val counts = Array(3) { intArrayOf(0, 0, 0) }
        for (subject in 0..2) {
            for (q in 0..24) {
                val questionId = subject * 25 + q + 1
                val gridId = resources.getIdentifier("q${subject}_${q+1}", "id", packageName)
                if (gridId != 0) {
                    binding.sidebar.findViewById<TextView>(gridId)?.apply {
                        val isCurrent = subject == currentSubject && q == currentQuestionIndex
                        when {
                            isCurrent -> {
                                setBackgroundResource(R.drawable.bg_current_question)
                                setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
                            }
                            bookmarkedQuestions.contains(questionId) -> {
                                setBackgroundResource(R.drawable.bg_marked)
                                setTextColor(android.graphics.Color.parseColor("#7E57C2"))
                                counts[subject][2]++
                            }
                            userAnswers.containsKey(questionId) -> {
                                setBackgroundResource(R.drawable.bg_answered)
                                setTextColor(android.graphics.Color.parseColor("#1C1C1C"))
                                counts[subject][0]++
                            }
                            else -> {
                                setBackgroundResource(R.drawable.bg_not_answered)
                                setTextColor(android.graphics.Color.parseColor("#9E9E9E"))
                                counts[subject][1]++
                            }
                        }
                    }
                }
            }
        }
        binding.physicsAnswered.text = "● ${counts[0][0]} answered"
        binding.physicsNotAnswered.text = "○ ${counts[0][1]} Not answered"
        binding.physicsMarked.text = "● ${counts[0][2]} Marked"
        binding.chemistryAnswered.text = "● ${counts[1][0]} answered"
        binding.chemistryNotAnswered.text = "○ ${counts[1][1]} Not answered"
        binding.chemistryMarked.text = "● ${counts[1][2]} Marked"
        binding.mathsAnswered.text = "● ${counts[2][0]} answered"
        binding.mathsNotAnswered.text = "○ ${counts[2][1]} Not answered"
        binding.mathsMarked.text = "● ${counts[2][2]} Marked"
    }
}
