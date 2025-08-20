package com.example.aigiri.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aigiri.model.Report
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class ReportHistoryViewModel : ViewModel() {

    private val _reportList = MutableStateFlow<List<Report>>(emptyList())
    val reportList: StateFlow<List<Report>> = _reportList

    init {
        loadReports()
    }

    private fun loadReports() {
        _reportList.value = listOf(
            Report("March 8, 2023", "cyberbullying"),
            Report("January 15, 2023", "Discrimination")
        )
    }
}