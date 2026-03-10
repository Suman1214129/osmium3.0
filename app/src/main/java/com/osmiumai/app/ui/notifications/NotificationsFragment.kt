package com.osmiumai.app.ui.notifications

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.osmiumai.app.MockTestActivity
import com.osmiumai.app.R
import com.osmiumai.app.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    
    data class Notification(
        val id: String,
        val title: String,
        val message: String,
        val emoji: String,
        val time: String,
        val type: String,
        var isRead: Boolean = false
    )
    
    private val notifications = mutableListOf(
        Notification("1", "Advanced Data Structures", "A new course is now available", "📚", "2h", "course"),
        Notification("2", "JEE MAINS Mock Test", "You scored 85% — great performance!", "✅", "5h", "test"),
        Notification("3", "Premium Plan Expiring", "Your plan expires in 30 days", "💎", "1d", "premium"),
        Notification("4", "Live Class Starting", "Physics class starts in 15 minutes", "📡", "15m", "live"),
        Notification("5", "Chemistry Test", "You scored 78% — well done!", "✅", "Mar 8", "test", true),
        Notification("6", "Verbal Reasoning", "42 new lessons available", "📚", "Mar 6", "course", true)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        
        binding.btnBack.setOnClickListener { 
            requireActivity().onBackPressedDispatcher.onBackPressed() 
        }
        
        binding.btnMarkAllRead.setOnClickListener { 
            notifications.clear()
            loadNotifications()
        }
        
        setupChips()
        loadNotifications()
        
        return binding.root
    }
    
    private var selectedChip = "all"
    
    private fun setupChips() {
        binding.chipAll.setOnClickListener { selectChip("all") }
        binding.chipUnread.setOnClickListener { selectChip("unread") }
        binding.chipCourse.setOnClickListener { selectChip("course") }
        binding.chipTest.setOnClickListener { selectChip("test") }
    }
    
    private fun selectChip(filter: String) {
        selectedChip = filter
        val chips = listOf(
            binding.chipAll to "all",
            binding.chipUnread to "unread",
            binding.chipCourse to "course",
            binding.chipTest to "test"
        )
        chips.forEach { (chip, type) ->
            if (type == filter) {
                chip.background = createRoundedBackground("#FFFFFF")
                chip.setTextColor(Color.parseColor("#1E1E1E"))
            } else {
                chip.background = createRoundedBackground("#F0F0F0")
                chip.setTextColor(Color.parseColor("#6B6B6B"))
            }
        }
        filterNotifications(filter)
    }
    
    private fun createRoundedBackground(color: String) = GradientDrawable().apply {
        setColor(Color.parseColor(color))
        cornerRadius = 50f
    }
    
    private fun filterNotifications(filter: String) {
        val filtered = when (filter) {
            "unread" -> notifications.filter { !it.isRead }
            "course" -> notifications.filter { it.type == "course" }
            "test" -> notifications.filter { it.type == "test" }
            else -> notifications
        }
        displayNotifications(filtered)
        updateCounts()
    }
    
    private fun loadNotifications() {
        displayNotifications(notifications)
        updateCounts()
    }
    
    private fun displayNotifications(list: List<Notification>) {
        binding.notificationsContainer.removeAllViews()
        
        if (list.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.labelToday.visibility = View.GONE
            return
        }
        
        binding.emptyState.visibility = View.GONE
        binding.labelToday.visibility = View.VISIBLE
        
        list.forEach { notif ->
            val view = layoutInflater.inflate(R.layout.item_notification, binding.notificationsContainer, false)
            
            view.findViewById<TextView>(R.id.tvNotifTitle).text = notif.title
            view.findViewById<TextView>(R.id.tvNotifMessage).text = notif.message
            view.findViewById<TextView>(R.id.tvNotifTime).text = notif.time
            view.findViewById<TextView>(R.id.tvNotifTag).text = getTag(notif.type)
            view.findViewById<TextView>(R.id.notifEmoji).text = notif.emoji
            
            val border = view.findViewById<View>(R.id.notifBorder)
            val emoji = view.findViewById<TextView>(R.id.notifEmoji)
            val tag = view.findViewById<TextView>(R.id.tvNotifTag)
            val badge = view.findViewById<View>(R.id.unreadBadge)
            
            val colors = getColors(notif.type)
            border.setBackgroundColor(Color.parseColor(colors.first))
            emoji.setBackgroundColor(Color.parseColor(colors.second))
            tag.setTextColor(Color.parseColor(colors.first))
            tag.background = createTagBackground(colors.second)
            
            badge.visibility = if (notif.isRead) View.GONE else View.VISIBLE
            
            view.setOnClickListener {
                if (!notif.isRead) {
                    notif.isRead = true
                    badge.visibility = View.GONE
                    updateCounts()
                }
                startActivity(Intent(requireContext(), MockTestActivity::class.java))
            }
            
            binding.notificationsContainer.addView(view)
        }
    }
    
    private fun updateCounts() {
        val unread = notifications.count { !it.isRead }
        binding.unreadDot.visibility = if (unread > 0) View.VISIBLE else View.GONE
        binding.tvUnreadCount.text = if (unread > 0) "$unread unread" else "No unread"
        binding.chipAll.text = "All (${notifications.size})"
        binding.chipUnread.text = "Unread ($unread)"
        binding.chipCourse.text = "Course (${notifications.count { it.type == "course" }})"
        binding.chipTest.text = "Test (${notifications.count { it.type == "test" }})"
    }
    
    private fun getTag(type: String) = when (type) {
        "course" -> "New Course"
        "test" -> "Test Result"
        "premium" -> "Premium"
        "live" -> "Live Class"
        else -> "Notification"
    }
    
    private fun getColors(type: String) = when (type) {
        "course" -> Pair("#D4AF37", "#FDF6EC")
        "test" -> Pair("#4CAF50", "#EDF7EE")
        "premium" -> Pair("#FF9800", "#FFF8EE")
        "live" -> Pair("#2196F3", "#E8F4FD")
        else -> Pair("#9E9E9E", "#F5F5F5")
    }
    
    private fun createTagBackground(bgColor: String) = GradientDrawable().apply {
        setColor(Color.parseColor(bgColor))
        cornerRadius = 20f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
