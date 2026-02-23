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
        
        binding.btnManageSubscription.setOnClickListener {
            showSubscriptionDialog()
        }

        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnChangePassword.setOnClickListener {
            Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "Notifications ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        binding.switchReminders.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "Daily reminders ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        binding.btnLanguage.setOnClickListener {
            showLanguageDialog()
        }

        binding.btnHelp.setOnClickListener {
            Toast.makeText(this, "Help & Support", Toast.LENGTH_SHORT).show()
        }

        binding.btnPrivacy.setOnClickListener {
            Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show()
        }

        binding.btnAbout.setOnClickListener {
            Toast.makeText(this, "About Osmium App", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showSubscriptionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Manage Subscription")
            .setMessage("Your Premium Plan is active until Dec 31, 2024.\n\nWould you like to:")
            .setPositiveButton("Renew") { _, _ ->
                Toast.makeText(this, "Redirecting to payment...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel Subscription") { _, _ ->
                Toast.makeText(this, "Subscription cancelled", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Close", null)
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

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Hindi", "Spanish", "French")
        AlertDialog.Builder(this)
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                Toast.makeText(this, "Language: ${languages[which]}", Toast.LENGTH_SHORT).show()
            }
            .show()
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
