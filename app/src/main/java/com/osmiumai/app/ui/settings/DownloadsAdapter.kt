package com.osmiumai.app.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osmiumai.app.databinding.ItemDownloadGridBinding
import com.osmiumai.app.databinding.ItemDownloadListBinding

class DownloadsAdapter(
    private var items: List<DownloadItem>,
    private var isGridView: Boolean = true
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_GRID = 0
        private const val VIEW_TYPE_LIST = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGridView) VIEW_TYPE_GRID else VIEW_TYPE_LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_GRID) {
            val binding = ItemDownloadGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            GridViewHolder(binding)
        } else {
            val binding = ItemDownloadListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ListViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is GridViewHolder) {
            holder.bind(item)
        } else if (holder is ListViewHolder) {
            holder.bind(item)
        }
    }

    override fun getItemCount() = items.size

    fun toggleView(isGrid: Boolean) {
        isGridView = isGrid
        notifyDataSetChanged()
    }

    inner class GridViewHolder(private val binding: ItemDownloadGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DownloadItem) {
            binding.tvFileName.text = item.fileName
            binding.tvFileSize.text = item.fileSize
        }
    }

    inner class ListViewHolder(private val binding: ItemDownloadListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DownloadItem) {
            binding.tvFileName.text = item.fileName
            binding.tvFileSize.text = item.fileSize
        }
    }
}
