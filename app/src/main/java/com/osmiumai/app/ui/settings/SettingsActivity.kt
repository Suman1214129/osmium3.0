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
        
        // Account
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
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

        binding.btnFeedback.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=$packageName")))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
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

        // Fix black corners
        dialog.window?.findViewById<android.view.View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)

        val prefs = getSharedPreferences("OsmiumPrefs", MODE_PRIVATE)

        // Pre-fill all fields
        dialogBinding.etFullName.setText(prefs.getString("user_name", ""))
        dialogBinding.etEmail.setText(prefs.getString("user_email", ""))
        dialogBinding.etPhone.setText(prefs.getString("user_phone", ""))
        val savedExam = prefs.getString("user_exam", "JEE MAINS")
        dialogBinding.tvSelectedExam.text = savedExam

        // Exam picker
        val exams = arrayOf("JEE MAINS", "JEE ADVANCED", "NEET UG", "GATE", "UPSC", "Other")
        dialogBinding.dropdownExam.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
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
