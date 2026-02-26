package com.osmiumai.app

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.databinding.ActivityProfileDetailsBinding
import java.util.Calendar

class ProfileDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        
        binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBirthdayPicker()
        setupNameInput()

        binding.btnContinue.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val birthday = binding.etBirthday.text.toString().trim()
            
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (name.length < 3 || !name.contains(" ")) {
                Toast.makeText(this, "Please enter your full name (first and last name)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (birthday.isEmpty()) {
                Toast.makeText(this, "Please enter your birthday", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            startActivity(Intent(this, DescribeYouActivity::class.java))
            finish()
        }
    }
    
    private fun setupBirthdayPicker() {
        binding.etBirthday.isFocusable = false
        binding.etBirthday.isClickable = true
        
        binding.etBirthday.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d / %02d / %d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etBirthday.setText(formattedDate)
            }, year, month, day).show()
        }
    }
    
    private fun setupNameInput() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotEmpty() && !text[0].isUpperCase()) {
                    val capitalized = text.substring(0, 1).uppercase() + text.substring(1)
                    binding.etName.removeTextChangedListener(this)
                    binding.etName.setText(capitalized)
                    binding.etName.setSelection(capitalized.length)
                    binding.etName.addTextChangedListener(this)
                }
            }
        })
    }
}
