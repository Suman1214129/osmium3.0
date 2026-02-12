package com.example.susu

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.susu.databinding.ActivityTopicLearnBinding
import com.example.susu.databinding.DialogLessonSelectorBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class TopicLearnActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTopicLearnBinding
    private var popupWindow: PopupWindow? = null
    private val youtubeVideoId = "4OVptMIxsT4"
    
    private var currentFlashcardIndex = 0
    private var isShowingAnswer = false
    
    private val flashcards = listOf(
        Pair("What is the difference between accuracy and precision?", "Accuracy refers to how close a measurement is to the true value, while precision refers to how consistent repeated measurements are."),
        Pair("What is user-centric design?", "User-centric design is an approach that places the user's needs, preferences, and limitations at the center of the design process."),
        Pair("What is a wireframe?", "A wireframe is a basic visual guide that represents the skeletal framework of a website or application."),
        Pair("What is usability testing?", "Usability testing is a technique used to evaluate a product by testing it with representative users."),
        Pair("What is a persona in UX design?", "A persona is a fictional character created to represent a user type that might use a product in a similar way."),
        Pair("What is A/B testing?", "A/B testing is a method of comparing two versions of a webpage or app to determine which performs better."),
        Pair("What is accessibility in design?", "Accessibility ensures that products and services are usable by people with disabilities."),
        Pair("What is an MVP?", "MVP stands for Minimum Viable Product - a version with just enough features to satisfy early customers."),
        Pair("What is information architecture?", "Information architecture is the structural design of shared information environments."),
        Pair("What is responsive design?", "Responsive design is an approach that makes web pages render well on various devices and screen sizes.")
    )
    
    private var currentQuestionIndex = 0
    private val userAnswers = mutableMapOf<Int, Int>()
    
    private val questions = listOf(
        Triple("What is the primary goal of user-centric design?", 
            listOf("Aesthetic perfection", "Business scalability", "Solving real user problems", "Technical optimization"), 2),
        Triple("Which phase comes first in the UCD process?", 
            listOf("Testing", "Research", "Design", "Implementation"), 1),
        Triple("What is a persona in UX design?", 
            listOf("A fictional character", "A real user", "A user archetype", "A designer"), 2),
        Triple("What does usability testing measure?", 
            listOf("Visual appeal", "User satisfaction", "Code quality", "Loading speed"), 1),
        Triple("What is the purpose of wireframing?", 
            listOf("Final design", "Visual layout structure", "Color scheme", "Typography"), 1),
        Triple("Which method is best for gathering user feedback?", 
            listOf("Assumptions", "User interviews", "Guessing", "Copying competitors"), 1),
        Triple("What is A/B testing used for?", 
            listOf("Comparing two versions", "Testing bugs", "Security testing", "Performance testing"), 0),
        Triple("What does accessibility in design mean?", 
            listOf("Fast loading", "Usable by everyone", "Mobile-friendly", "Colorful design"), 1),
        Triple("What is the main benefit of user research?", 
            listOf("Faster development", "Understanding user needs", "Lower costs", "Better graphics"), 1),
        Triple("What is an MVP in product design?", 
            listOf("Most Valuable Player", "Minimum Viable Product", "Maximum Value Proposition", "Minimal Visual Prototype"), 1)
    )
    
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.hide()
        
        binding.ivBack.setOnClickListener {
            finish()
        }
        
        binding.titleRow.setOnClickListener {
            showLessonSelectorPopup(it)
        }
        
        setupTabClickListeners()
        loadVideoThumbnail()
        setupPlayButton()
        
        // Check if we should open podcast tab
        val selectedTab = intent.getStringExtra("SELECTED_TAB")
        if (selectedTab == "podcast") {
            switchToPodcast()
        }
    }
    
    private fun setupTabClickListeners() {
        binding.tabLearn.setOnClickListener {
            switchToLearn()
        }
        
        binding.tabPodcast.setOnClickListener {
            switchToPodcast()
        }
        
        binding.tabQuiz.setOnClickListener {
            switchToQuiz()
        }
        
        binding.tabFlashcard.setOnClickListener {
            switchToFlashcard()
        }
        
        binding.tabQBank.setOnClickListener {
            switchToQBank()
        }
    }
    
    private fun switchToLearn() {
        binding.root.findViewById<View>(R.id.learnContent).visibility = View.VISIBLE
        binding.root.findViewById<View>(R.id.podcastContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.quizContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.quizResultContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.flashcardContent).visibility = View.GONE
        
        binding.iconLearn.setColorFilter(ContextCompat.getColor(this, R.color.black))
        binding.textLearn.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.indicatorLearn.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        
        binding.iconPodcast.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textPodcast.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorPodcast.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        binding.iconQuiz.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textQuiz.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorQuiz.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        binding.iconFlashcard.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textFlashcard.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorFlashcard.setBackgroundColor(android.graphics.Color.TRANSPARENT)
    }
    
    private fun switchToPodcast() {
        binding.root.findViewById<View>(R.id.learnContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.podcastContent).visibility = View.VISIBLE
        binding.root.findViewById<View>(R.id.quizContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.quizResultContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.flashcardContent).visibility = View.GONE
        
        binding.iconPodcast.setColorFilter(ContextCompat.getColor(this, R.color.black))
        binding.textPodcast.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.indicatorPodcast.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        
        binding.iconLearn.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textLearn.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorLearn.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        binding.iconQuiz.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textQuiz.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorQuiz.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        binding.iconFlashcard.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textFlashcard.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorFlashcard.setBackgroundColor(android.graphics.Color.TRANSPARENT)
    }
    
    private fun switchToQuiz() {
        binding.root.findViewById<View>(R.id.learnContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.podcastContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.quizContent).visibility = View.VISIBLE
        binding.root.findViewById<View>(R.id.quizResultContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.flashcardContent).visibility = View.GONE
        
        binding.iconQuiz.setColorFilter(ContextCompat.getColor(this, R.color.black))
        binding.textQuiz.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.indicatorQuiz.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        
        binding.iconLearn.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textLearn.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorLearn.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        binding.iconPodcast.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textPodcast.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorPodcast.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        binding.iconFlashcard.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textFlashcard.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorFlashcard.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        loadQuizQuestion()
        setupQuizButtons()
    }
    
    private fun loadVideoThumbnail() {
        val thumbnailUrl = "https://img.youtube.com/vi/$youtubeVideoId/maxresdefault.jpg"
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(thumbnailUrl)
                val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                withContext(Dispatchers.Main) {
                    val videoThumbnail = binding.root.findViewById<android.widget.ImageView>(R.id.videoThumbnail)
                    videoThumbnail?.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupPlayButton() {
        val playButton = binding.root.findViewById<android.widget.ImageView>(R.id.playButton)
        val videoThumbnail = binding.root.findViewById<android.widget.ImageView>(R.id.videoThumbnail)
        val videoWebView = binding.root.findViewById<android.webkit.WebView>(R.id.videoWebView)
        
        playButton?.setOnClickListener {
            videoThumbnail?.visibility = View.GONE
            playButton.visibility = View.GONE
            videoWebView?.visibility = View.VISIBLE
            
            videoWebView?.settings?.javaScriptEnabled = true
            videoWebView?.settings?.mediaPlaybackRequiresUserGesture = false
            videoWebView?.settings?.domStorageEnabled = true
            videoWebView?.webChromeClient = WebChromeClient()
            
            val videoHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { margin: 0; padding: 0; background: #000; }
                        .video-container { position: relative; width: 100%; height: 100vh; }
                        iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: 0; }
                    </style>
                </head>
                <body>
                    <div class="video-container">
                        <iframe src="https://www.youtube.com/embed/$youtubeVideoId?autoplay=1&controls=1&modestbranding=1&rel=0&showinfo=0" 
                                frameborder="0" 
                                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                                allowfullscreen>
                        </iframe>
                    </div>
                </body>
                </html>
            """.trimIndent()
            
            videoWebView?.loadData(videoHtml, "text/html", "utf-8")
        }
    }
    
    private fun showLessonSelectorPopup(anchorView: View) {
        val popupBinding = DialogLessonSelectorBinding.inflate(LayoutInflater.from(this))
        
        popupWindow = PopupWindow(
            popupBinding.root,
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        
        popupBinding.closeDialog.setOnClickListener {
            popupWindow?.dismiss()
        }
        
        val lessons = listOf(
            popupBinding.lesson1Item,
            popupBinding.lesson2Item,
            popupBinding.lesson3Item,
            popupBinding.lesson4Item,
            popupBinding.lesson5Item,
            popupBinding.lesson6Item,
            popupBinding.lesson7Item,
            popupBinding.lesson8Item
        )
        
        lessons.forEach { lessonView ->
            lessonView.setOnClickListener {
                popupWindow?.dismiss()
            }
        }
        
        popupWindow?.elevation = 8f
        popupWindow?.showAsDropDown(anchorView, 0, 16)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        popupWindow?.dismiss()
        val videoWebView = binding.root.findViewById<android.webkit.WebView>(R.id.videoWebView)
        videoWebView?.destroy()
    }
    
    override fun onPause() {
        super.onPause()
        val videoWebView = binding.root.findViewById<android.webkit.WebView>(R.id.videoWebView)
        videoWebView?.onPause()
    }
    
    override fun onResume() {
        super.onResume()
        val videoWebView = binding.root.findViewById<android.webkit.WebView>(R.id.videoWebView)
        videoWebView?.onResume()
    }
    
    private fun loadQuizQuestion() {
        val quizContent = binding.root.findViewById<View>(R.id.quizContent)
        val question = questions[currentQuestionIndex]
        
        quizContent.findViewById<TextView>(R.id.questionText).text = question.first
        quizContent.findViewById<TextView>(R.id.questionProgress).text = "Questions ${currentQuestionIndex + 1}/10"
        quizContent.findViewById<android.widget.ProgressBar>(R.id.progressBar).progress = ((currentQuestionIndex + 1) * 10)
        
        val optionViews = listOf(
            quizContent.findViewById<View>(R.id.optionA),
            quizContent.findViewById<View>(R.id.optionB),
            quizContent.findViewById<View>(R.id.optionC),
            quizContent.findViewById<View>(R.id.optionD)
        )
        
        val optionTextIds = listOf(R.id.optionTextA, R.id.optionTextB, R.id.optionTextC, R.id.optionTextD)
        val optionLabelIds = listOf(R.id.optionLabelA, R.id.optionLabelB, R.id.optionLabelC, R.id.optionLabelD)
        
        question.second.forEachIndexed { index, option ->
            optionViews[index].findViewById<TextView>(optionTextIds[index]).text = option
            optionViews[index].findViewById<TextView>(optionLabelIds[index]).text = "${('A' + index)}."
            
            optionViews[index].setOnClickListener {
                selectQuizOption(index)
            }
        }
        
        updateQuizOptionsUI()
        updateQuizButtons()
    }
    
    private fun selectQuizOption(index: Int) {
        userAnswers[currentQuestionIndex] = index
        updateQuizOptionsUI()
        updateQuizButtons()
    }
    
    private fun updateQuizOptionsUI() {
        val quizContent = binding.root.findViewById<View>(R.id.quizContent)
        val optionViews = listOf(
            quizContent.findViewById<View>(R.id.optionA),
            quizContent.findViewById<View>(R.id.optionB),
            quizContent.findViewById<View>(R.id.optionC),
            quizContent.findViewById<View>(R.id.optionD)
        )
        
        val optionLabelIds = listOf(R.id.optionLabelA, R.id.optionLabelB, R.id.optionLabelC, R.id.optionLabelD)
        val selectedOption = userAnswers[currentQuestionIndex] ?: -1
        
        optionViews.forEachIndexed { index, view ->
            if (index == selectedOption) {
                view.setBackgroundResource(R.drawable.bg_option_selected)
                view.findViewById<TextView>(optionLabelIds[index]).setBackgroundResource(R.drawable.circle_green)
                view.findViewById<TextView>(optionLabelIds[index]).setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                view.setBackgroundResource(R.drawable.bg_option_card)
                view.findViewById<TextView>(optionLabelIds[index]).setBackgroundResource(R.drawable.circle_gray_light)
                view.findViewById<TextView>(optionLabelIds[index]).setTextColor(ContextCompat.getColor(this, R.color.gray_600))
            }
        }
    }
    
    private fun setupQuizButtons() {
        val quizContent = binding.root.findViewById<View>(R.id.quizContent)
        val btnPrevious = quizContent.findViewById<TextView>(R.id.btnPrevious)
        val btnNext = quizContent.findViewById<TextView>(R.id.btnNext)
        
        btnPrevious.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                loadQuizQuestion()
            }
        }
        
        btnNext.setOnClickListener {
            if (userAnswers[currentQuestionIndex] != null) {
                if (currentQuestionIndex < questions.size - 1) {
                    currentQuestionIndex++
                    loadQuizQuestion()
                } else {
                    showQuizResult()
                }
            }
        }
    }
    
    private fun updateQuizButtons() {
        val quizContent = binding.root.findViewById<View>(R.id.quizContent)
        val btnPrevious = quizContent.findViewById<TextView>(R.id.btnPrevious)
        val btnNext = quizContent.findViewById<TextView>(R.id.btnNext)
        val selectedOption = userAnswers[currentQuestionIndex] != null
        
        if (currentQuestionIndex == questions.size - 1) {
            btnNext.text = "Submit"
        } else {
            btnNext.text = "Next"
        }
        
        btnPrevious.isEnabled = currentQuestionIndex > 0
        btnNext.isEnabled = selectedOption
        
        btnNext.alpha = if (selectedOption) 1.0f else 0.5f
        btnPrevious.alpha = if (currentQuestionIndex > 0) 1.0f else 0.5f
    }
    
    private fun showQuizResult() {
        binding.root.findViewById<View>(R.id.quizContent).visibility = View.GONE
        val resultView = binding.root.findViewById<View>(R.id.quizResultContent)
        resultView.visibility = View.VISIBLE
        
        val correctCount = userAnswers.count { it.value == questions[it.key].third }
        resultView.findViewById<TextView>(R.id.scoreValue).text = correctCount.toString()
        
        val completionText = resultView.findViewById<TextView>(R.id.completionText)
        val text = android.text.SpannableString("You completed the \"User Centric Design\" quiz")
        text.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 19, 40, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        completionText.text = text
        
        val rowIcons = resultView.findViewById<android.widget.LinearLayout>(R.id.rowIcons)
        rowIcons.removeAllViews()
        rowIcons.orientation = android.widget.LinearLayout.VERTICAL
        
        val row1 = android.widget.LinearLayout(this)
        row1.orientation = android.widget.LinearLayout.HORIZONTAL
        val row2 = android.widget.LinearLayout(this)
        row2.orientation = android.widget.LinearLayout.HORIZONTAL
        
        questions.forEachIndexed { index, _ ->
            val icon = android.widget.ImageView(this)
            val params = android.widget.LinearLayout.LayoutParams(dpToPx(24), dpToPx(24))
            params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
            icon.layoutParams = params
            
            val isCorrect = userAnswers[index] == questions[index].third
            icon.setImageResource(if (isCorrect) R.drawable.ic_check_circle else R.drawable.ic_close_circle)
            
            if (index < 5) row1.addView(icon) else row2.addView(icon)
        }
        
        rowIcons.addView(row1)
        rowIcons.addView(row2)
        
        resultView.findViewById<TextView>(R.id.btnRestart).setOnClickListener {
            userAnswers.clear()
            currentQuestionIndex = 0
            resultView.visibility = View.GONE
            switchToQuiz()
        }
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
    
    private fun switchToFlashcard() {
        binding.root.findViewById<View>(R.id.learnContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.podcastContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.quizContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.quizResultContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.flashcardContent).visibility = View.VISIBLE
        binding.root.findViewById<View>(R.id.qbankContent).visibility = View.GONE
        
        binding.iconFlashcard.setColorFilter(ContextCompat.getColor(this, R.color.black))
        binding.textFlashcard.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.indicatorFlashcard.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        
        binding.iconLearn.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textLearn.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorLearn.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        binding.iconPodcast.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textPodcast.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorPodcast.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        binding.iconQuiz.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textQuiz.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorQuiz.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        binding.iconQBank.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textQBank.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorQBank.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        loadFlashcard()
        setupFlashcardButtons()
    }
    
    private fun switchToQBank() {
        binding.root.findViewById<View>(R.id.learnContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.podcastContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.quizContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.quizResultContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.flashcardContent).visibility = View.GONE
        binding.root.findViewById<View>(R.id.qbankContent).visibility = View.VISIBLE
        
        binding.iconQBank.setColorFilter(ContextCompat.getColor(this, R.color.black))
        binding.textQBank.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.indicatorQBank.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        
        binding.iconLearn.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textLearn.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorLearn.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        binding.iconPodcast.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textPodcast.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorPodcast.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        binding.iconQuiz.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textQuiz.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorQuiz.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        binding.iconFlashcard.setColorFilter(ContextCompat.getColor(this, R.color.gray_600))
        binding.textFlashcard.setTextColor(ContextCompat.getColor(this, R.color.gray_600))
        binding.indicatorFlashcard.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        setupQBankExpanders()
    }
    
    private fun setupQBankExpanders() {
        val qbankContent = binding.root.findViewById<View>(R.id.qbankContent)
        
        val cards = listOf(
            Triple(qbankContent.findViewById<View>(R.id.questionCard1), qbankContent.findViewById<View>(R.id.answerSection1), qbankContent.findViewById<android.widget.ImageView>(R.id.expandIcon1)),
            Triple(qbankContent.findViewById<View>(R.id.questionCard2), qbankContent.findViewById<View>(R.id.answerSection2), qbankContent.findViewById<android.widget.ImageView>(R.id.expandIcon2)),
            Triple(qbankContent.findViewById<View>(R.id.questionCard3), qbankContent.findViewById<View>(R.id.answerSection3), qbankContent.findViewById<android.widget.ImageView>(R.id.expandIcon3)),
            Triple(qbankContent.findViewById<View>(R.id.questionCard4), qbankContent.findViewById<View>(R.id.answerSection4), qbankContent.findViewById<android.widget.ImageView>(R.id.expandIcon4))
        )
        
        cards.forEach { (card, answer, icon) ->
            card.setOnClickListener {
                if (answer.visibility == View.GONE) {
                    answer.visibility = View.VISIBLE
                    icon.rotation = 180f
                } else {
                    answer.visibility = View.GONE
                    icon.rotation = 0f
                }
            }
        }
    }
    
    private fun loadFlashcard() {
        val flashcardContent = binding.root.findViewById<View>(R.id.flashcardContent)
        val flashcard = flashcards[currentFlashcardIndex]
        val cardView = flashcardContent.findViewById<androidx.cardview.widget.CardView>(R.id.flashcard)
        val flashcardText = flashcardContent.findViewById<TextView>(R.id.flashcardText)
        val divider = flashcardContent.findViewById<View>(R.id.divider)
        
        flashcardText.text = flashcard.first
        flashcardText.setTextColor(ContextCompat.getColor(this, R.color.white))
        flashcardContent.findViewById<TextView>(R.id.flashcardCounter).text = "${currentFlashcardIndex + 1}/${flashcards.size}"
        flashcardContent.findViewById<TextView>(R.id.seeAnswer).text = "See answer"
        cardView.setCardBackgroundColor(0xFF33342E.toInt())
        divider.setBackgroundColor(0x55FFFFFF)
        isShowingAnswer = false
    }
    
    private fun setupFlashcardButtons() {
        val flashcardContent = binding.root.findViewById<View>(R.id.flashcardContent)
        val cardView = flashcardContent.findViewById<androidx.cardview.widget.CardView>(R.id.flashcard)
        val seeAnswer = flashcardContent.findViewById<TextView>(R.id.seeAnswer)
        val btnPrev = flashcardContent.findViewById<android.widget.ImageView>(R.id.btnPrevFlashcard)
        val btnNext = flashcardContent.findViewById<android.widget.ImageView>(R.id.btnNextFlashcard)
        
        cardView.setOnClickListener {
            flipCard()
        }
        
        seeAnswer.setOnClickListener {
            flipCard()
        }
        
        btnPrev.setOnClickListener {
            if (currentFlashcardIndex > 0) {
                currentFlashcardIndex--
                loadFlashcard()
            }
        }
        
        btnNext.setOnClickListener {
            if (currentFlashcardIndex < flashcards.size - 1) {
                currentFlashcardIndex++
                loadFlashcard()
            }
        }
    }
    
    private fun flipCard() {
        val flashcardContent = binding.root.findViewById<View>(R.id.flashcardContent)
        val cardView = flashcardContent.findViewById<androidx.cardview.widget.CardView>(R.id.flashcard)
        val flashcardText = flashcardContent.findViewById<TextView>(R.id.flashcardText)
        val seeAnswer = flashcardContent.findViewById<TextView>(R.id.seeAnswer)
        val divider = flashcardContent.findViewById<View>(R.id.divider)
        
        cardView.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction {
            cardView.animate().rotationY(90f).setDuration(120).withEndAction {
                if (isShowingAnswer) {
                    flashcardText.text = flashcards[currentFlashcardIndex].first
                    flashcardText.setTextColor(ContextCompat.getColor(this, R.color.white))
                    flashcardText.gravity = android.view.Gravity.TOP or android.view.Gravity.START
                    seeAnswer.text = "See answer"
                    cardView.setCardBackgroundColor(0xFF33342E.toInt())
                    divider.setBackgroundColor(0x4D4D4D4D)
                    isShowingAnswer = false
                } else {
                    flashcardText.text = flashcards[currentFlashcardIndex].second
                    flashcardText.setTextColor(ContextCompat.getColor(this, R.color.black))
                    flashcardText.gravity = android.view.Gravity.TOP or android.view.Gravity.START
                    seeAnswer.text = "See question"
                    cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
                    divider.setBackgroundColor(0xFFE0E0E0.toInt())
                    isShowingAnswer = true
                }
                cardView.rotationY = -90f
                cardView.animate().rotationY(0f).setDuration(120).withEndAction {
                    cardView.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                }.start()
            }.start()
        }.start()
    }
}
