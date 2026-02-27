package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.osmiumai.app.databinding.ActivityCourseOverviewBinding

class CourseOverviewActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCourseOverviewBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        
        binding.ivBack.setOnClickListener {
            finish()
        }
        
        setupLessons()
    }
    
    private fun setupLessons() {
        // Lesson 1 - Visited
        binding.lesson1.tvLessonTitle.text = "UI/UX Basics"
        binding.lesson1.tvStatus.apply {
            text = "Visited"
            visibility = View.VISIBLE
            setBackgroundResource(R.drawable.bg_status_visited)
            setTextColor(0xFF707070.toInt())
        }
        binding.lesson1.tvContinueButton.visibility = View.GONE
        
        // Lesson 2 - Continue
        binding.lesson2.tvLessonTitle.text = "User-centered design"
        binding.lesson2.tvStatus.visibility = View.GONE
        binding.lesson2.tvContinueButton.visibility = View.VISIBLE
        binding.lesson2.tvContinueButton.setOnClickListener {
            val intent = Intent(this, TopicLearnActivity::class.java)
            startActivity(intent)
        }
        
        // Lesson 3 - Not visited
        binding.lesson3.tvLessonTitle.text = "Design principles"
        binding.lesson3.tvStatus.apply {
            text = "Not visited"
            visibility = View.VISIBLE
            setBackgroundResource(R.drawable.bg_status_not_visited)
            setTextColor(0xFF9E9E9E.toInt())
        }
        binding.lesson3.tvContinueButton.visibility = View.GONE
        
        // Lesson 4 - Not visited
        binding.lesson4.tvLessonTitle.text = "Wireframing & UX flows"
        binding.lesson4.tvStatus.apply {
            text = "Not visited"
            visibility = View.VISIBLE
            setBackgroundResource(R.drawable.bg_status_not_visited)
            setTextColor(0xFF9E9E9E.toInt())
        }
        binding.lesson4.tvContinueButton.visibility = View.GONE
        
        // Lesson 5 - Not visited
        binding.lesson5.tvLessonTitle.text = "UI/UX Basics"
        binding.lesson5.tvStatus.apply {
            text = "Not visited"
            visibility = View.VISIBLE
            setBackgroundResource(R.drawable.bg_status_not_visited)
            setTextColor(0xFF9E9E9E.toInt())
        }
        binding.lesson5.tvContinueButton.visibility = View.GONE
    }
}
