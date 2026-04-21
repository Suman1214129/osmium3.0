package com.osmiumai.app.ui.ai_mentor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.osmiumai.app.R

class AttachmentPreviewAdapter(
    private val onRemove: (AttachmentItem) -> Unit
) : RecyclerView.Adapter<AttachmentPreviewAdapter.ViewHolder>() {

    private val items = mutableListOf<AttachmentItem>()

    fun addItem(item: AttachmentItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun removeItem(item: AttachmentItem) {
        val idx = items.indexOfFirst { it.uri == item.uri }
        if (idx >= 0) {
            items.removeAt(idx)
            notifyItemRemoved(idx)
        }
    }

    fun getItems(): List<AttachmentItem> = items.toList()

    fun clear() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun isEmpty() = items.isEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attachment_preview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivPreview: ImageView = itemView.findViewById(R.id.ivPreview)
        private val layoutFile: LinearLayout = itemView.findViewById(R.id.layoutFile)
        private val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        private val btnRemove: ImageView = itemView.findViewById(R.id.btnRemove)

        fun bind(item: AttachmentItem) {
            when (item.type) {
                AttachmentType.IMAGE -> {
                    ivPreview.visibility = View.VISIBLE
                    layoutFile.visibility = View.GONE
                    ivPreview.setImageURI(item.uri)
                }
                AttachmentType.FILE -> {
                    ivPreview.visibility = View.GONE
                    layoutFile.visibility = View.VISIBLE
                    tvFileName.text = item.name
                }
            }
            btnRemove.setOnClickListener { onRemove(item) }
        }
    }
}
