package com.osmiumai.app.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.databinding.ActivityHelpCenterBinding

class HelpCenterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpCenterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.faq1.setOnClickListener {
            toggleFaq(binding.answerFaq1, binding.iconFaq1)
        }

        binding.faq2.setOnClickListener {
            toggleFaq(binding.answerFaq2, binding.iconFaq2)
        }

        binding.faq3.setOnClickListener {
            toggleFaq(binding.answerFaq3, binding.iconFaq3)
        }

        binding.btnEmailSupport.setOnClickListener {
            Toast.makeText(this, "Opening email...", Toast.LENGTH_SHORT).show()
        }

        binding.btnLiveChat.setOnClickListener {
            Toast.makeText(this, "Starting live chat...", Toast.LENGTH_SHORT).show()
        }

        binding.btnCallUs.setOnClickListener {
            Toast.makeText(this, "Calling support...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFaq(answer: View, icon: View) {
        if (answer.visibility == View.GONE) {
            answer.visibility = View.VISIBLE
            icon.rotation = 180f
        } else {
            answer.visibility = View.GONE
            icon.rotation = 0f
        }
    }
}
