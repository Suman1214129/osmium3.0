package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.databinding.ActivityVerifyOtpBinding

class VerifyOtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyOtpBinding
    private var phoneNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumber = intent.getStringExtra("phone") ?: ""
        binding.tvSubtitle.text = "Enter OTP to $phoneNumber"

        setupOtpInputs()
        startResendTimer()

        binding.fabVerify.setOnClickListener {
            val otp = "${binding.etOtp1.text}${binding.etOtp2.text}${binding.etOtp3.text}${binding.etOtp4.text}${binding.etOtp5.text}${binding.etOtp6.text}"
            if (otp.length < 6) {
                Toast.makeText(this, "Please enter complete OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, ProfileDetailsActivity::class.java))
            finish()
        }
    }

    private fun setupOtpInputs() {
        val otpFields = listOf(
            binding.etOtp1, binding.etOtp2, binding.etOtp3,
            binding.etOtp4, binding.etOtp5, binding.etOtp6
        )
        
        val lines = listOf(
            binding.line1, binding.line2, binding.line3,
            binding.line4, binding.line5, binding.line6
        )

        otpFields.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        lines[index].setBackgroundColor(0xFF1E1E1E.toInt())
                        if (index < otpFields.size - 1) {
                            otpFields[index + 1].requestFocus()
                        }
                    } else {
                        lines[index].setBackgroundColor(0xFFBDBDBD.toInt())
                    }
                }
            })
        }

        binding.etOtp1.requestFocus()
    }

    private fun startResendTimer() {
        object : CountDownTimer(36000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.tvResendOtp.text = "Request a new OTP in 00:${String.format("%02d", seconds)}"
            }

            override fun onFinish() {
                binding.tvResendOtp.text = "Resend OTP"
                binding.tvResendOtp.setOnClickListener {
                    startResendTimer()
                }
            }
        }.start()
    }
}
