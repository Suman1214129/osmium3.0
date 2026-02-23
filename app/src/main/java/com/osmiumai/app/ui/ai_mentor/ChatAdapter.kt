package com.osmiumai.app.ui.ai_mentor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osmiumai.app.databinding.ItemMessageAiBinding
import com.osmiumai.app.databinding.ItemMessageUserBinding

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val messages = mutableListOf<ChatMessage>()
    
    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
    }
    
    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
    
    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_AI
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding = ItemMessageUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UserMessageViewHolder(binding)
            }
            else -> {
                val binding = ItemMessageAiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AiMessageViewHolder(binding)
            }
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is AiMessageViewHolder -> holder.bind(message)
        }
    }
    
    override fun getItemCount() = messages.size
    
    class UserMessageViewHolder(private val binding: ItemMessageUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.messageText.text = message.text
        }
    }
    
    class AiMessageViewHolder(private val binding: ItemMessageAiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.messageText.text = message.text
        }
    }
}
