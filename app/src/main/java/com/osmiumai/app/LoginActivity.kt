package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.osmiumai.app.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
            val phoneNumber = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phoneNumber.length != 10) {
                Toast.makeText(this, "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        binding.tvSignupLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
        
        binding.btnGoogleLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
