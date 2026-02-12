package com.example.susu.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.susu.databinding.ItemTestCardBinding

class TestsAdapter(private var tests: List<HomeViewModel.TestItem>) : 
    RecyclerView.Adapter<TestsAdapter.TestViewHolder>() {

    class TestViewHolder(private val binding: ItemTestCardBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(test: HomeViewModel.TestItem) {
            binding.tvDate.text = test.date
            binding.tvTitle.text = test.title
            binding.tvMarks.text = "Marks: ${test.marks}"
            binding.tvAccuracy.text = "Accuracy: ${test.accuracy}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val binding = ItemTestCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(tests[position])
    }

    override fun getItemCount() = tests.size

    fun updateTests(newTests: List<HomeViewModel.TestItem>) {
        tests = newTests
        notifyDataSetChanged()
    }
}