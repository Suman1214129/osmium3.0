package com.osmiumai.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var weakCard: View
    private lateinit var strongCard: View

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
        setupMonthDropdown()
        setupExamTabs()
        setupStrengthTabs()
        setupSuggestionTabs()
        setupTopicsCards()
        setupEditProfile()
        setupSettings()
        setupNotifications()
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

    private fun setupExamTabs() {
        binding.tabJeeMains.setOnClickListener { selectTab(binding.tabJeeMains) }
        binding.tabGate.setOnClickListener { selectTab(binding.tabGate) }
        binding.tabNeetUg.setOnClickListener { selectTab(binding.tabNeetUg) }
    }

    private fun setupStrengthTabs() {
        binding.tabStrengthJeeMains.setOnClickListener { selectStrengthTab(binding.tabStrengthJeeMains) }
        binding.tabStrengthGate.setOnClickListener { selectStrengthTab(binding.tabStrengthGate) }
        binding.tabStrengthNeetUg.setOnClickListener { selectStrengthTab(binding.tabStrengthNeetUg) }
    }

    private fun setupSuggestionTabs() {
        binding.tabSuggestionJeeMains.setOnClickListener { selectSuggestionTab(binding.tabSuggestionJeeMains) }
        binding.tabSuggestionGate.setOnClickListener { selectSuggestionTab(binding.tabSuggestionGate) }
        binding.tabSuggestionNeetUg.setOnClickListener { selectSuggestionTab(binding.tabSuggestionNeetUg) }
    }

    private fun setupTopicsCards() {
        weakCard = binding.root.findViewById(R.id.weakTopicsCard)
        strongCard = binding.root.findViewById(R.id.strongTopicsCard)
        
        populateCard(weakCard, true)
        populateCard(strongCard, false)
        
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
    
    private fun populateCard(card: View, isWeak: Boolean) {
        val headerTopic = card.findViewById<TextView>(R.id.headerTopic)
        val listContainer = card.findViewById<LinearLayout>(R.id.listContainer)
        val tvPagination = card.findViewById<TextView>(R.id.tvPagination)
        
        headerTopic.text = if (isWeak) "Weak Topics" else "Strong Topics"
        
        val topics = if (isWeak) {
            listOf(
                "Electrostatic Potential & Capacitance" to "25.5%",
                "Gauss's Law and Applications" to "26.4%",
                "Photoelectric Effect" to "31.4%",
                "Moving Charges and Magnetism" to "36.6%",
                "Wave Optics – Young's Double Slit Experiment" to "45.9%"
            )
        } else {
            listOf(
                "Current Electricity - Kirchhoff's Laws" to "91.1%",
                "Semiconductor Devices - PN Junction" to "86.4%",
                "Stoke's Law" to "81.2%",
                "Logic Gates & Digital Electronics" to "76.6%",
                "Ray Optics - Refraction through Lenses" to "65.6%"
            )
        }
        
        tvPagination.text = "${topics.size} Topics"
        
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
                (tab as android.widget.TextView).apply {
                    setTextColor(0xFF1E1E1E.toInt())
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
            } else {
                tab.setBackgroundResource(R.drawable.bg_pill_gray_light)
                (tab as android.widget.TextView).apply {
                    setTextColor(0xFF737373.toInt())
                    setTypeface(null, android.graphics.Typeface.NORMAL)
                }
            }
        }
    }

    private fun selectStrengthTab(selectedTab: View) {
        listOf(binding.tabStrengthJeeMains, binding.tabStrengthGate, binding.tabStrengthNeetUg).forEach { tab ->
            if (tab == selectedTab) {
                tab.setBackgroundResource(R.drawable.bg_pill_white_border)
                (tab as android.widget.TextView).apply {
                    setTextColor(0xFF1E1E1E.toInt())
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
            } else {
                tab.setBackgroundResource(R.drawable.bg_pill_gray_light)
                (tab as android.widget.TextView).apply {
                    setTextColor(0xFF737373.toInt())
                    setTypeface(null, android.graphics.Typeface.NORMAL)
                }
            }
        }
    }

    private fun selectSuggestionTab(selectedTab: View) {
        listOf(binding.tabSuggestionJeeMains, binding.tabSuggestionGate, binding.tabSuggestionNeetUg).forEach { tab ->
            if (tab == selectedTab) {
                tab.setBackgroundResource(R.drawable.bg_pill_white_border)
                (tab as android.widget.TextView).apply {
                    setTextColor(0xFF1E1E1E.toInt())
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
            } else {
                tab.setBackgroundResource(R.drawable.bg_pill_gray_light)
                (tab as android.widget.TextView).apply {
                    setTextColor(0xFF737373.toInt())
                    setTypeface(null, android.graphics.Typeface.NORMAL)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
