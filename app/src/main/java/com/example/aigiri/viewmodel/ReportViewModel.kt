package com.example.aigiri.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportViewModel(
    private val repository: ReportRepository
) : ViewModel() {

    // Form fields
    var incidentDescription = MutableStateFlow("")
        private set
    var involvedPersons = MutableStateFlow("")
        private set
    var witnessName = MutableStateFlow("")
        private set
    var incidentDate: MutableStateFlow<Date?> = MutableStateFlow(null)
        private set
    var incidentTime = MutableStateFlow("")
        private set
    var incidentLocation = MutableStateFlow("")
        private set

    // Generated legal draft
    var legalDraftText = MutableStateFlow("")
        private set
    var legalReferences = MutableStateFlow<List<String>>(emptyList())
        private set
    var isEditingLegalDraft = MutableStateFlow(false)
        private set

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Idle)
    val uiState: StateFlow<ReportUiState> = _uiState

    // Updaters
    fun updateIncidentDescription(v: String) { incidentDescription.value = v }
    fun updateInvolvedPersons(v: String) { involvedPersons.value = v }
    fun updateWitnessName(v: String) { witnessName.value = v }
    fun updateIncidentDate(d: Date) { incidentDate.value = d }
    fun updateIncidentTime(v: String) { incidentTime.value = v }
    fun updateIncidentLocation(v: String) { incidentLocation.value = v }
    fun updateLegalDraftText(v: String) { legalDraftText.value = v }
    fun toggleEditing() { isEditingLegalDraft.value = !isEditingLegalDraft.value }

    // Step 1: Call ML API to generate legal draft
    fun generateLegalDraft(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validateFormForDraft()) {
            onError((uiState.value as? ReportUiState.Error)?.message ?: "Invalid form")
            return
        }
        _uiState.value = ReportUiState.Loading
        viewModelScope.launch {
            try {
                // Server requires a non-null date. Default to today's date if user didn't select one.
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(incidentDate.value ?: Date())
                val res = repository.generateLegalDraft(
                    whatHappened = incidentDescription.value,
                    whereHappened = incidentLocation.value.ifBlank { null },
                    whoInvolved = involvedPersons.value.ifBlank { null },
                    witnessName = witnessName.value.ifBlank { null },
                    date = dateStr,
                    approxTime = incidentTime.value.ifBlank { null }
                )
                res.onSuccess { resp ->
                    val draft = resp.draft_complaint
                    val err = resp.error
                    if (!err.isNullOrBlank()) {
                        _uiState.value = ReportUiState.Error(err)
                        onError(err)
                        return@onSuccess
                    }
                    if (draft.isNullOrBlank()) {
                        _uiState.value = ReportUiState.Error("Server returned no draft")
                        onError("Server returned no draft")
                        return@onSuccess
                    }
                    legalDraftText.value = draft
                    legalReferences.value = resp.relevant_legal_inferences ?: emptyList()
                    isEditingLegalDraft.value = false
                    _uiState.value = ReportUiState.DraftReady
                    onSuccess()
                }.onFailure { e ->
                    _uiState.value = ReportUiState.Error(e.message ?: "Failed to generate draft")
                    onError(e.message ?: "Failed to generate draft")
                }
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(e.message ?: "Failed to generate draft")
                onError(e.message ?: "Failed to generate draft")
            }
        }
    }

    // Step 2: final submit placeholder (integrate backend + PDF later as needed)
    fun confirmAndSubmit(onSuccess: (complaintId: String, pdfUrl: String?) -> Unit, onError: (String) -> Unit) {
        _uiState.value = ReportUiState.Loading
        // Simulate success until backend is wired here
        viewModelScope.launch {
            // Assume a generated complaint ID for now
            val id = System.currentTimeMillis().toString()
            _uiState.value = ReportUiState.Success(id, pdfUrl = null)
            onSuccess(id, null)
        }
    }

    private fun validateFormForDraft(): Boolean {
        return if (incidentDescription.value.isBlank()) {
            _uiState.value = ReportUiState.Error("Please describe the incident")
            false
        } else true
    }
}

sealed class ReportUiState {
    object Idle : ReportUiState()
    object Loading : ReportUiState()
    object DraftReady : ReportUiState()
    data class Success(val complaintId: String, val pdfUrl: String?) : ReportUiState()
    data class Error(val message: String) : ReportUiState()
}