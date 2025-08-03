package com.example.aigiri.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReportViewModel : ViewModel() {

    private val _reportText = MutableStateFlow(
        "The comments were made in the presence of several team members, including [Witness Names]. I have previously attempted to address this issue directly with [Name], requesting that they stop making such comments, but the behavior has continued.\n\nThis incident has affected my ability to work effectively and has created a hostile work environment. I am requesting appropriate action to address this issue and prevent similar incidents in the future."
    )
    val reportText: StateFlow<String> = _reportText

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    fun toggleEditing() {
        _isEditing.value = !_isEditing.value
    }

    fun updateReportText(newText: String) {
        _reportText.value = newText
    }

    fun saveReport() {
        _isEditing.value = false
        // Add saving logic here (e.g., save to local storage or send to backend)
    }
}
