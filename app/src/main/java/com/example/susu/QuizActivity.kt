package com.example.susu

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.susu.databinding.ContentQuizBinding

data class Question(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int
)

class QuizActivity : AppCompatActivity() {
    
    private lateinit var binding: ContentQuizBinding
    private var currentQuestionIndex = 0
    private var selectedOption = -1
    
    private val questions = listOf(
        Question("What is the primary goal of user-centric design?", 
            listOf("Aesthetic perfection", "Business scalability", "Solving real user problems", "Technical optimization"), 2),
        Question("Which phase comes first in the UCD process?", 
            listOf("Testing", "Research", "Design", "Implementation"), 1),
        Question("What is a persona in UX design?", 
            listOf("A fictional character", "A real user", "A user archetype", "A designer"), 2),
        Question("What does usability testing measure?", 
            listOf("Visual appeal", "User satisfaction", "Code quality", "Loading speed"), 1),
        Question("What is the purpose of wireframing?", 
            listOf("Final design", "Visual layout structure", "Color scheme", "Typography"), 1),
        Question("Which method is best for gathering user feedback?", 
            listOf("Assumptions", "User interviews", "Guessing", "Copying competitors"), 1),
        Question("What is A/B testing used for?", 
            listOf("Comparing two versions", "Testing bugs", "Security testing", "Performance testing"), 0),
        Question("What does accessibility in design mean?", 
            listOf("Fast loading", "Usable by everyone", "Mobile-friendly", "Colorful design"), 1),
        Question("What is the main benefit of user research?", 
            listOf("Faster development", "Understanding user needs", "Lower costs", "Better graphics"), 1),
        Question("What is an MVP in product design?", 
            listOf("Most Valuable Player", "Minimum Viable Product", "Maximum Value Proposition", "Minimal Visual Prototype"), 1)
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        loadQuestion()
        setupButtons()
    }
    
    private fun loadQuestion() {
        val question = questions[currentQuestionIndex]
        
        binding.questionText.text = question.question
        binding.questionProgress.text = "Questions ${currentQuestionIndex + 1}/10"
        binding.progressBar.progress = ((currentQuestionIndex + 1) * 10)
        
        val optionViews = listOf(
            binding.optionA,
            binding.optionB,
            binding.optionC,
            binding.optionD
        )
        
        val optionTextIds = listOf(R.id.optionTextA, R.id.optionTextB, R.id.optionTextC, R.id.optionTextD)
        val optionLabelIds = listOf(R.id.optionLabelA, R.id.optionLabelB, R.id.optionLabelC, R.id.optionLabelD)
        
        question.options.forEachIndexed { index, option ->
            optionViews[index].findViewById<TextView>(optionTextIds[index]).text = option
            optionViews[index].findViewById<TextView>(optionLabelIds[index]).text = "${('A' + index)}."
            
            optionViews[index].setOnClickListener {
                selectOption(index)
            }
        }
        
        selectedOption = -1
        updateOptionsUI()
        updateButtons()
    }
    
    private fun selectOption(index: Int) {
        selectedOption = index
        updateOptionsUI()
        updateButtons()
    }
    
    private fun updateOptionsUI() {
        val optionViews = listOf(
            binding.optionA,
            binding.optionB,
            binding.optionC,
            binding.optionD
        )
        
        val optionLabelIds = listOf(R.id.optionLabelA, R.id.optionLabelB, R.id.optionLabelC, R.id.optionLabelD)
        
        optionViews.forEachIndexed { index, view ->
            if (index == selectedOption) {
                view.setBackgroundResource(R.drawable.bg_option_selected)
                view.findViewById<TextView>(optionLabelIds[index]).setBackgroundResource(R.drawable.circle_green)
                view.findViewById<TextView>(optionLabelIds[index]).setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                view.setBackgroundResource(R.drawable.bg_option_card)
                view.findViewById<TextView>(optionLabelIds[index]).setBackgroundResource(R.drawable.circle_gray_light)
                view.findViewById<TextView>(optionLabelIds[index]).setTextColor(ContextCompat.getColor(this, R.color.gray_600))
            }
        }
    }
    
    private fun setupButtons() {
        binding.btnPrevious.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                loadQuestion()
            }
        }
        
        binding.btnNext.setOnClickListener {
            if (selectedOption != -1) {
                if (currentQuestionIndex < questions.size - 1) {
                    currentQuestionIndex++
                    loadQuestion()
                } else {
                    // Quiz completed
                    finish()
                }
            }
        }
    }
    
    private fun updateButtons() {
        binding.btnPrevious.isEnabled = currentQuestionIndex > 0
        binding.btnNext.isEnabled = selectedOption != -1
        
        binding.btnNext.alpha = if (selectedOption != -1) 1.0f else 0.5f
        binding.btnPrevious.alpha = if (currentQuestionIndex > 0) 1.0f else 0.5f
    }
}
