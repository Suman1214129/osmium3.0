package com.osmiumai.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.osmiumai.app.R
import com.osmiumai.app.StartCourseActivity
import com.osmiumai.app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupScrollListener()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnStartNewCourse.setOnClickListener {
            val intent = Intent(requireContext(), StartCourseActivity::class.java)
            startActivity(intent)
        }
        
        binding.btnNotificationHome.setOnClickListener {
            val intent = Intent(requireContext(), com.osmiumai.app.ui.notifications.NotificationsActivity::class.java)
            startActivity(intent)
        }
        
        binding.analyticsCard.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile)
        }
        
        binding.courseCardNuclearPhysics.setOnClickListener {
            val intent = Intent(requireContext(), com.osmiumai.app.CourseOverviewActivity::class.java)
            startActivity(intent)
        }
        
        binding.courseCardDSA.setOnClickListener {
            val intent = Intent(requireContext(), com.osmiumai.app.CourseOverviewActivity::class.java)
            startActivity(intent)
        }
        
        binding.courseCardJava.setOnClickListener {
            val intent = Intent(requireContext(), com.osmiumai.app.CourseOverviewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupScrollListener() {
        binding.testsScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            val screenWidth = resources.displayMetrics.widthPixels
            val currentPage = when {
                scrollX < screenWidth / 2 -> 0
                scrollX < screenWidth + screenWidth / 2 -> 1
                else -> 2
            }
            updateDots(currentPage)
        }
        setupTabClickListeners()
    }

    private fun setupTabClickListeners() {
        binding.tabPersonalized.setOnClickListener {
            selectTab(0)
        }
        binding.tabTestYourself.setOnClickListener {
            selectTab(1)
        }
        binding.tabRecommended.setOnClickListener {
            selectTab(2)
        }
    }

    private fun selectTab(tabIndex: Int) {
        val tabs = listOf(binding.tabPersonalized, binding.tabTestYourself, binding.tabRecommended)
        val selectedBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_selected)
        val unselectedBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_unselected)
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.black)
        val unselectedColor = ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
        
        tabs.forEachIndexed { index, tab ->
            if (index == tabIndex) {
                tab.background = selectedBg
                tab.setTextColor(selectedColor)
                tab.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                tab.background = unselectedBg
                tab.setTextColor(unselectedColor)
                tab.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }
    }

    private fun updateDots(currentPage: Int) {
        val dots = listOf(binding.dot1, binding.dot2, binding.dot3)
        val activeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.dot_active)
        val inactiveDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.dot_inactive)
        
        dots.forEachIndexed { index, dot ->
            dot.background = if (index == currentPage) activeDrawable else inactiveDrawable
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
