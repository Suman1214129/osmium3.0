package com.osmiumai.app.ui.ai_mentor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.osmiumai.app.R
import com.osmiumai.app.databinding.FragmentAiMentorBinding
import com.osmiumai.app.databinding.ItemUserQueryBinding
import com.osmiumai.app.databinding.ItemAiResponseBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AiMentorFragment : Fragment() {

    private var _binding: FragmentAiMentorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiMentorBinding.inflate(inflater, container, false)
        
        setupDrawer()
        setupChatInput()
        setupButtons()
        setupActionChips()
        setupChatMenuButton()
        
        return binding.root
    }
    
    private fun setupDrawer() {
        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        
        binding.closeDrawer.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        
        binding.newChat.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        
        binding.searchChat.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }
    
    private fun setupChatMenuButton() {
        binding.chatMenuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    
    private fun setupChatInput() {
        binding.chatInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrEmpty()
                binding.sendButton.isVisible = hasText
                binding.micButton.isVisible = !hasText
            }
        })
        
        binding.sendButton.setOnClickListener {
            val message = binding.chatInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                binding.chatInput.text.clear()
            }
        }
    }
    
    private fun setupButtons() {
        binding.settingsButton.setOnClickListener {
            // Handle settings
        }
        
        binding.micButton.setOnClickListener {
            // Handle voice input
        }
    }
    
    private fun setupActionChips() {
        binding.chipStudyPlan.setOnClickListener {
            sendMessage("Start today's study plan")
        }
        
        binding.chipWeakAreas.setOnClickListener {
            sendMessage("Analyze my weak areas")
        }
        
        binding.chipStudyNext.setOnClickListener {
            sendMessage("What should I study next?")
        }
        
        binding.chipExamTopics.setOnClickListener {
            sendMessage("Predict important exam topics")
        }
    }
    
    private fun sendMessage(message: String) {
        // Hide welcome container and show chat
        binding.welcomeContainer.isVisible = false
        binding.chatScrollView.isVisible = true
        
        // Add user message
        addUserMessage(message)
        
        // Generate AI response
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000) // Simulate thinking time
            val aiResponse = generateAIResponse(message)
            addAIResponse(aiResponse)
        }
    }
    
    private fun addUserMessage(message: String) {
        val userQueryBinding = ItemUserQueryBinding.inflate(layoutInflater)
        userQueryBinding.tvUserMessage.text = message
        
        userQueryBinding.copyButton.setOnClickListener {
            // Handle copy
        }
        
        userQueryBinding.editButton.setOnClickListener {
            // Handle edit
        }
        
        binding.chatContainer.addView(userQueryBinding.root)
        scrollToBottom()
    }
    
    private fun addAIResponse(response: String) {
        val aiResponseBinding = ItemAiResponseBinding.inflate(layoutInflater)
        
        // Set intro text
        aiResponseBinding.aiIntroText.text = "Below is a clear, structured explanation, followed by common mistakes students make. This is written in a student-friendly, exam-oriented way (JEE / NEET aligned)."
        
        // Parse and add structured content
        parseAndAddContent(response, aiResponseBinding.aiContentContainer)
        
        // Add space before AI response
        val space = View(context)
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60)
        space.layoutParams = params
        binding.chatContainer.addView(space)
        
        binding.chatContainer.addView(aiResponseBinding.root)
        scrollToBottom()
    }
    
    private fun parseAndAddContent(content: String, container: LinearLayout) {
        val lines = content.split("\n")
        
        for (line in lines) {
            when {
                line.startsWith("**") && line.endsWith("**") -> {
                    // Bold heading
                    val textView = TextView(context).apply {
                        text = line.replace("**", "")
                        textSize = 16f
                        setTextColor(0xFF000000.toInt())
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                        setPadding(0, 0, 0, 30)
                    }
                    container.addView(textView)
                }
                line.matches(Regex("^\\d+\\. .*")) -> {
                    // Numbered item
                    val textView = TextView(context).apply {
                        text = line
                        textSize = 16f
                        setTextColor(0xFF000000.toInt())
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                        setPadding(0, 0, 0, 30)
                    }
                    container.addView(textView)
                }
                line.startsWith("   •") || line.startsWith("•") -> {
                    // Bullet point
                    val bulletLayout = LinearLayout(context).apply {
                        orientation = LinearLayout.HORIZONTAL
                        setPadding(0, 0, 0, 10)
                    }
                    
                    val bullet = TextView(context).apply {
                        text = "•  "
                        textSize = 15f
                        setTextColor(0xFF000000.toInt())
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    
                    val text = TextView(context).apply {
                        text = line.replace("•", "").trim()
                        textSize = 15f
                        setTextColor(0xFF1E1E1E.toInt())
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    }
                    
                    bulletLayout.addView(bullet)
                    bulletLayout.addView(text)
                    container.addView(bulletLayout)
                }
                line.isNotBlank() -> {
                    // Regular text
                    val textView = TextView(context).apply {
                        text = line
                        textSize = 15f
                        setTextColor(0xFF1E1E1E.toInt())
                        setPadding(0, 0, 0, 30)
                        setLineSpacing(12f, 1f)
                    }
                    container.addView(textView)
                }
            }
        }
    }
    
    private fun generateAIResponse(userMessage: String): String {
        return when {
            userMessage.contains("Chemical Bonding", ignoreCase = true) || userMessage.contains("study next", ignoreCase = true) -> 
                "**1. What is Chemical Bonding?**\n\nChemical bonding explains how and why atoms join to form molecules and compounds.\n\nAtoms bond to:\n• Achieve lower energy\n• Attain a stable electronic configuration (often octet or duplet)\n\n**2. What is Molecular weight?**\n\nMolecular weight explains how and why atoms join to form molecules and compounds."
            
            userMessage.contains("study plan", ignoreCase = true) -> 
                "**Your Personalized Study Plan**\n\n**1. Morning Session (9-11 AM)**\n• Review yesterday's topics\n• Focus on weak areas\n\n**2. Afternoon Session (2-4 PM)**\n• New concept learning\n• Practice problems\n\n**3. Evening Session (7-9 PM)**\n• Revision and notes\n• Mock tests"
            
            userMessage.contains("weak areas", ignoreCase = true) -> 
                "**Areas Needing Attention**\n\n**1. Chemical Bonding - 65% accuracy**\n• Focus on molecular geometry\n• Practice Lewis structures\n\n**2. Thermodynamics - 70% accuracy**\n• Review entropy concepts\n• Solve numerical problems\n\n**3. Organic Chemistry - 68% accuracy**\n• Memorize reaction mechanisms\n• Practice nomenclature"
            
            userMessage.contains("exam topics", ignoreCase = true) -> 
                "**High-Probability Exam Topics**\n\n**1. Chemical Bonding & Structure (85% chance)**\n• Lewis structures\n• VSEPR theory\n• Hybridization\n\n**2. Thermodynamics (80% chance)**\n• First law applications\n• Entropy calculations\n• Gibbs free energy\n\n**3. Organic Reactions (75% chance)**\n• Substitution mechanisms\n• Addition reactions\n• Elimination reactions"
            
            else -> 
                "**Understanding Your Query**\n\nLet me help you with: \"$userMessage\"\n\n**1. Key Concepts**\n• Start with fundamental principles\n• Build understanding step by step\n\n**2. Practice Approach**\n• Solve basic examples first\n• Progress to complex problems\n• Take regular practice tests\n\n**3. Study Tips**\n• Make comprehensive notes\n• Use visual aids and diagrams\n• Schedule regular revision sessions"
        }
    }
    
    private fun scrollToBottom() {
        binding.chatScrollView.post {
            binding.chatScrollView.fullScroll(android.view.View.FOCUS_DOWN)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
