//package com.example.aigiri.viewmodel
//
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import com.example.aigiri.model.*
//import androidx.compose.runtime.*
//
//data class FakeCallUiState(
//    val presets: List<Preset> = DefaultPresets.presets,
//    val selectedPreset: Preset? = DefaultPresets.presets.firstOrNull(),
//    val selectedDuration: CallDuration = CallDuration.TEN_SEC,
//    val inCall: Boolean = false,
//    val showEditDialog: Boolean = false,
//    val showAddDialog: Boolean = false,
//    val editingPreset: Preset? = null
//)
//
//data class CallProgressUiState(
//    val callState: String = "Ringing...",
//    val isSpeakerOn: Boolean = false,
//    val isMuted: Boolean = false,
//    val currentSpeakingText: String = "",
//    val isPersonSpeaking: Boolean = false,
//    val remainingTime: Int = 0,
//    val callEnded: Boolean = false
//)
//
//class FakeCallViewModel : ViewModel() {
//    private val _uiState = MutableStateFlow(FakeCallUiState())
//    val uiState: StateFlow<FakeCallUiState> = _uiState.asStateFlow()
//
//    private val _callProgressState = MutableStateFlow(CallProgressUiState())
//    val callProgressState: StateFlow<CallProgressUiState> = _callProgressState.asStateFlow()
//
//    fun selectPreset(preset: Preset) {
//        _uiState.value = _uiState.value.copy(selectedPreset = preset)
//    }
//
//    fun selectDuration(duration: CallDuration) {
//        _uiState.value = _uiState.value.copy(selectedDuration = duration)
//    }
//
//    fun startCall() {
//        if (_uiState.value.selectedPreset != null) {
//            _uiState.value = _uiState.value.copy(inCall = true)
//            _callProgressState.value = _callProgressState.value.copy(
//                remainingTime = _uiState.value.selectedDuration.seconds,
//                callEnded = false
//            )
//        }
//    }
//
//    fun endCall() {
//        _uiState.value = _uiState.value.copy(inCall = false)
//        _callProgressState.value = CallProgressUiState()
//    }
//
//    fun showEditDialog(preset: Preset) {
//        _uiState.value = _uiState.value.copy(
//            showEditDialog = true,
//            editingPreset = preset
//        )
//    }
//
//    fun hideEditDialog() {
//        _uiState.value = _uiState.value.copy(
//            showEditDialog = false,
//            editingPreset = null
//        )
//    }
//
//    fun showAddDialog() {
//        if (_uiState.value.presets.size < 3) {
//            _uiState.value = _uiState.value.copy(showAddDialog = true)
//        }
//    }
//
//    fun hideAddDialog() {
//        _uiState.value = _uiState.value.copy(showAddDialog = false)
//    }
//
//    fun savePreset(name: String, phone: String) {
//        val editingPreset = _uiState.value.editingPreset ?: return
//        val updatedPresets = _uiState.value.presets.map { preset ->
//            if (preset.id == editingPreset.id) {
//                preset.copy(name = name, phone = phone)
//            } else {
//                preset
//            }
//        }
//        val updatedSelectedPreset = if (_uiState.value.selectedPreset?.id == editingPreset.id) {
//            _uiState.value.selectedPreset?.copy(name = name, phone = phone)
//        } else {
//            _uiState.value.selectedPreset
//        }
//
//        _uiState.value = _uiState.value.copy(
//            presets = updatedPresets,
//            selectedPreset = updatedSelectedPreset,
//            showEditDialog = false,
//            editingPreset = null
//        )
//    }
//
//    fun deletePreset() {
//        val editingPreset = _uiState.value.editingPreset ?: return
//        val updatedPresets = _uiState.value.presets.filter { it.id != editingPreset.id }
//        val updatedSelectedPreset = if (_uiState.value.selectedPreset?.id == editingPreset.id) {
//            updatedPresets.firstOrNull()
//        } else {
//            _uiState.value.selectedPreset
//        }
//
//        _uiState.value = _uiState.value.copy(
//            presets = updatedPresets,
//            selectedPreset = updatedSelectedPreset,
//            showEditDialog = false,
//            editingPreset = null
//        )
//    }
//
//    fun addPreset(name: String, phone: String, voiceType: VoiceType, relationship: String) {
//        val newPreset = Preset(
//            name = name,
//            phone = phone,
//            color = primaryAigiri,
//            icon = DefaultPresets.availableIcons.random(),
//            voiceType = voiceType,
//            relationship = relationship
//        )
//        _uiState.value = _uiState.value.copy(
//            presets = _uiState.value.presets + newPreset,
//            showAddDialog = false
//        )
//    }
//
//    // Call progress methods
//    fun setCallState(state: String) {
//        _callProgressState.value = _callProgressState.value.copy(callState = state)
//    }
//
//    fun toggleSpeaker() {
//        _callProgressState.value = _callProgressState.value.copy(
//            isSpeakerOn = !_callProgressState.value.isSpeakerOn
//        )
//    }
//
//    fun toggleMute() {
//        _callProgressState.value = _callProgressState.value.copy(
//            isMuted = !_callProgressState.value.isMuted
//        )
//    }
//
//    fun setSpeakingText(text: String) {
//        _callProgressState.value = _callProgressState.value.copy(currentSpeakingText = text)
//    }
//
//    fun setPersonSpeaking(speaking: Boolean) {
//        _callProgressState.value = _callProgressState.value.copy(isPersonSpeaking = speaking)
//    }
//
//    fun updateRemainingTime(time: Int) {
//        _callProgressState.value = _callProgressState.value.copy(remainingTime = time)
//    }
//
//    fun setCallEnded(ended: Boolean) {
//        _callProgressState.value = _callProgressState.value.copy(callEnded = ended)
//    }
//}