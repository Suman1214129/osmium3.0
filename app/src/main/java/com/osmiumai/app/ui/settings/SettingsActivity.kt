package com.osmiumai.app.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.SessionManager
import com.osmiumai.app.WelcomeActivity
import com.osmiumai.app.databinding.ActivitySettingsBinding
import com.osmiumai.app.databinding.DialogEditProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.osmiumai.app.MainActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        loadUserProfile()
        setupClickListeners()
    }

    private fun loadUserProfile() {
        val prefs = getSharedPreferences("OsmiumPrefs", MODE_PRIVATE)
        val userName = prefs.getString("user_name", "User")
        val userEmail = prefs.getString("user_email", "user@osmium.ai")
        
        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        // Account
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnLinkedAccounts.setOnClickListener {
            Toast.makeText(this, "Linked Accounts", Toast.LENGTH_SHORT).show()
        }

        binding.btnChangePassword.setOnClickListener {
            Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show()
        }

        binding.btnDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }

        // Storage
        binding.btnFileManager.setOnClickListener {
            startActivity(Intent(this, FileManagerActivity::class.java))
        }

        // Notifications
        binding.btnNotificationSettings.setOnClickListener {
            startActivity(Intent(this, NotificationSettingsActivity::class.java))
        }

        // Subscription
        binding.btnSubscription.setOnClickListener {
            startActivity(Intent(this, ChoosePlanActivity::class.java))
        }

        // Support
        binding.btnHelp.setOnClickListener {
            startActivity(Intent(this, HelpCenterActivity::class.java))
        }

        binding.btnReportBug.setOnClickListener {
            Toast.makeText(this, "Report a Bug", Toast.LENGTH_SHORT).show()
        }

        binding.btnFeedback.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=$packageName"))
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                startActivity(intent)
            }
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
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
                SessionManager.logout(this)
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
