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

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        loadUserProfile()
        setupClickListeners()
        binding.tvVersion.text = "Osmium AI v${packageManager.getPackageInfo(packageName, 0).versionName}"
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

        // Profile card → edit profile
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        // Profile details → same edit profile
        binding.btnProfileDetails.setOnClickListener {
            showEditProfileDialog()
        }

        // Password
        binding.btnPassword.setOnClickListener {
            Toast.makeText(this, "Change password coming soon", Toast.LENGTH_SHORT).show()
        }

        // Notifications
        binding.btnNotificationSettings.setOnClickListener {
            startActivity(Intent(this, NotificationSettingsActivity::class.java))
        }

        // Storage / File Manager
        binding.btnFileManager.setOnClickListener {
            startActivity(Intent(this, FileManagerActivity::class.java))
        }

        // Send Feedback
        binding.btnFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }

        // About application
        binding.btnAbout.setOnClickListener {
            showAboutDialog()
        }

        // Help/FAQ
        binding.btnHelp.setOnClickListener {
            startActivity(Intent(this, HelpCenterActivity::class.java))
        }

        // Deactivate account
        binding.btnDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }

        // Log Out
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showAboutDialog() {
        val version = packageManager.getPackageInfo(packageName, 0).versionName
        AlertDialog.Builder(this)
            .setTitle("About Osmium AI")
            .setMessage("Version $version\n\nOsmium AI is your intelligent learning companion for exam preparation.\n\n© 2026 Osmium AI. All rights reserved.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(this)
            .setTitle("Deactivate Account")
            .setMessage("Are you sure you want to deactivate your account? This action cannot be undone.")
            .setPositiveButton("Deactivate") { _, _ ->
                getSharedPreferences("OsmiumPrefs", MODE_PRIVATE).edit().clear().apply()
                SessionManager.logout(this)
                Toast.makeText(this, "Account deactivated", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
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

    private fun showEditProfileDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.findViewById<android.view.View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)

        val prefs = getSharedPreferences("OsmiumPrefs", MODE_PRIVATE)

        dialogBinding.etFullName.setText(prefs.getString("user_name", ""))
        dialogBinding.etEmail.setText(prefs.getString("user_email", ""))
        dialogBinding.etPhone.setText(prefs.getString("user_phone", ""))
        val savedExam = prefs.getString("user_exam", "JEE MAINS")
        dialogBinding.tvSelectedExam.text = savedExam

        val exams = arrayOf("JEE MAINS", "JEE ADVANCED", "NEET UG", "GATE", "UPSC", "Other")
        dialogBinding.dropdownExam.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Select Target Exam")
                .setItems(exams) { _, which ->
                    dialogBinding.tvSelectedExam.text = exams[which]
                }
                .show()
        }

        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.etFullName.text.toString().trim()
            val email = dialogBinding.etEmail.text.toString().trim()
            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Name and email cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            prefs.edit()
                .putString("user_name", name)
                .putString("user_email", email)
                .putString("user_phone", dialogBinding.etPhone.text.toString().trim())
                .putString("user_exam", dialogBinding.tvSelectedExam.text.toString())
                .apply()
            binding.tvUserName.text = name
            binding.tvUserEmail.text = email
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}
