package com.osmiumai.app.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.osmiumai.app.R

class ChoosePlanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_plan)

        supportActionBar?.hide()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        findViewById<android.widget.ImageView>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.btnManageSubscription)?.setOnClickListener {
            Toast.makeText(this, "Manage Subscription", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.btnGetPlus)?.setOnClickListener {
            Toast.makeText(this, "Upgrading to Plus plan...", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.btnGetPro)?.setOnClickListener {
            Toast.makeText(this, "Upgrading to Pro plan...", Toast.LENGTH_SHORT).show()
        }

        findViewById<android.widget.TextView>(R.id.btnRestoreSubscription)?.setOnClickListener {
            Toast.makeText(this, "Restoring subscription...", Toast.LENGTH_SHORT).show()
        }
    }
}
