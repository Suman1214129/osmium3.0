package com.osmiumai.app.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.osmiumai.app.MockTestActivity
import com.osmiumai.app.NotificationStorageManager
import com.osmiumai.app.R
import com.osmiumai.app.databinding.FragmentNotificationsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        
        binding.btnMarkAllRead.setOnClickListener {
            NotificationStorageManager.clearAllNotifications(requireContext())
            loadNotifications()
        }
        
        loadNotifications()
        
        return binding.root
    }
    
    private fun loadNotifications() {
        val notifications = NotificationStorageManager.getNotifications(requireContext())
        val container = binding.root.findViewById<LinearLayout>(R.id.notificationsContainer)
        val unreadCount = notifications.count { !it.isRead }
        
        binding.root.findViewById<TextView>(R.id.tvUnreadCount)?.text = "$unreadCount unread"
        
        container?.removeAllViews()
        
        if (notifications.isEmpty()) {
            binding.notif1.visibility = View.GONE
            binding.notif2.visibility = View.GONE
            binding.notif3.visibility = View.GONE
            return
        }
        
        notifications.forEach { notification ->
            val notifView = layoutInflater.inflate(R.layout.item_notification, container, false)
            
            notifView.findViewById<TextView>(R.id.tvNotifTitle).text = notification.title
            notifView.findViewById<TextView>(R.id.tvNotifMessage).text = notification.message
            notifView.findViewById<TextView>(R.id.tvNotifTime).text = getTimeAgo(notification.timestamp)
            
            notifView.setOnClickListener {
                startActivity(Intent(requireContext(), MockTestActivity::class.java))
            }
            
            container?.addView(notifView)
        }
    }
    
    private fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m"
            diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h"
            diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}d"
            else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
