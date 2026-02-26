package com.osmiumai.app.ui.ai_mentor

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import java.io.File

class AiMentorFragment : Fragment() {

    private var _binding: FragmentAiMentorBinding? = null
    private val binding get() = _binding!!
    private var isProUser = false
    private var currentPhotoUri: Uri? = null
    private var pendingAttachmentUri: Uri? = null
    private var pendingAttachmentType: String? = null
    
    private val speechRecognizerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
            matches?.firstOrNull()?.let { text ->
                binding.chatInput.setText(text)
                binding.chatInput.setSelection(text.length)
            }
        }
    }
    
    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }
    
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                showAttachmentPreview(uri, "image")
            }
        }
    }
    
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { showAttachmentPreview(it, "image") }
    }
    
    private val fileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { showAttachmentPreview(it, "file") }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiMentorBinding.inflate(inflater, container, false)
        
        setupProBadge()
        setupDrawer()
        setupChatInput()
        setupButtons()
        setupActionChips()
        setupChatMenuButton()
        
        return binding.root
    }
    
    private fun setupProBadge() {
        binding.proBadge.isVisible = isProUser
        binding.proBadgeChat.isVisible = isProUser
    }
    
    private fun setupDrawer() {
        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        
        binding.newChat.setOnClickListener {
            clearChat()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }
    
    private fun clearChat() {
        binding.chatContainer.removeAllViews()
        binding.welcomeContainer.isVisible = true
        binding.chatContainerLayout.isVisible = false
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
            if (pendingAttachmentUri != null) {
                sendMessageWithAttachment(message)
            } else if (message.isNotEmpty()) {
                sendMessage(message)
                binding.chatInput.text.clear()
            }
        }
        
        binding.chatInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.root.postDelayed({
                    binding.bottomInputArea.requestFocus()
                    binding.chatInput.requestFocus()
                    scrollToBottom()
                }, 100)
            }
        }
    }
    
    private fun setupButtons() {
        binding.attachmentButton.setOnClickListener {
            toggleAttachmentOptions()
        }
        
        binding.optionCamera.setOnClickListener {
            openCamera()
            hideAttachmentOptions()
        }
        
        binding.optionPhoto.setOnClickListener {
            openGallery()
            hideAttachmentOptions()
        }
        
        binding.optionFile.setOnClickListener {
            openFilePicker()
            hideAttachmentOptions()
        }
        
        binding.micButton.setOnClickListener {
            startVoiceInput()
        }
    }
    
    private fun toggleAttachmentOptions() {
        binding.attachmentOptions.isVisible = !binding.attachmentOptions.isVisible
    }
    
    private fun hideAttachmentOptions() {
        binding.attachmentOptions.isVisible = false
    }
    
    private fun startVoiceInput() {
        try {
            val intent = Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())
                putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Voice input not available", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openCamera() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun launchCamera() {
        try {
            val photoFile = File(requireContext().cacheDir, "photo_${System.currentTimeMillis()}.jpg")
            currentPhotoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )
            cameraLauncher.launch(currentPhotoUri)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
    
    private fun openFilePicker() {
        fileLauncher.launch("*/*")
    }
    
    private fun showAttachmentPreview(uri: Uri, type: String) {
        pendingAttachmentUri = uri
        pendingAttachmentType = type
        hideAttachmentOptions()
        
        binding.attachmentPreviewContainer.isVisible = true
        binding.attachmentPreviewLayout.removeAllViews()
        
        val previewCard = androidx.cardview.widget.CardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                marginEnd = 16
            }
            radius = 16f
            cardElevation = 4f
            
            if (type == "image") {
                val imageView = ImageView(requireContext()).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageURI(uri)
                }
                addView(imageView)
            } else {
                setCardBackgroundColor(0xFFF5F5F5.toInt())
                val fileLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = android.view.Gravity.CENTER
                    setPadding(20, 20, 20, 20)
                    
                    addView(ImageView(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(60, 60)
                        setImageResource(R.drawable.ic_file_attach)
                        setColorFilter(0xFF1976D2.toInt())
                    })
                    
                    addView(TextView(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { topMargin = 12 }
                        text = getFileName(uri)
                        textSize = 12f
                        setTextColor(0xFF1E1E1E.toInt())
                        maxLines = 2
                        ellipsize = android.text.TextUtils.TruncateAt.MIDDLE
                    })
                }
                addView(fileLayout)
            }
            
            val closeButton = ImageView(requireContext()).apply {
                layoutParams = android.widget.FrameLayout.LayoutParams(32, 32).apply {
                    gravity = android.view.Gravity.TOP or android.view.Gravity.END
                    setMargins(8, 8, 8, 8)
                }
                setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                setColorFilter(0xFFFFFFFF.toInt())
                setBackgroundResource(android.R.drawable.btn_default)
                background.setTint(0x88000000.toInt())
                setOnClickListener { clearAttachmentPreview() }
            }
            addView(closeButton)
        }
        
        binding.attachmentPreviewLayout.addView(previewCard)
        binding.sendButton.isVisible = true
        binding.micButton.isVisible = false
    }
    
    private fun clearAttachmentPreview() {
        pendingAttachmentUri = null
        pendingAttachmentType = null
        binding.attachmentPreviewContainer.isVisible = false
        binding.attachmentPreviewLayout.removeAllViews()
        if (binding.chatInput.text.isEmpty()) {
            binding.sendButton.isVisible = false
            binding.micButton.isVisible = true
        }
    }
    
    private fun sendMessageWithAttachment(message: String) {
        binding.welcomeContainer.isVisible = false
        binding.chatContainerLayout.isVisible = true
        
        val messageContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 20, 0, 20)
            }
        }
        
        pendingAttachmentUri?.let { uri ->
            if (pendingAttachmentType == "image") {
                androidx.cardview.widget.CardView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    radius = 24f
                    cardElevation = 4f
                    
                    val imageView = ImageView(requireContext()).apply {
                        layoutParams = ViewGroup.LayoutParams(600, 600)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        setImageURI(uri)
                    }
                    addView(imageView)
                    messageContainer.addView(this)
                }
            } else {
                val fileName = getFileName(uri)
                androidx.cardview.widget.CardView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    radius = 16f
                    cardElevation = 2f
                    setCardBackgroundColor(0xFFF5F5F5.toInt())
                    
                    val fileLayout = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = android.view.Gravity.CENTER_VERTICAL
                        setPadding(40, 30, 40, 30)
                        
                        addView(ImageView(requireContext()).apply {
                            layoutParams = LinearLayout.LayoutParams(48, 48)
                            setImageResource(R.drawable.ic_file_attach)
                            setColorFilter(0xFF1976D2.toInt())
                        })
                        
                        addView(TextView(requireContext()).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply { marginStart = 24 }
                            text = fileName
                            textSize = 15f
                            setTextColor(0xFF1E1E1E.toInt())
                            maxWidth = 600
                        })
                    }
                    addView(fileLayout)
                    messageContainer.addView(this)
                }
            }
        }
        
        if (message.isNotEmpty()) {
            val textView = TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = 16 }
                text = message
                textSize = 16f
                setTextColor(0xFF1E1E1E.toInt())
                setPadding(0, 0, 0, 0)
            }
            messageContainer.addView(textView)
        }
        
        binding.chatContainer.addView(messageContainer)
        scrollToBottom()
        
        binding.chatInput.text.clear()
        clearAttachmentPreview()
        
        generateAIResponseForMedia(pendingAttachmentType ?: "image")
    }
    
    private fun getFileName(uri: Uri): String {
        var name = "file"
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
    
    private fun setupActionChips() {
        binding.chipStudyPlan.setOnClickListener {
            sendMessage("What does my name mean?")
        }
        
        binding.chipWeakAreas.setOnClickListener {
            sendMessage("Help me plan a weekend trip")
        }
        
        binding.chipStudyNext.setOnClickListener {
            sendMessage("Latest AI news in India")
        }
    }
    
    private fun sendMessage(message: String) {
        // Hide welcome container and show chat
        binding.welcomeContainer.isVisible = false
        binding.chatContainerLayout.isVisible = true
        
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
            copyToClipboard(message)
        }
        
        userQueryBinding.editButton.setOnClickListener {
            editMessage(message)
        }
        
        binding.chatContainer.addView(userQueryBinding.root)
        scrollToBottom()
    }
    
    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("message", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    
    private fun editMessage(message: String) {
        binding.chatInput.setText(message)
        binding.chatInput.setSelection(message.length)
        binding.chatInput.requestFocus()
    }
    
    private fun addAIResponse(response: String) {
        val aiResponseBinding = ItemAiResponseBinding.inflate(layoutInflater)
        
        // Set intro text
        aiResponseBinding.aiIntroText.text = "Below is a clear, structured explanation, followed by common mistakes students make. This is written in a student-friendly, exam-oriented way (JEE / NEET aligned)."
        
        // Parse and add structured content
        parseAndAddContent(response, aiResponseBinding.aiContentContainer)
        
        // Add copy functionality
        aiResponseBinding.copyResponseButton.setOnClickListener {
            copyToClipboard(response)
        }
        
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
    
    private fun generateAIResponseForMedia(type: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            val response = when(type) {
                "image" -> "**Image Analysis**\n\nI can see the image you've uploaded. How can I help you with this?\n\n• Explain concepts shown\n• Solve problems\n• Provide detailed analysis"
                else -> "**File Received**\n\nI've received your file. What would you like me to do with it?\n\n• Analyze content\n• Answer questions\n• Provide explanations"
            }
            addAIResponse(response)
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
