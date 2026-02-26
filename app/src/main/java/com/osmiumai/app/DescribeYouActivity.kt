package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.osmiumai.app.databinding.ActivityDescribeYouBinding

class DescribeYouActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDescribeYouBinding
    private var selectedLearningChip: TextView? = null
    private var selectedSkillChip: TextView? = null
    private var selectedCareerChip: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        
        binding = ActivityDescribeYouBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLearningChips()
        setupSections()
        setupSkillsChips()
        setupCareerChips()

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnGetStarted.setOnClickListener {
            if (selectedLearningChip == null) {
                Toast.makeText(this, "Please select your learning journey", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedSkillChip == null) {
                Toast.makeText(this, "Please select skills you're interested in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedCareerChip == null) {
                Toast.makeText(this, "Please select your career path", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, PlanSelectionActivity::class.java))
            finish()
        }
    }
    
    private fun setupLearningChips() {
        val chips = listOf<TextView>(
            findViewById(R.id.chip1), findViewById(R.id.chip2), findViewById(R.id.chip3), findViewById(R.id.chip4),
            findViewById(R.id.chip5), findViewById(R.id.chip6), findViewById(R.id.chip7), findViewById(R.id.chip8)
        )
        
        chips.forEach { chip ->
            chip.setBackgroundResource(R.drawable.bg_chip_unselected)
            chip.setTextColor(0xFF616161.toInt())
            chip.setOnClickListener {
                selectedLearningChip?.let { previous ->
                    previous.setBackgroundResource(R.drawable.bg_chip_unselected)
                    previous.setTextColor(0xFF616161.toInt())
                }
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(0xFFFFFFFF.toInt())
                selectedLearningChip = chip
            }
        }
    }
    
    private fun setupSkillsChips() {
        val etSkillOther = findViewById<EditText>(R.id.etSkillOther)
        val skillChips = listOf(
            findViewById<TextView>(R.id.chipSkillProgramming),
            findViewById<TextView>(R.id.chipSkillMath),
            findViewById<TextView>(R.id.chipSkillScience),
            findViewById<TextView>(R.id.chipSkillOther)
        )
        
        skillChips.forEach { chip ->
            chip.setOnClickListener {
                selectedSkillChip?.let { previous ->
                    previous.setBackgroundResource(R.drawable.bg_chip_unselected)
                    previous.setTextColor(0xFF616161.toInt())
                }
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(0xFFFFFFFF.toInt())
                selectedSkillChip = chip
                
                if (chip.id == R.id.chipSkillOther) {
                    etSkillOther.visibility = View.VISIBLE
                } else {
                    etSkillOther.visibility = View.GONE
                }
            }
        }
    }
    
    private fun setupCareerChips() {
        val etCareerOther = findViewById<EditText>(R.id.etCareerOther)
        val careerChips = listOf(
            findViewById<TextView>(R.id.chipCareerEngineering),
            findViewById<TextView>(R.id.chipCareerMedical),
            findViewById<TextView>(R.id.chipCareerCivil),
            findViewById<TextView>(R.id.chipCareerOther)
        )
        
        careerChips.forEach { chip ->
            chip.setOnClickListener {
                selectedCareerChip?.let { previous ->
                    previous.setBackgroundResource(R.drawable.bg_chip_unselected)
                    previous.setTextColor(0xFF616161.toInt())
                }
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(0xFFFFFFFF.toInt())
                selectedCareerChip = chip
                
                if (chip.id == R.id.chipCareerOther) {
                    etCareerOther.visibility = View.VISIBLE
                } else {
                    etCareerOther.visibility = View.GONE
                }
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
