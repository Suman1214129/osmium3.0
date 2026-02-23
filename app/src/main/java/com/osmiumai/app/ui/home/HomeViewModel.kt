package com.osmiumai.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    data class TestItem(
        val date: String,
        val title: String,
        val marks: String,
        val accuracy: String
    )

    private val _tests = MutableLiveData(
        listOf(
            TestItem("16 May, 2025", "DSA with Java", "78/150", "91.1%"),
            TestItem("26 May, 2025", "JEE MAIN 1", "181/300", "74.6%")
        )
    )

    val tests: LiveData<List<TestItem>> = _tests
}
