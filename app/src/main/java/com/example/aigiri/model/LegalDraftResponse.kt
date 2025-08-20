package com.example.aigiri.model

data class LegalDraftResponse(
    val draft_complaint: String?,
    val relevant_legal_inferences: List<String>? = emptyList(),
    val error: String? = null
)

