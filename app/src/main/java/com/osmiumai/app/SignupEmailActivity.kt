package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.osmiumai.app.databinding.ActivitySignupEmailBinding

class SignupEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupEmailBinding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        
        binding = ActivitySignupEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye)
            } else {
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye_off)
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.fabNext.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val passwordStrength = checkPasswordStrength(password)
            if (passwordStrength != "Strong") {
                showPasswordStrengthDialog(passwordStrength)
                return@setOnClickListener
            }
            startActivity(Intent(this, ProfileDetailsActivity::class.java))
            finish()
        }
        
        binding.btnPhoneSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
        
        binding.btnGoogleSignup.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        binding.tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginEmailActivity::class.java))
            finish()
        }
    }
    
    private fun checkPasswordStrength(password: String): String {
        val hasMinLength = password.length >= 8
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        
        return when {
            hasMinLength && hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar -> "Strong"
            hasMinLength && ((hasUpperCase && hasLowerCase && hasDigit) || (hasUpperCase && hasLowerCase && hasSpecialChar)) -> "Medium"
            else -> "Weak"
        }
    }
    
    private fun showPasswordStrengthDialog(strength: String) {
        val message = when (strength) {
            "Weak" -> "Password is too weak. It must be at least 8 characters and include uppercase, lowercase, number, and special character."
            "Medium" -> "Password is medium strength. For better security, include uppercase, lowercase, number, and special character."
            else -> "Password strength: $strength"
        }
        
        AlertDialog.Builder(this)
            .setTitle("Weak Password")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
