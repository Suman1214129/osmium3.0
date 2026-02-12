package com.example.susu

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.susu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide action bar
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        
        navView.setupWithNavController(navController)
        
        // Handle navigation from other activities
        intent.getStringExtra("navigate_to")?.let { destination ->
            when (destination) {
                "ai_mentor" -> {
                    navController.navigate(R.id.navigation_ai_mentor)
                    navView.selectedItemId = R.id.navigation_ai_mentor
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getStringExtra("navigate_to")?.let { destination ->
            when (destination) {
                "ai_mentor" -> {
                    val navController = findNavController(R.id.nav_host_fragment_activity_main)
                    navController.navigate(R.id.navigation_ai_mentor)
                    binding.navView.selectedItemId = R.id.navigation_ai_mentor
                }
            }
        }
    }
}