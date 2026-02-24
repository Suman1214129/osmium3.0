package com.osmiumai.app.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.osmiumai.app.R
import com.osmiumai.app.databinding.ActivityDownloadsBinding

class DownloadsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDownloadsBinding
    private lateinit var adapter: DownloadsAdapter
    private var isGridView = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupRecyclerView()
        setupClickListeners()
        loadDownloads()
    }

    private fun setupRecyclerView() {
        val downloads = getSampleDownloads()
        adapter = DownloadsAdapter(downloads, isGridView)
        
        binding.rvDownloads.layoutManager = GridLayoutManager(this, 2)
        binding.rvDownloads.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnToggleView.setOnClickListener {
            isGridView = !isGridView
            toggleViewMode()
        }
    }

    private fun toggleViewMode() {
        if (isGridView) {
            binding.rvDownloads.layoutManager = GridLayoutManager(this, 2)
            binding.btnToggleView.setImageResource(R.drawable.ic_menu)
        } else {
            binding.rvDownloads.layoutManager = LinearLayoutManager(this)
            binding.btnToggleView.setImageResource(R.drawable.ic_layers)
        }
        adapter.toggleView(isGridView)
    }

    private fun loadDownloads() {
        val downloads = getSampleDownloads()
        
        if (downloads.isEmpty()) {
            binding.tvNoFiles.visibility = View.VISIBLE
            binding.rvDownloads.visibility = View.GONE
        } else {
            binding.tvNoFiles.visibility = View.GONE
            binding.rvDownloads.visibility = View.VISIBLE
        }
    }

    private fun getSampleDownloads(): List<DownloadItem> {
        return listOf(
            DownloadItem("Physics Chapter 1.pdf", "2.5 MB", ""),
            DownloadItem("Chemistry Notes.pdf", "1.8 MB", ""),
            DownloadItem("Math Solutions.pdf", "3.2 MB", ""),
            DownloadItem("Biology Diagrams.pdf", "4.1 MB", ""),
            DownloadItem("English Grammar.pdf", "1.5 MB", ""),
            DownloadItem("History Timeline.pdf", "2.9 MB", "")
        )
    }
}
