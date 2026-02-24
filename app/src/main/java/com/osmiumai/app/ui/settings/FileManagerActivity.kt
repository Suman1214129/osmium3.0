package com.osmiumai.app.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.databinding.ActivityFileManagerBinding

class FileManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFileManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupClickListeners()
        loadStorageInfo()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadStorageInfo() {
        // Calculate storage usage
        // For now showing placeholder
        binding.tvStorageUsed.text = "0.0% of 2GB"
    }
}
