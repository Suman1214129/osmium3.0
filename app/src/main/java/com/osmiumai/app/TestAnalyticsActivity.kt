package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.osmiumai.app.databinding.ActivityTestAnalyticsBinding
import org.json.JSONArray
import org.json.JSONObject

data class AnalyticsQuestion(val id: Int, val text: String, val optionA: String, val optionB: String, val optionC: String, val optionD: String, val correctAnswer: String)

class TestAnalyticsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTestAnalyticsBinding
    private var userAnswers = mutableMapOf<Int, Int>()
    private val correctAnswers = mutableMapOf<Int, String>()
    private var currentSection = 0
    private lateinit var performanceGrid: LinearLayout
    private lateinit var tabsLayout: LinearLayout
    private lateinit var questionsContainer: LinearLayout
    private val allQuestions = mutableMapOf<Int, List<AnalyticsQuestion>>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        
        @Suppress("UNCHECKED_CAST", "DEPRECATION")
        userAnswers = (intent.getSerializableExtra("userAnswers") as? HashMap<Int, Int>)?.toMutableMap() ?: mutableMapOf()
        
        loadCorrectAnswers()
        calculateResults()
        setupViews()
        
        binding.backButton.setOnClickListener { finish() }
    }
    
    private fun loadCorrectAnswers() {
        val json = assets.open("mock_test_questions.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(json)
        
        allQuestions[0] = parseQuestions(jsonObject.getJSONArray("physics"), 1)
        allQuestions[1] = parseQuestions(jsonObject.getJSONArray("mathematics"), 51)
        allQuestions[2] = parseQuestions(jsonObject.getJSONArray("chemistry"), 26)
        
        allQuestions.values.flatten().forEach { q ->
            correctAnswers[q.id] = q.correctAnswer
        }
    }
    
    private fun parseQuestions(array: JSONArray, startId: Int): List<AnalyticsQuestion> {
        return (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            AnalyticsQuestion(
                startId + i,
                obj.getString("question"),
                obj.getString("optionA"),
                obj.getString("optionB"),
                obj.getString("optionC"),
                obj.getString("optionD"),
                obj.getString("correctAnswer")
            )
        }
    }
    
    private fun calculateResults() {
        val optionMap = mapOf(0 to "A", 1 to "B", 2 to "C", 3 to "D")
        var totalCorrect = 0
        var totalIncorrect = 0
        
        for ((questionId, userAnswer) in userAnswers) {
            if (optionMap[userAnswer] == correctAnswers[questionId]) {
                totalCorrect++
            } else {
                totalIncorrect++
            }
        }
        
        val totalAttempted = userAnswers.size
        val totalMarks = (totalCorrect * 4) - totalIncorrect
        val accuracy = if (totalAttempted > 0) (totalCorrect.toFloat() / totalAttempted * 100) else 0f
        
        binding.scoreValue.text = totalMarks.toString()
        binding.attemptedText.text = "( $totalAttempted/75 Attempted )"
        binding.accuracyValue.text = accuracy.toInt().toString()
        binding.accuracyDecimal.text = ".${((accuracy - accuracy.toInt()) * 100).toInt()}%"
        binding.correctAttemptedText.text = "$totalCorrect/$totalAttempted Correct"
    }
    
    private fun setupViews() {
        val scrollView = binding.root as android.widget.ScrollView
        val mainLayout = scrollView.getChildAt(0) as LinearLayout
        
        tabsLayout = mainLayout.getChildAt(4) as LinearLayout
        val cardView = mainLayout.getChildAt(5) as androidx.cardview.widget.CardView
        val cardContent = cardView.getChildAt(0) as LinearLayout
        performanceGrid = cardContent.getChildAt(2) as LinearLayout
        questionsContainer = mainLayout
        
        mainLayout.removeViewAt(6)
        mainLayout.removeViewAt(6)
        
        tabsLayout.getChildAt(0).setOnClickListener {
            currentSection = 0
            updateTabUI()
            updateSection()
        }
        
        tabsLayout.getChildAt(1).setOnClickListener {
            currentSection = 1
            updateTabUI()
            updateSection()
        }
        
        tabsLayout.getChildAt(2).setOnClickListener {
            currentSection = 2
            updateTabUI()
            updateSection()
        }
        
        updateSection()
    }
    
    private fun updateTabUI() {
        for (i in 0..2) {
            val tab = tabsLayout.getChildAt(i) as LinearLayout
            val textView = tab.getChildAt(0) as TextView
            val underline = tab.getChildAt(1)
            
            if (i == currentSection) {
                textView.setTextColor(android.graphics.Color.parseColor("#1E1E1E"))
                textView.setTypeface(null, android.graphics.Typeface.BOLD)
                underline.layoutParams.height = 4
                underline.setBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"))
            } else {
                textView.setTextColor(android.graphics.Color.parseColor("#757575"))
                textView.setTypeface(null, android.graphics.Typeface.NORMAL)
                underline.layoutParams.height = 3
                underline.setBackgroundColor(android.graphics.Color.parseColor("#E0E0E0"))
            }
            underline.requestLayout()
        }
    }
    
    private fun updateSection() {
        val optionMap = mapOf(0 to "A", 1 to "B", 2 to "C", 3 to "D")
        val startId = when(currentSection) { 0 -> 1; 1 -> 51; else -> 26 }
        
        var sectionCorrect = 0
        var sectionIncorrect = 0
        var sectionAttempted = 0
        
        for (i in startId until startId + 25) {
            if (userAnswers.containsKey(i)) {
                sectionAttempted++
                if (optionMap[userAnswers[i]] == correctAnswers[i]) {
                    sectionCorrect++
                } else {
                    sectionIncorrect++
                }
            }
        }
        
        val sectionScore = (sectionCorrect * 4) - sectionIncorrect
        val sectionAccuracy = if (sectionAttempted > 0) (sectionCorrect.toFloat() / sectionAttempted * 100) else 0f
        
        binding.physicsSectionScore.text = sectionScore.toString()
        binding.physicsSectionAttempted.text = sectionAttempted.toString()
        binding.physicsSectionAccuracy.text = sectionAccuracy.toInt().toString()
        binding.physicsSectionAccuracyDecimal.text = ".${((sectionAccuracy - sectionAccuracy.toInt()) * 100).toInt()}%"
        
        updatePerformanceGrid(optionMap, startId)
        loadQuestions()
    }
    
    private fun loadQuestions() {
        while (questionsContainer.childCount > 6) {
            questionsContainer.removeViewAt(6)
        }
        
        val questions = allQuestions[currentSection] ?: return
        val optionMap = mapOf(0 to "A", 1 to "B", 2 to "C", 3 to "D")
        
        questions.forEachIndexed { index, q ->
            val cardView = LayoutInflater.from(this).inflate(R.layout.item_analytics_question, questionsContainer, false) as LinearLayout
            
            val header = cardView.findViewById<LinearLayout>(R.id.questionHeader)
            val details = cardView.findViewById<LinearLayout>(R.id.questionDetails)
            val dropdownIcon = cardView.findViewById<ImageView>(R.id.dropdownIcon)
            val questionPreview = cardView.findViewById<TextView>(R.id.questionPreview)
            
            cardView.findViewById<TextView>(R.id.questionNumber).text = "Question ${index + 1}."
            cardView.findViewById<TextView>(R.id.questionText).text = q.text
            questionPreview.text = q.text
            
            details.visibility = View.GONE
            dropdownIcon.rotation = 180f
            
            // Time spent and expected time
            val timeSpentMin = (60 + (index * 17) % 120)
            val timeSpentSec = (timeSpentMin % 60)
            cardView.findViewById<TextView>(R.id.timeSpent).text = "${timeSpentMin / 60}:${String.format("%02d", timeSpentSec)} Min"
            cardView.findViewById<TextView>(R.id.expectedTime).text = "180s"
            
            // Topic tags
            val topicTags = cardView.findViewById<LinearLayout>(R.id.topicTags)
            val topics = getTopicsForQuestion(currentSection, index)
            val colors = listOf("#4CAF50", "#2196F3", "#9C27B0", "#FF5722")
            topics.forEachIndexed { i, topic ->
                val tagView = TextView(this).apply {
                    text = topic
                    textSize = 10f
                    setTextColor(android.graphics.Color.parseColor(colors[i % colors.size]))
                    setBackgroundResource(R.drawable.subject_tag_bg)
                    setPadding(24, 12, 24, 12)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 24
                    }
                }
                topicTags.addView(tagView)
            }
            
            // Ask Mentor button
            cardView.findViewById<LinearLayout>(R.id.askMentorButton).setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("navigate_to", "ai_mentor")
                intent.putExtra("questionText", q.text)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            
            val userAnswer = userAnswers[q.id]?.let { optionMap[it] }
            val yourAnswerValue = cardView.findViewById<TextView>(R.id.yourAnswerValue)
            val correctLabel = cardView.findViewById<TextView>(R.id.correctLabel)
            val correctValue = cardView.findViewById<TextView>(R.id.correctValue)
            
            if (userAnswer == null) {
                yourAnswerValue.text = "Not Attempted"
                yourAnswerValue.setTextColor(android.graphics.Color.parseColor("#757575"))
                correctLabel.visibility = View.GONE
                correctValue.visibility = View.GONE
            } else {
                yourAnswerValue.text = userAnswer
                yourAnswerValue.setTextColor(if (userAnswer == q.correctAnswer) android.graphics.Color.parseColor("#4CAF50") else android.graphics.Color.parseColor("#F44336"))
                correctLabel.visibility = View.VISIBLE
                correctValue.visibility = View.VISIBLE
                correctValue.text = q.correctAnswer
            }
            
            val options = listOf(
                Triple(R.id.optionA, R.id.optionALabel, R.id.optionAText) to Pair("A", q.optionA),
                Triple(R.id.optionB, R.id.optionBLabel, R.id.optionBText) to Pair("B", q.optionB),
                Triple(R.id.optionC, R.id.optionCLabel, R.id.optionCText) to Pair("C", q.optionC),
                Triple(R.id.optionD, R.id.optionDLabel, R.id.optionDText) to Pair("D", q.optionD)
            )
            
            options.forEach { (ids, data) ->
                val (containerId, labelId, textId) = ids
                val (letter, text) = data
                
                val container = cardView.findViewById<LinearLayout>(containerId)
                val label = cardView.findViewById<TextView>(labelId)
                val textView = cardView.findViewById<TextView>(textId)
                
                textView.text = text
                
                when {
                    letter == q.correctAnswer -> {
                        container.setBackgroundResource(R.drawable.option_correct_bg)
                        label.setBackgroundResource(R.drawable.option_correct_circle)
                        label.setTextColor(android.graphics.Color.WHITE)
                        label.setTypeface(null, android.graphics.Typeface.BOLD)
                    }
                    letter == userAnswer && userAnswer != q.correctAnswer -> {
                        container.setBackgroundResource(R.drawable.option_wrong_bg)
                        label.setBackgroundResource(R.drawable.option_wrong_circle)
                        label.setTextColor(android.graphics.Color.WHITE)
                        label.setTypeface(null, android.graphics.Typeface.BOLD)
                    }
                    else -> {
                        container.setBackgroundResource(R.drawable.option_default_bg)
                        label.setBackgroundResource(R.drawable.option_default_circle)
                        label.setTextColor(android.graphics.Color.parseColor("#757575"))
                    }
                }
            }
            
            header.setOnClickListener {
                if (details.visibility == View.VISIBLE) {
                    details.visibility = View.GONE
                    dropdownIcon.rotation = 180f
                    questionPreview.visibility = View.VISIBLE
                } else {
                    details.visibility = View.VISIBLE
                    dropdownIcon.rotation = 0f
                    questionPreview.visibility = View.GONE
                }
            }
            
            questionsContainer.addView(cardView)
        }
    }
    
    private fun getTopicsForQuestion(section: Int, index: Int): List<String> {
        return when (section) {
            0 -> when (index % 5) {
                0 -> listOf("Kinematics", "NLM")
                1 -> listOf("Friction", "Forces")
                2 -> listOf("Electrostatics", "Capacitors")
                3 -> listOf("Optics", "Waves")
                else -> listOf("Thermodynamics", "Heat")
            }
            1 -> when (index % 5) {
                0 -> listOf("Calculus", "Limits")
                1 -> listOf("Algebra", "Equations")
                2 -> listOf("Geometry", "Circles")
                3 -> listOf("Probability", "Statistics")
                else -> listOf("Trigonometry", "Functions")
            }
            else -> when (index % 5) {
                0 -> listOf("Organic", "Reactions")
                1 -> listOf("Inorganic", "Periodic Table")
                2 -> listOf("Physical", "Thermodynamics")
                3 -> listOf("Atomic Structure", "Bonding")
                else -> listOf("Equilibrium", "Kinetics")
            }
        }
    }
    
    private fun updatePerformanceGrid(optionMap: Map<Int, String>, startId: Int) {
        for (row in 0..2) {
            val rowLayout = performanceGrid.getChildAt(row) as? LinearLayout ?: continue
            val count = if (row == 2) 5 else 9
            
            for (col in 0 until count) {
                val questionIndex = row * 9 + col
                if (questionIndex < 25) {
                    val questionId = startId + questionIndex
                    val view = rowLayout.getChildAt(col) as? ImageView ?: continue
                    
                    val userAnswerLetter = userAnswers[questionId]?.let { optionMap[it] }
                    val correctAnswerLetter = correctAnswers[questionId]
                    
                    when {
                        userAnswerLetter == null -> {
                            view.setBackgroundResource(R.drawable.circle_bg_light_gray)
                            view.setImageDrawable(null)
                        }
                        userAnswerLetter == correctAnswerLetter -> {
                            view.setBackgroundResource(R.drawable.circle_bg_green)
                            view.setImageResource(R.drawable.ic_check)
                            view.setColorFilter(android.graphics.Color.parseColor("#4CAF50"))
                        }
                        else -> {
                            view.setBackgroundResource(R.drawable.circle_bg_white)
                            view.setImageResource(R.drawable.ic_close)
                            view.setColorFilter(android.graphics.Color.parseColor("#F44336"))
                        }
                    }
                }
            }
        }
    }
}
