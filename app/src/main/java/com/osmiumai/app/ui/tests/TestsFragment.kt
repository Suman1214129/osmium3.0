package com.osmiumai.app.ui.tests

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.osmiumai.app.R
import com.osmiumai.app.TestAnalyticsActivity
import com.osmiumai.app.UploadPapersActivity
import com.osmiumai.app.databinding.FragmentTestsBinding
import com.google.android.material.chip.Chip

class TestsFragment : Fragment() {

    private var _binding: FragmentTestsBinding? = null
    private val binding get() = _binding!!
    private var selectedChip: Chip? = null
    private val dots = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestsBinding.inflate(inflater, container, false)
        setupChipClickListeners()
        setupPaginationDots()
        setupTryNowButton()
        setupNotificationButton()
        setupTestCardClickListeners()
        return binding.root
    }

    private fun setupChipClickListeners() {
        val chips = listOf(
            binding.chipAll,
            binding.chipJeeMains,
            binding.chipGate,
            binding.chipSscCgl
        )
        
        selectedChip = binding.chipAll
        
        chips.forEach { chip ->
            chip.setOnClickListener {
                selectChip(chip, chips)
            }
        }
    }
    
    private fun selectChip(clickedChip: Chip, allChips: List<Chip>) {
        selectedChip = clickedChip
        
        allChips.forEach { chip ->
            if (chip == clickedChip) {
                // Selected state
                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.white))
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.chip_selected_text))
            } else {
                // Unselected state
                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.chip_unselected_bg))
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.chip_unselected_text))
            }
        }
    }

    private fun setupPaginationDots() {
        dots.clear()
        dots.add(binding.dot1)
        dots.add(binding.dot2)
        dots.add(binding.dot3)
        
        binding.testCardsScrollView.setOnScrollChangeListener { _: View, scrollX: Int, _: Int, _: Int, _: Int ->
            updatePaginationDots(scrollX)
        }
    }
    
    private fun updatePaginationDots(scrollX: Int) {
        val screenWidth = resources.displayMetrics.widthPixels
        val currentPage = when {
            scrollX < screenWidth / 2 -> 0
            scrollX < screenWidth + screenWidth / 2 -> 1
            else -> 2
        }
        
        dots.forEachIndexed { index, dot ->
            if (index == currentPage) {
                dot.setBackgroundResource(R.drawable.pagination_dot_active)
            } else {
                dot.setBackgroundResource(R.drawable.pagination_dot_inactive)
            }
        }
    }
    
    private fun setupTryNowButton() {
        val tryNowButton = binding.root.findViewById<View>(R.id.tryNowButton)
        tryNowButton?.setOnClickListener {
            val intent = Intent(requireContext(), UploadPapersActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupNotificationButton() {
        binding.btnNotificationTests.setOnClickListener {
            val intent = Intent(requireContext(), com.osmiumai.app.ui.notifications.NotificationsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupTestCardClickListeners() {
        val testCards = listOf(
            binding.testCard1,
            binding.testCard2,
            binding.testCard3,
            binding.testCard4,
            binding.testCard5,
            binding.testCard6
        )
        
        testCards.forEach { card ->
            card.setOnClickListener {
                val intent = Intent(requireContext(), TestAnalyticsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
