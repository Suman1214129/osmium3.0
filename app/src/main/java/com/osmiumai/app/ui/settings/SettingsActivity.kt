package com.osmiumai.app.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.WelcomeActivity
import com.osmiumai.app.databinding.ActivitySettingsBinding
import com.osmiumai.app.databinding.DialogEditProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.osmiumai.app.ui.notifications.NotificationsActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        // Files Section
        binding.btnFileManager.setOnClickListener {
            startActivity(Intent(this, FileManagerActivity::class.java))
        }

        binding.btnCloudStorage.setOnClickListener {
            Toast.makeText(this, "Cloud Storage", Toast.LENGTH_SHORT).show()
        }

        binding.btnDownloads.setOnClickListener {
            startActivity(Intent(this, DownloadsActivity::class.java))
        }

        // Account Section
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnNotifications.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        binding.btnNotificationSettings.setOnClickListener {
            startActivity(Intent(this, NotificationSettingsActivity::class.java))
        }

        binding.btnPrivacySecurity.setOnClickListener {
            Toast.makeText(this, "Privacy & Security", Toast.LENGTH_SHORT).show()
        }

        binding.btnSubscription.setOnClickListener {
            startActivity(Intent(this, SubscriptionActivity::class.java))
        }

        // Support Section
        binding.btnHelp.setOnClickListener {
            startActivity(Intent(this, HelpCenterActivity::class.java))
        }

        binding.btnContactUs.setOnClickListener {
            Toast.makeText(this, "Contact Us", Toast.LENGTH_SHORT).show()
        }

        binding.btnFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }



    private fun showEditProfileDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        
        dialogBinding.btnSave.setOnClickListener {
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }



    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out") { _, _ ->
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
