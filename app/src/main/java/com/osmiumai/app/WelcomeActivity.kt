package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.osmiumai.app.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Susu)
        
        supportActionBar?.hide()
        
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = WelcomePagerAdapter(this)
        binding.viewPager.isUserInputEnabled = true
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.progressBar.progress = (position + 1) * 25
            }
        })
        
        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    
    fun nextPage() {
        if (binding.viewPager.currentItem < 3) {
            binding.viewPager.currentItem += 1
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    
    inner class WelcomePagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount() = 4
        override fun createFragment(position: Int): Fragment = WelcomePageFragment.newInstance(position)
    }
}
