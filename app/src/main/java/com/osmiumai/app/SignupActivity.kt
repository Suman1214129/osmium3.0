package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var selectedCountryCode = "+91"
    private var selectedCountryName = "India (+91)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.containerCountry.setOnClickListener {
            showCountryPicker()
        }

        binding.fabNext.setOnClickListener {
            val phoneNumber = binding.etPhone.text.toString().trim()
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phoneNumber.length < 10) {
                Toast.makeText(this, "Please enter valid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val phone = "${binding.tvCountryCode.text}${phoneNumber}"
            val intent = Intent(this, VerifyOtpActivity::class.java)
            intent.putExtra("phone", phone)
            startActivity(intent)
        }
        
        binding.btnGoogleSignup.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    
    private fun showCountryPicker() {
        val countries = arrayOf(
            "India (+91)",
            "United States (+1)",
            "United Kingdom (+44)",
            "Canada (+1)",
            "Australia (+61)",
            "Germany (+49)",
            "France (+33)",
            "Japan (+81)",
            "China (+86)",
            "Brazil (+55)"
        )
        
        val codes = arrayOf("+91", "+1", "+44", "+1", "+61", "+49", "+33", "+81", "+86", "+55")
        
        AlertDialog.Builder(this)
            .setTitle("Select Country")
            .setItems(countries) { _, which ->
                selectedCountryName = countries[which]
                selectedCountryCode = codes[which]
                binding.tvCountry.text = selectedCountryName
                binding.tvCountryCode.text = selectedCountryCode
            }
            .show()
    }
}
