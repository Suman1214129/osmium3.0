package com.example.susu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.susu.databinding.ItemUploadedFileBinding

class UploadedFilesAdapter(
    private val files: MutableList<UploadedFile>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<UploadedFilesAdapter.FileViewHolder>() {

    inner class FileViewHolder(private val binding: ItemUploadedFileBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(file: UploadedFile, position: Int) {
            binding.fileName.text = file.name
            binding.fileSize.text = file.size
            binding.deleteButton.setOnClickListener {
                onDeleteClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemUploadedFileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(files[position], position)
    }

    override fun getItemCount() = files.size
}