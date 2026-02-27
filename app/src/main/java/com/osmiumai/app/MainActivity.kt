package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.osmiumai.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        
        navView.setupWithNavController(navController)
        setupBottomNavAvatar()
        
        navView.setOnItemSelectedListener { item ->
            navController.popBackStack(navController.graph.startDestinationId, false)
            if (item.itemId != navController.graph.startDestinationId) {
                navController.navigate(item.itemId)
            }
            true
        }
        
        intent.getStringExtra("navigate_to")?.let { destination ->
            when (destination) {
                "ai_mentor" -> {
                    navController.navigate(R.id.navigation_ai_mentor)
                    navView.selectedItemId = R.id.navigation_ai_mentor
                }
            }
        }
    }
    
    private fun setupBottomNavAvatar() {
        binding.navView.post {
            val menuView = binding.navView.getChildAt(0) as? android.view.ViewGroup
            menuView?.let {
                for (i in 0 until it.childCount) {
                    val item = it.getChildAt(i) as? android.view.ViewGroup
                    if (item?.id == R.id.navigation_profile) {
                        val icon = item.getChildAt(0) as? android.widget.ImageView
                        icon?.let { img ->
                            val parent = img.parent as? android.view.ViewGroup
                            val cardView = androidx.cardview.widget.CardView(this).apply {
                                layoutParams = android.widget.FrameLayout.LayoutParams(
                                    (24 * resources.displayMetrics.density).toInt(),
                                    (24 * resources.displayMetrics.density).toInt()
                                )
                                radius = 12f * resources.displayMetrics.density
                                cardElevation = 0f
                                addView(WebView(this@MainActivity).apply {
                                    settings.javaScriptEnabled = false
                                    loadUrl("https://api.dicebear.com/9.x/glass/svg?seed=Jameson")
                                })
                            }
                            parent?.addView(cardView)
                            img.visibility = View.GONE
                        }
                        break
                    }
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
