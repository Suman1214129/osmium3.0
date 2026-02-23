package com.osmiumai.app.ui.notifications

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.osmiumai.app.databinding.ActivityNotificationsBinding

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnMarkAllRead.setOnClickListener {
            Toast.makeText(this, "All notifications marked as read", Toast.LENGTH_SHORT).show()
        }

        binding.notif1.setOnClickListener {
            Toast.makeText(this, "Opening course details", Toast.LENGTH_SHORT).show()
        }

        binding.notif2.setOnClickListener {
            Toast.makeText(this, "Opening test results", Toast.LENGTH_SHORT).show()
        }

        binding.notif3.setOnClickListener {
            Toast.makeText(this, "Opening subscription details", Toast.LENGTH_SHORT).show()
        }

        binding.notif4.setOnClickListener {
            Toast.makeText(this, "Opening daily practice", Toast.LENGTH_SHORT).show()
        }
    }
}
