package com.osmiumai.app.ui.ai_mentor

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.osmiumai.app.R

class ChatHistoryAdapter(
    private val onSessionClick: (ChatSession) -> Unit,
    private val onDeleteClick: (ChatSession) -> Unit
) : RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder>() {

    private val items = mutableListOf<ChatSession>()

    fun submitList(list: List<ChatSession>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvChatTitle)
        private val btnDelete: ImageView = itemView.findViewById(R.id.btnDeleteChat)

        fun bind(session: ChatSession) {
            tvTitle.text = session.title
            itemView.setOnClickListener { onSessionClick(session) }
            btnDelete.setOnClickListener { onDeleteClick(session) }
        }
    }
}
