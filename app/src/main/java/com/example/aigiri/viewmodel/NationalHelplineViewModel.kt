package com.example.aigiri.viewmodel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.aigiri.model.HelplineContact

class NationalHelplineViewModel : ViewModel() {

    // StateFlow holding the list of helplines
    private val _helplineList = MutableStateFlow<List<HelplineContact>>(emptyList())
    val helplineList: StateFlow<List<HelplineContact>> = _helplineList

    init {
        _helplineList.value = listOf(
            HelplineContact("112", "National Helpline", Icons.Default.Phone, Color(0xFFB3E5FC)),
            HelplineContact("108", "Ambulance", Icons.Default.MedicalServices, Color(0xFFE1F5FE)),
            HelplineContact("102", "Pregnancy Medic", Icons.Default.Favorite, Color(0xFFF8BBD9)),
            HelplineContact("101", "Fire Service", Icons.Default.LocalFireDepartment, Color(0xFFFFE0B2)),
            HelplineContact("100", "Police", Icons.Default.Shield, Color(0xFFE8F5E8)),
            HelplineContact("1098", "Child Helpline", Icons.Default.ChildCare, Color(0xFFE3F2FD)),
            HelplineContact("1091", "Women Helpline", Icons.Default.SupportAgent, Color(0xFFF3E5F5)),
            HelplineContact("1076", "Kisan Call Centre", Icons.Default.Psychology, Color(0xFFF1F8E9)),
            HelplineContact("1077", "Tourist Helpline", Icons.Default.SupportAgent, Color(0xFFE0F2F1)),
            HelplineContact("1078", "Pollution Control", Icons.Default.Report, Color(0xFFF9FBE7)),
            HelplineContact("1090", "Senior Citizens", Icons.Default.Elderly, Color(0xFFE8EAF6)),
            HelplineContact("1950", "Cyber Crime", Icons.Default.Security, Color(0xFFFFF3E0)),
            HelplineContact("1947", "Health Ministry", Icons.Default.MedicalServices, Color(0xFFE8F5E8)),
            HelplineContact("1139", "Railway Enquiry", Icons.Default.SupportAgent, Color(0xFFE1F5FE)),
            HelplineContact("1070", "Blood Bank", Icons.Default.Favorite, Color(0xFFFFEBEE)),
            HelplineContact("14419", "Disaster Management", Icons.Default.EmergencyShare, Color(0xFFFFF8E1)),
            HelplineContact("1800-11-0031", "Mental Health", Icons.Default.Psychology, Color(0xFFF3E5F5)),
            HelplineContact("1800-11-4000", "Anti-Corruption", Icons.Default.Balance, Color(0xFFE8F5E8)),
            HelplineContact("1800-180-1104", "LPG Emergency", Icons.Default.LocalFireDepartment, Color(0xFFFFE0B2)),
            HelplineContact("1800-11-2959", "Railway Security", Icons.Default.Security, Color(0xFFE1F5FE)),
            HelplineContact("1800-11-8111", "Organ Donation", Icons.Default.MedicalServices, Color(0xFFE8F5E8)),
            HelplineContact("1800-103-1139", "COVID-19 Helpline", Icons.Default.MedicalServices, Color(0xFFE8F5E8)),
            HelplineContact("1800-11-0007", "Road Accident", Icons.Default.EmergencyShare, Color(0xFFFFEBEE)),
            HelplineContact("1800-11-2870", "Disabled Persons", Icons.Default.AccessibilityNew, Color(0xFFE8EAF6)),
            HelplineContact("1800-11-4900", "Anti-Narcotics", Icons.Default.Security, Color(0xFFF3E5F5))
        )
    }
}
