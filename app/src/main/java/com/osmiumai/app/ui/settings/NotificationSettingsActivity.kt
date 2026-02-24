package com.osmiumai.app.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.databinding.ActivityNotificationSettingsBinding

class NotificationSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationSettingsBinding
    private val prefs by lazy { getSharedPreferences("notification_settings", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupClickListeners()
        loadSettings()
        setupSwitchListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadSettings() {
        binding.switchCourseUpdates.isChecked = prefs.getBoolean("course_updates", true)
        binding.switchNewContent.isChecked = prefs.getBoolean("new_content", true)
        binding.switchReminders.isChecked = prefs.getBoolean("reminders", false)
        binding.switchPromotions.isChecked = prefs.getBoolean("promotions", false)
    }

    private fun setupSwitchListeners() {
        binding.switchCourseUpdates.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("course_updates", isChecked).apply()
        }

        binding.switchNewContent.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("new_content", isChecked).apply()
        }

        binding.switchReminders.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("reminders", isChecked).apply()
        }

        binding.switchPromotions.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("promotions", isChecked).apply()
        }
    }
}
