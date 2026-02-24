package com.osmiumai.app.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.databinding.ActivitySubscriptionBinding

class SubscriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubscriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnUpgradePremium.setOnClickListener {
            Toast.makeText(this, "Redirecting to payment...", Toast.LENGTH_SHORT).show()
        }

        binding.btnSelectPro.setOnClickListener {
            Toast.makeText(this, "Redirecting to payment...", Toast.LENGTH_SHORT).show()
        }

        binding.btnCancelSubscription.setOnClickListener {
            showCancelDialog()
        }
    }

    private fun showCancelDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cancel Subscription")
            .setMessage("Are you sure you want to cancel your subscription? You will lose access to premium features.")
            .setPositiveButton("Cancel Subscription") { _, _ ->
                Toast.makeText(this, "Subscription cancelled", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Keep Subscription", null)
            .show()
    }
}
