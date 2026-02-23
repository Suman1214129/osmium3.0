package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.databinding.ActivityStartCourseBinding
import com.google.android.material.chip.Chip

class StartCourseActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStartCourseBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.hide()
        
        binding.ivBack.setOnClickListener {
            finish()
        }
        
        binding.btnSearchArrow.setOnClickListener {
            navigateToCourseOverview()
        }
        
        setupChipClickListeners()
    }
    
    private fun setupChipClickListeners() {
        for (i in 0 until binding.chipGroup.childCount) {
            val child = binding.chipGroup.getChildAt(i)
            if (child is Chip) {
                child.setOnClickListener {
                    navigateToCourseOverview()
                }
            }
        }
    }
    
    private fun navigateToCourseOverview() {
        val intent = Intent(this, CourseOverviewActivity::class.java)
        startActivity(intent)
    }
}
