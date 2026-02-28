package com.osmiumai.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.osmiumai.app.R
import com.osmiumai.app.databinding.FragmentProfileBinding
import com.osmiumai.app.databinding.DialogEditProfileBinding
import kotlin.math.abs

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var isShowingWeakTopics = true
    private var currentWeakPage = 0
    private var currentStrongPage = 0
    private var currentSubject = "Physics"
    private var currentSuggestionExam = "JEE MAINS"
    private var currentSuggestionPage = 0
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var weakCard: View
    private lateinit var strongCard: View
    private lateinit var lineChartView: com.osmiumai.app.views.LineChartView
    
    private val suggestionsByExam = mapOf(
        "JEE MAINS" to listOf(
            Triple("Arrays", "Practice two-pointer & sliding window patterns", "Solve 5-7 medium problems to strengthen pattern recognition"),
            Triple("Strings", "Focus on substring and palindrome problems", "Master KMP algorithm and string manipulation techniques"),
            Triple("Dynamic Programming", "Study memoization and tabulation approaches", "Practice classic DP problems like knapsack and LCS"),
            Triple("Graphs", "Learn BFS and DFS traversal techniques", "Implement shortest path algorithms like Dijkstra"),
            Triple("Trees", "Understand binary tree traversals", "Practice problems on BST and tree construction"),
            Triple("Linked Lists", "Master pointer manipulation", "Solve problems on reversing and detecting cycles")
        ),
        "GATE" to listOf(
            Triple("Operating Systems", "Review process scheduling algorithms", "Focus on deadlock prevention and memory management"),
            Triple("Database Management", "Study normalization and SQL queries", "Practice transaction management and indexing concepts"),
            Triple("Computer Networks", "Understand OSI and TCP/IP models", "Master routing protocols and network security"),
            Triple("Data Structures", "Review time complexity analysis", "Practice implementation of advanced data structures"),
            Triple("Algorithms", "Study greedy and divide-conquer approaches", "Solve problems on sorting and searching algorithms"),
            Triple("Digital Logic", "Review Boolean algebra and K-maps", "Practice sequential and combinational circuit design")
        ),
        "NEET-UG" to listOf(
            Triple("Cell Biology", "Focus on cell organelles and their functions", "Review cell division processes - mitosis and meiosis"),
            Triple("Genetics", "Study Mendelian inheritance patterns", "Practice pedigree analysis and genetic disorders"),
            Triple("Human Physiology", "Understand circulatory and respiratory systems", "Review hormonal regulation and nervous system"),
            Triple("Plant Physiology", "Study photosynthesis and respiration", "Focus on plant growth regulators and movements"),
            Triple("Ecology", "Review ecosystem dynamics and energy flow", "Understand biodiversity and conservation strategies"),
            Triple("Evolution", "Study theories of evolution and evidences", "Practice Hardy-Weinberg principle applications")
        )
    )
    
    private val topicsBySubject = mapOf(
        "Physics" to mapOf(
            "weak" to listOf(
                "Electrostatic Potential & Capacitance" to "25.5%",
                "Gauss's Law and Applications" to "26.4%",
                "Photoelectric Effect" to "31.4%",
                "Moving Charges and Magnetism" to "36.6%",
                "Wave Optics – Young's Double Slit Experiment" to "45.9%",
                "Electromagnetic Induction" to "48.2%",
                "AC Circuits - Resonance" to "52.3%",
                "Atomic Structure - Bohr Model" to "54.1%",
                "Nuclear Physics - Radioactivity" to "56.8%"
            ),
            "strong" to listOf(
                "Current Electricity - Kirchhoff's Laws" to "91.1%",
                "Semiconductor Devices - PN Junction" to "86.4%",
                "Stoke's Law" to "81.2%",
                "Logic Gates & Digital Electronics" to "76.6%",
                "Ray Optics - Refraction through Lenses" to "65.6%",
                "Thermodynamics - First Law" to "88.5%",
                "Rotational Motion - Moment of Inertia" to "84.7%",
                "Simple Harmonic Motion" to "79.3%",
                "Gravitation - Kepler's Laws" to "73.2%"
            )
        ),
        "Chemistry" to mapOf(
            "weak" to listOf(
                "Chemical Bonding - Hybridization" to "28.3%",
                "Coordination Compounds" to "32.1%",
                "Electrochemistry - Nernst Equation" to "35.7%",
                "Chemical Kinetics - Rate Laws" to "38.9%",
                "Thermodynamics - Entropy" to "42.5%",
                "Organic Chemistry - Reactions" to "46.8%",
                "Equilibrium - Le Chatelier's Principle" to "49.2%",
                "Redox Reactions" to "51.6%",
                "Atomic Structure - Quantum Numbers" to "54.3%"
            ),
            "strong" to listOf(
                "Periodic Table - Trends" to "89.7%",
                "Stoichiometry" to "87.3%",
                "Acids and Bases" to "83.5%",
                "States of Matter" to "78.9%",
                "Mole Concept" to "76.2%",
                "Chemical Reactions - Balancing" to "85.4%",
                "Solutions - Concentration" to "81.8%",
                "Gases - Ideal Gas Law" to "77.6%",
                "Nomenclature - IUPAC" to "74.1%"
            )
        ),
        "Mathematics" to mapOf(
            "weak" to listOf(
                "Differential Equations" to "27.8%",
                "Vector Calculus" to "30.5%",
                "Complex Numbers - De Moivre's Theorem" to "33.9%",
                "Probability - Conditional" to "37.2%",
                "Matrices - Determinants" to "41.6%",
                "Integration - Definite Integrals" to "44.8%",
                "Trigonometry - Inverse Functions" to "48.3%",
                "Sequences and Series" to "52.1%",
                "Limits and Continuity" to "55.7%"
            ),
            "strong" to listOf(
                "Algebra - Quadratic Equations" to "92.4%",
                "Coordinate Geometry - Straight Lines" to "88.9%",
                "Differentiation - Basic Rules" to "85.6%",
                "Sets and Relations" to "82.3%",
                "Functions - Domain and Range" to "79.1%",
                "Binomial Theorem" to "86.7%",
                "Permutations and Combinations" to "83.2%",
                "Statistics - Mean, Median, Mode" to "80.5%",
                "Logarithms" to "77.8%"
            )
        )
    )
    
    private val weakTopics: List<Pair<String, String>>
        get() = topicsBySubject[currentSubject]?.get("weak") ?: emptyList()
    
    private val strongTopics: List<Pair<String, String>>
        get() = topicsBySubject[currentSubject]?.get("strong") ?: emptyList()
    
    private val examData = mapOf(
        "JEE MAINS" to listOf(150f, 140f, 180f, 255f),
        "GATE" to listOf(120f, 160f, 145f, 200f),
        "NEET-UG" to listOf(180f, 170f, 210f, 240f)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfileAvatar()
        setupMonthDropdown()
        setupChart()
        setupExamTabs()
        setupStrengthTabs()
        setupSuggestionTabs()
        setupTopicsCards()
        setupEditProfile()
        setupSettings()
        setupNotifications()
    }

    private fun loadProfileAvatar() {
        binding.root.findViewById<WebView>(R.id.wvProfileAvatar)?.apply {
            settings.javaScriptEnabled = false
            loadUrl("https://api.dicebear.com/9.x/glass/svg?seed=Jameson")
        }
    }

    private fun setupMonthDropdown() {
        binding.monthDropdown.setOnClickListener { view ->
            PopupMenu(requireContext(), view).apply {
                menuInflater.inflate(R.menu.menu_month_filter, menu)
                setOnMenuItemClickListener { item ->
                    binding.monthText.text = item.title
                    true
                }
                show()
            }
        }
    }
    
    private fun setupChart() {
        lineChartView = binding.root.findViewById(R.id.lineChartView)
        lineChartView.setData(examData["JEE MAINS"]!!, animate = false)
    }

    private fun setupExamTabs() {
        binding.tabJeeMains.setOnClickListener { 
            selectTab(binding.tabJeeMains)
            updateChart("JEE MAINS")
        }
        binding.tabGate.setOnClickListener { 
            selectTab(binding.tabGate)
            updateChart("GATE")
        }
        binding.tabNeetUg.setOnClickListener { 
            selectTab(binding.tabNeetUg)
            updateChart("NEET-UG")
        }
    }
    
    private fun updateChart(exam: String) {
        examData[exam]?.let { data ->
            lineChartView.setData(data, animate = true)
        }
    }

    private fun setupStrengthTabs() {
        binding.tabStrengthJeeMains.setOnClickListener { selectStrengthTab(binding.tabStrengthJeeMains) }
        binding.tabStrengthGate.setOnClickListener { selectStrengthTab(binding.tabStrengthGate) }
        binding.tabStrengthNeetUg.setOnClickListener { selectStrengthTab(binding.tabStrengthNeetUg) }
    }

    private fun setupSuggestionTabs() {
        binding.tabSuggestionJeeMains.setOnClickListener { 
            selectSuggestionTab(binding.tabSuggestionJeeMains)
            currentSuggestionExam = "JEE MAINS"
            currentSuggestionPage = 0
            updateSuggestionCard()
        }
        binding.tabSuggestionGate.setOnClickListener { 
            selectSuggestionTab(binding.tabSuggestionGate)
            currentSuggestionExam = "GATE"
            currentSuggestionPage = 0
            updateSuggestionCard()
        }
        binding.tabSuggestionNeetUg.setOnClickListener { 
            selectSuggestionTab(binding.tabSuggestionNeetUg)
            currentSuggestionExam = "NEET-UG"
            currentSuggestionPage = 0
            updateSuggestionCard()
        }
        
        binding.btnSuggestionPrev.setOnClickListener {
            if (currentSuggestionPage > 0) {
                currentSuggestionPage--
                updateSuggestionCard()
            }
        }
        
        binding.btnSuggestionNext.setOnClickListener {
            val totalPages = (suggestionsByExam[currentSuggestionExam]?.size ?: 0 + 1) / 2
            if (currentSuggestionPage < totalPages - 1) {
                currentSuggestionPage++
                updateSuggestionCard()
            }
        }
        
        updateSuggestionCard()
    }
    
    private fun updateSuggestionCard() {
        val suggestions = suggestionsByExam[currentSuggestionExam] ?: return
        val pagedSuggestions = suggestions.chunked(2).getOrNull(currentSuggestionPage) ?: return
        val totalPages = (suggestions.size + 1) / 2
        
        binding.tvSuggestionPagination.text = "${currentSuggestionPage + 1} of $totalPages"
        binding.btnSuggestionPrev.isEnabled = currentSuggestionPage > 0
        binding.btnSuggestionNext.isEnabled = currentSuggestionPage < totalPages - 1
        
        val item1 = binding.item1
        val item2 = binding.item2
        
        if (pagedSuggestions.isNotEmpty()) {
            val (topic1, issue1, suggestion1) = pagedSuggestions[0]
            item1.getChildAt(0).let { it as TextView }.text = topic1
            (item1.getChildAt(1) as LinearLayout).getChildAt(1).let { it as TextView }.text = issue1
            item1.getChildAt(3).let { it as TextView }.text = suggestion1
        }
        
        if (pagedSuggestions.size > 1) {
            val (topic2, issue2, suggestion2) = pagedSuggestions[1]
            item2.visibility = View.VISIBLE
            binding.dividerSuggestion.visibility = View.VISIBLE
            item2.getChildAt(0).let { it as TextView }.text = topic2
            (item2.getChildAt(1) as LinearLayout).getChildAt(1).let { it as TextView }.text = issue2
            item2.getChildAt(3).let { it as TextView }.text = suggestion2
        } else {
            item2.visibility = View.GONE
            binding.dividerSuggestion.visibility = View.GONE
        }
    }

    private fun setupTopicsCards() {
        weakCard = binding.root.findViewById(R.id.weakTopicsCard)
        strongCard = binding.root.findViewById(R.id.strongTopicsCard)
        
        setupCardNavigation(weakCard, true)
        setupCardNavigation(strongCard, false)
        
        populateCard(weakCard, true, currentWeakPage)
        populateCard(strongCard, false, currentStrongPage)
        
        gestureDetector = GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(e2.y - e1.y) && abs(diffX) > 100) {
                    if (diffX > 0 && !isShowingWeakTopics) {
                        showWeakTopics()
                    } else if (diffX < 0 && isShowingWeakTopics) {
                        showStrongTopics()
                    }
                    return true
                }
                return false
            }
        })
        
        binding.root.findViewById<View>(R.id.topicsCardContainer).setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
        
        updateDots()
    }
    
    private fun setupCardNavigation(card: View, isWeak: Boolean) {
        val btnNext = card.findViewById<ImageView>(R.id.btnNext)
        val btnPrev = card.findViewById<ImageView>(R.id.btnPrev)
        val dropdown = card.findViewById<LinearLayout>(R.id.dropdownContainer)
        val tvSubject = card.findViewById<TextView>(R.id.tvSubject)
        
        dropdown.setOnClickListener {
            PopupMenu(requireContext(), it).apply {
                menu.add("Physics")
                menu.add("Chemistry")
                menu.add("Mathematics")
                setOnMenuItemClickListener { item ->
                    currentSubject = item.title.toString()
                    tvSubject.text = currentSubject
                    currentWeakPage = 0
                    currentStrongPage = 0
                    populateCard(weakCard, true, currentWeakPage)
                    populateCard(strongCard, false, currentStrongPage)
                    true
                }
                show()
            }
        }
        
        btnNext.setOnClickListener {
            if (isWeak) {
                currentWeakPage++
                populateCard(weakCard, true, currentWeakPage)
            } else {
                currentStrongPage++
                populateCard(strongCard, false, currentStrongPage)
            }
        }
        
        btnPrev.setOnClickListener {
            if (isWeak) {
                currentWeakPage--
                populateCard(weakCard, true, currentWeakPage)
            } else {
                currentStrongPage--
                populateCard(strongCard, false, currentStrongPage)
            }
        }
    }
    
    private fun populateCard(card: View, isWeak: Boolean, page: Int) {
        val headerTopic = card.findViewById<TextView>(R.id.headerTopic)
        val listContainer = card.findViewById<LinearLayout>(R.id.listContainer)
        val tvPagination = card.findViewById<TextView>(R.id.tvPagination)
        val btnPrev = card.findViewById<ImageView>(R.id.btnPrev)
        val btnNext = card.findViewById<ImageView>(R.id.btnNext)
        
        headerTopic.text = if (isWeak) "Weak Topics" else "Strong Topics"
        
        val allTopics = if (isWeak) weakTopics else strongTopics
        val topics = allTopics.chunked(3).getOrNull(page) ?: emptyList()
        val totalPages = (allTopics.size + 2) / 3
        
        tvPagination.text = "${page + 1} of $totalPages"
        
        btnPrev.isEnabled = page > 0
        btnNext.isEnabled = page < totalPages - 1
        
        listContainer.removeAllViews()
        topics.forEach { (topic, accuracy) ->
            val row = layoutInflater.inflate(R.layout.item_topic_row, listContainer, false)
            row.findViewById<TextView>(R.id.tvTopic).text = topic
            row.findViewById<TextView>(R.id.tvAccuracy).apply {
                text = accuracy
                setTextColor(if (isWeak) 0xFF9E511E.toInt() else 0xFF4CAF50.toInt())
            }
            listContainer.addView(row)
        }
    }
    
    private fun showWeakTopics() {
        strongCard.animate()
            .translationX(strongCard.width.toFloat())
            .alpha(0f)
            .setDuration(300)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .withEndAction {
                strongCard.visibility = View.GONE
                strongCard.translationX = 0f
                weakCard.visibility = View.VISIBLE
                weakCard.translationX = -weakCard.width.toFloat()
                weakCard.alpha = 0f
                weakCard.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(android.view.animation.DecelerateInterpolator())
                    .start()
            }.start()
        isShowingWeakTopics = true
        updateDots()
    }
    
    private fun showStrongTopics() {
        weakCard.animate()
            .translationX(-weakCard.width.toFloat())
            .alpha(0f)
            .setDuration(300)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .withEndAction {
                weakCard.visibility = View.GONE
                weakCard.translationX = 0f
                strongCard.visibility = View.VISIBLE
                strongCard.translationX = strongCard.width.toFloat()
                strongCard.alpha = 0f
                strongCard.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(android.view.animation.DecelerateInterpolator())
                    .start()
            }.start()
        isShowingWeakTopics = false
        updateDots()
    }
    
    private fun updateDots() {
        if (isShowingWeakTopics) {
            binding.dot1.setBackgroundResource(R.drawable.dot_active)
            binding.dot2.setBackgroundResource(R.drawable.dot_inactive)
        } else {
            binding.dot1.setBackgroundResource(R.drawable.dot_inactive)
            binding.dot2.setBackgroundResource(R.drawable.dot_active)
        }
    }

    private fun setupEditProfile() {
        binding.root.findViewById<TextView>(R.id.btnEditProfile)?.setOnClickListener {
            showEditProfileDialog()
        }
    }

    private fun setupSettings() {
        binding.root.findViewById<ImageView>(R.id.btnSettings)?.setOnClickListener {
            startActivity(Intent(requireContext(), com.osmiumai.app.ui.settings.SettingsActivity::class.java))
        }
    }

    private fun setupNotifications() {
        binding.root.findViewById<ImageView>(R.id.btnNotifications)?.setOnClickListener {
            startActivity(Intent(requireContext(), com.osmiumai.app.ui.notifications.NotificationsActivity::class.java))
        }
    }

    private fun showEditProfileDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        
        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.etFullName.text.toString()
            val email = dialogBinding.etEmail.text.toString()
            binding.root.findViewById<TextView>(R.id.tvUserName)?.text = name
            binding.root.findViewById<TextView>(R.id.tvUserEmail)?.text = email
            dialog.dismiss()
        }

        dialogBinding.dropdownExam.setOnClickListener { view ->
            PopupMenu(requireContext(), view).apply {
                menu.add("JEE MAINS")
                menu.add("GATE")
                menu.add("NEET-UG")
                setOnMenuItemClickListener { item ->
                    dialogBinding.tvSelectedExam.text = item.title
                    true
                }
                show()
            }
        }

        dialog.show()
    }



    private fun selectTab(selectedTab: View) {
        listOf(binding.tabJeeMains, binding.tabGate, binding.tabNeetUg).forEach { tab ->
            if (tab == selectedTab) {
                tab.setBackgroundResource(R.drawable.bg_pill_white_border)
                (tab as android.widget.TextView).setTextColor(0xFF1E1E1E.toInt())
            } else {
                tab.setBackgroundResource(R.drawable.bg_pill_gray_light)
                (tab as android.widget.TextView).setTextColor(0xFF737373.toInt())
            }
        }
    }

    private fun selectStrengthTab(selectedTab: View) {
        listOf(binding.tabStrengthJeeMains, binding.tabStrengthGate, binding.tabStrengthNeetUg).forEach { tab ->
            if (tab == selectedTab) {
                tab.setBackgroundResource(R.drawable.bg_pill_white_border)
                (tab as android.widget.TextView).setTextColor(0xFF1E1E1E.toInt())
            } else {
                tab.setBackgroundResource(R.drawable.bg_pill_gray_light)
                (tab as android.widget.TextView).setTextColor(0xFF737373.toInt())
            }
        }
    }

    private fun selectSuggestionTab(selectedTab: View) {
        listOf(binding.tabSuggestionJeeMains, binding.tabSuggestionGate, binding.tabSuggestionNeetUg).forEach { tab ->
            if (tab == selectedTab) {
                tab.setBackgroundResource(R.drawable.bg_pill_white_border)
                (tab as android.widget.TextView).setTextColor(0xFF1E1E1E.toInt())
            } else {
                tab.setBackgroundResource(R.drawable.bg_pill_gray_light)
                (tab as android.widget.TextView).setTextColor(0xFF737373.toInt())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
