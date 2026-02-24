package com.osmiumai.app.ui.settings

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.osmiumai.app.R
import com.osmiumai.app.databinding.ActivityFeedbackBinding

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private var selectedRating = 0
    private val stars = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupStars()
        setupClickListeners()
    }

    private fun setupStars() {
        stars.addAll(listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5))
        
        stars.forEachIndexed { index, star ->
            star.setOnClickListener {
                selectedRating = index + 1
                updateStars()
            }
        }
    }

    private fun updateStars() {
        stars.forEachIndexed { index, star ->
            if (index < selectedRating) {
                star.setColorFilter(ContextCompat.getColor(this, R.color.star_filled))
            } else {
                star.setColorFilter(ContextCompat.getColor(this, R.color.star_empty))
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnSubmitFeedback.setOnClickListener {
            val feedback = binding.etFeedback.text.toString()
            val selectedChipId = binding.chipGroup.checkedChipId
            
            if (selectedRating == 0) {
                Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (feedback.isEmpty()) {
                Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (selectedChipId == -1) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
