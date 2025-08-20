package com.example.aigiri.model

data class LegalDraftRequest(
    val what_happened_text: String,
    val where_happened: String?,
    val who_involved: String?,
    val witness_name: String?,
    val date: String?,      // yyyy-MM-dd
    val approx_time: String?
)

