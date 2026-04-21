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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.osmiumai.app.R
import com.osmiumai.app.databinding.FragmentAiMentorBinding
import com.osmiumai.app.databinding.ItemUserQueryBinding
import com.osmiumai.app.databinding.ItemAiResponseBinding
import java.io.File

class AiMentorFragment : Fragment() {

    private var _binding: FragmentAiMentorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AiMentorViewModel by viewModels()
    private lateinit var historyAdapter: ChatHistoryAdapter
    private lateinit var attachmentAdapter: AttachmentPreviewAdapter

    private var currentPhotoUri: Uri? = null

    // ── Launchers ─────────────────────────────────────────────────────────────

    private val speechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()?.let { text ->
                    binding.chatInput.setText(text)
                    binding.chatInput.setSelection(text.length)
                }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchCamera()
        else Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show()
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) currentPhotoUri?.let { uri ->
            addAttachment(AttachmentItem(uri, AttachmentType.IMAGE, "photo.jpg"))
        }
        hideAttachmentOptions()
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris.forEach { uri -> addAttachment(AttachmentItem(uri, AttachmentType.IMAGE, getFileName(uri))) }
        hideAttachmentOptions()
    }

    private val fileLauncher = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris.forEach { uri ->
            try {
                requireContext().contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            addAttachment(AttachmentItem(uri, AttachmentType.FILE, getFileName(uri)))
        }
        hideAttachmentOptions()
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiMentorBinding.inflate(inflater, container, false)
        setupAttachmentPreview()
        setupDrawer()
        setupSidebar()
        setupChatInput()
        setupButtons()
        setupChips()
        observeViewModel()
        activity?.intent?.getStringExtra("question_text")?.let { sendMessage(it) }
        return binding.root
    }

    // ── Attachment preview strip ──────────────────────────────────────────────

    private fun setupAttachmentPreview() {
        attachmentAdapter = AttachmentPreviewAdapter { item ->
            attachmentAdapter.removeItem(item)
            if (attachmentAdapter.isEmpty()) {
                binding.rvAttachmentPreview.isVisible = false
                updateSendButtonState()
            }
        }
        binding.rvAttachmentPreview.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = attachmentAdapter
        }
    }

    private fun addAttachment(item: AttachmentItem) {
        attachmentAdapter.addItem(item)
        binding.rvAttachmentPreview.isVisible = true
        binding.sendButton.isVisible = true
        binding.micButton.isVisible = false
    }

    private fun clearAttachments() {
        attachmentAdapter.clear()
        binding.rvAttachmentPreview.isVisible = false
        updateSendButtonState()
    }

    // ── Drawer ────────────────────────────────────────────────────────────────

    private fun setupDrawer() {
        binding.menuButton.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
        binding.chatMenuButton.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private fun setupSidebar() {
        historyAdapter = ChatHistoryAdapter(
            onSessionClick = { session ->
                loadSession(session)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            },
            onDeleteClick = { session -> viewModel.deleteSession(session) }
        )
        binding.rvChatHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
        binding.newChat.setOnClickListener {
            startNewChat()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun startNewChat() {
        viewModel.startNewSession()
        binding.chatContainer.removeAllViews()
        binding.welcomeContainer.isVisible = true
        binding.chatContainerLayout.isVisible = false
        clearAttachments()
    }

    private fun loadSession(session: ChatSession) {
        viewModel.loadSession(session)
        binding.chatContainer.removeAllViews()
        binding.welcomeContainer.isVisible = false
        binding.chatContainerLayout.isVisible = true
        session.messages.forEach { msg ->
            if (msg.isUser) renderUserMessage(msg.text, msg.attachments)
            else renderAiMessage(msg.text)
        }
        scrollToBottom()
    }

    // ── ViewModel observers ───────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.sessions.observe(viewLifecycleOwner) { sessions ->
            historyAdapter.submitList(sessions)
            binding.tvEmptyHistory.isVisible = sessions.isEmpty()
        }
        viewModel.newMessage.observe(viewLifecycleOwner) { msg ->
            if (msg.isUser) {
                binding.welcomeContainer.isVisible = false
                binding.chatContainerLayout.isVisible = true
                renderUserMessage(msg.text, msg.attachments)
            } else {
                renderAiMessage(msg.text)
            }
            scrollToBottom()
        }
    }

    // ── Message rendering ─────────────────────────────────────────────────────

    private fun renderUserMessage(text: String, attachments: List<AttachmentItem> = emptyList()) {
        val itemBinding = ItemUserQueryBinding.inflate(layoutInflater)

        // Show attachments
        if (attachments.isNotEmpty()) {
            itemBinding.attachmentScrollView.isVisible = true
            attachments.forEach { item ->
                val thumb = buildAttachmentThumb(item, tappable = true)
                itemBinding.attachmentContainer.addView(thumb)
            }
        }

        // Show text bubble only if there's text
        if (text.isNotEmpty()) {
            itemBinding.tvUserMessage.isVisible = true
            itemBinding.tvUserMessage.text = text
        }

        itemBinding.copyButton.setOnClickListener { copyToClipboard(text) }
        itemBinding.editButton.setOnClickListener {
            binding.chatInput.setText(text)
            binding.chatInput.setSelection(text.length)
            binding.chatInput.requestFocus()
        }

        binding.chatContainer.addView(itemBinding.root)
    }

    /**
     * Builds a tappable thumbnail for an image or file attachment shown in the chat bubble.
     */
    private fun buildAttachmentThumb(item: AttachmentItem, tappable: Boolean): View {
        val ctx = requireContext()
        val dp = ctx.resources.displayMetrics.density

        return when (item.type) {
            AttachmentType.IMAGE -> {
                // Rounded image card, tappable to open full-screen
                MaterialCardView(ctx).apply {
                    val size = (80 * dp).toInt()
                    layoutParams = LinearLayout.LayoutParams(size, size).apply { marginEnd = (8 * dp).toInt() }
                    radius = 12 * dp
                    cardElevation = 0f
                    strokeWidth = (1 * dp).toInt()
                    strokeColor = 0x1A000000

                    val iv = ImageView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        setImageURI(item.uri)
                    }
                    addView(iv)

                    if (tappable) {
                        isClickable = true
                        isFocusable = true
                        setOnClickListener { openImage(item.uri) }
                    }
                }
            }

            AttachmentType.FILE -> {
                // File pill card, tappable to open with system viewer
                MaterialCardView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        (56 * dp).toInt()
                    ).apply { marginEnd = (8 * dp).toInt() }
                    radius = 12 * dp
                    cardElevation = 0f
                    setCardBackgroundColor(0xFFF0F0F0.toInt())
                    strokeWidth = (1 * dp).toInt()
                    strokeColor = 0x1A000000

                    val row = LinearLayout(ctx).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = android.view.Gravity.CENTER_VERTICAL
                        setPadding((12 * dp).toInt(), 0, (12 * dp).toInt(), 0)
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }

                    row.addView(ImageView(ctx).apply {
                        layoutParams = LinearLayout.LayoutParams((20 * dp).toInt(), (20 * dp).toInt()).apply {
                            marginEnd = (8 * dp).toInt()
                        }
                        setImageResource(R.drawable.ic_file_attach)
                        setColorFilter(0xFF616161.toInt())
                    })

                    row.addView(TextView(ctx).apply {
                        text = item.name.take(24).let { if (item.name.length > 24) "$it…" else it }
                        textSize = 12f
                        setTextColor(0xFF1E1E1E.toInt())
                        maxLines = 1
                    })

                    addView(row)

                    if (tappable) {
                        isClickable = true
                        isFocusable = true
                        setOnClickListener { openFile(item.uri, item.name) }
                    }
                }
            }
        }
    }

    private fun openImage(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Open image"))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No app to open image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFile(uri: Uri, name: String) {
        try {
            val mime = requireContext().contentResolver.getType(uri) ?: "*/*"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Open $name"))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No app to open this file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderAiMessage(text: String) {
        val spacer = View(context)
        spacer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 32)
        binding.chatContainer.addView(spacer)

        val itemBinding = ItemAiResponseBinding.inflate(layoutInflater)
        itemBinding.aiIntroText.text = "Osmium AI"
        parseAndAddContent(text, itemBinding.aiContentContainer)
        itemBinding.copyResponseButton.setOnClickListener { copyToClipboard(text) }
        binding.chatContainer.addView(itemBinding.root)
    }

    private fun parseAndAddContent(content: String, container: LinearLayout) {
        content.split("\n").forEach { line ->
            when {
                line.startsWith("**") && line.endsWith("**") -> container.addView(
                    TextView(context).apply {
                        text = line.replace("**", "")
                        textSize = 15f
                        setTextColor(0xFF1E1E1E.toInt())
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                        setPadding(0, 0, 0, 20)
                    })
                line.startsWith("• ") || line.startsWith("•") -> {
                    val row = LinearLayout(context).apply {
                        orientation = LinearLayout.HORIZONTAL; setPadding(0, 0, 0, 8)
                    }
                    row.addView(TextView(context).apply { text = "•  "; textSize = 14f; setTextColor(0xFF616161.toInt()) })
                    row.addView(TextView(context).apply {
                        text = line.removePrefix("•").trim(); textSize = 14f
                        setTextColor(0xFF1E1E1E.toInt())
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                        setLineSpacing(4f, 1f)
                    })
                    container.addView(row)
                }
                line.isNotBlank() -> container.addView(TextView(context).apply {
                    text = line; textSize = 14f; setTextColor(0xFF1E1E1E.toInt())
                    setPadding(0, 0, 0, 16); setLineSpacing(4f, 1f)
                })
            }
        }
    }

    // ── Input ─────────────────────────────────────────────────────────────────

    private fun setupChatInput() {
        binding.chatInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { updateSendButtonState() }
        })
        binding.sendButton.setOnClickListener { dispatchSend() }
    }

    private fun updateSendButtonState() {
        val hasText = binding.chatInput.text.isNotEmpty()
        val hasAttachments = !attachmentAdapter.isEmpty()
        binding.sendButton.isVisible = hasText || hasAttachments
        binding.micButton.isVisible = !hasText && !hasAttachments
    }

    private fun dispatchSend() {
        val text = binding.chatInput.text.toString().trim()
        val attachments = attachmentAdapter.getItems()
        if (attachments.isEmpty() && text.isEmpty()) return

        viewModel.sendMessage(text, attachments)
        binding.chatInput.text.clear()
        clearAttachments()
        hideAttachmentOptions()
    }

    private fun sendMessage(text: String) {
        viewModel.sendMessage(text)
    }

    // ── Chips ─────────────────────────────────────────────────────────────────

    private fun setupChips() {
        binding.chipStudyPlan.setOnClickListener { sendMessage("Generate notes on Newton's Laws of Motion") }
        binding.chipWeakAreas.setOnClickListener { sendMessage("Clear my doubt on photosynthesis") }
        binding.chipStudyNext.setOnClickListener { sendMessage("Solve this maths question: find the derivative of x²") }
        binding.chipQuickStart.setOnClickListener { sendMessage("Make a study plan for JEE Mains in 3 months") }
    }

    // ── Attachment buttons ────────────────────────────────────────────────────

    private fun setupButtons() {
        binding.attachmentButton.setOnClickListener {
            binding.attachmentOptions.isVisible = !binding.attachmentOptions.isVisible
        }
        binding.optionCamera.setOnClickListener { openCamera() }
        binding.optionPhoto.setOnClickListener { galleryLauncher.launch("image/*") }
        binding.optionFile.setOnClickListener {
            fileLauncher.launch(arrayOf("application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "text/plain", "image/*", "*/*"))
        }
        binding.micButton.setOnClickListener { startVoiceInput() }
    }

    private fun hideAttachmentOptions() { binding.attachmentOptions.isVisible = false }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) launchCamera()
        else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun launchCamera() {
        try {
            val file = File(requireContext().cacheDir, "photo_${System.currentTimeMillis()}.jpg")
            currentPhotoUri = FileProvider.getUriForFile(
                requireContext(), "${requireContext().packageName}.provider", file)
            cameraLauncher.launch(currentPhotoUri)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startVoiceInput() {
        try {
            val intent = Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())
                putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Voice input not available", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun getFileName(uri: Uri): String {
        var name = "file"
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val col = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && col >= 0) name = cursor.getString(col)
        }
        return name
    }

    private fun copyToClipboard(text: String) {
        val cb = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cb.setPrimaryClip(ClipData.newPlainText("message", text))
        Toast.makeText(requireContext(), "Copied", Toast.LENGTH_SHORT).show()
    }

    private fun scrollToBottom() {
        binding.chatScrollView.post { binding.chatScrollView.fullScroll(View.FOCUS_DOWN) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
