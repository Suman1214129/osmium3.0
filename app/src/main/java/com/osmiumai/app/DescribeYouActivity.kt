package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.osmiumai.app.databinding.ActivityDescribeYouBinding

class DescribeYouActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDescribeYouBinding
    private var selectedChip: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        
        binding = ActivityDescribeYouBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupChips()
        setupSections()

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    
    private fun setupChips() {
        val chips = listOf<TextView>(
            findViewById(R.id.chip1), findViewById(R.id.chip2), findViewById(R.id.chip3), findViewById(R.id.chip4),
            findViewById(R.id.chip5), findViewById(R.id.chip6), findViewById(R.id.chip7), findViewById(R.id.chip8)
        )
        
        selectedChip = findViewById(R.id.chip1)
        
        chips.forEach { chip ->
            chip.setOnClickListener {
                selectedChip?.let { previous ->
                    previous.setBackgroundResource(R.drawable.bg_chip_unselected)
                    previous.setTextColor(0xFF616161.toInt())
                }
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(0xFFFFFFFF.toInt())
                selectedChip = chip
            }
        }
    }
    
    private fun setupSections() {
        findViewById<LinearLayout>(R.id.section1Header).setOnClickListener {
            toggleSection(findViewById(R.id.section1Content), findViewById(R.id.arrow1))
        }
        
        findViewById<LinearLayout>(R.id.section2Header).setOnClickListener {
            toggleSection(findViewById(R.id.section2Content), findViewById(R.id.arrow2))
        }
        
        findViewById<LinearLayout>(R.id.section3Header).setOnClickListener {
            toggleSection(findViewById(R.id.section3Content), findViewById(R.id.arrow3))
        }
    }
    
    private fun toggleSection(content: LinearLayout, arrow: ImageView) {
        if (content.visibility == View.VISIBLE) {
            content.visibility = View.GONE
            arrow.setImageResource(R.drawable.ic_arrow_down)
        } else {
            content.visibility = View.VISIBLE
            arrow.setImageResource(R.drawable.ic_arrow_up)
        }
    }
}
