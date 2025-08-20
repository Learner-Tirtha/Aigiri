package com.example.aigiri.repository

import com.example.aigiri.model.LegalDraftRequest
import com.example.aigiri.model.LegalDraftResponse
import com.example.aigiri.network.LegalDraftClient


class ReportRepository {
    suspend fun generateLegalDraft(
        whatHappened: String,
        whereHappened: String?,
        whoInvolved: String?,
        witnessName: String?,
        date: String?,
        approxTime: String?
    ): Result<LegalDraftResponse> {
        return try {
            val req = LegalDraftRequest(
                what_happened_text = whatHappened,
                where_happened = whereHappened,
                who_involved = whoInvolved,
                witness_name = witnessName,
                date = date,
                approx_time = approxTime
            )
            val resp = LegalDraftClient.api.processComplaint(req)
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null && body.error.isNullOrBlank() && !body.draft_complaint.isNullOrBlank()) {
                    Result.success(body)
                } else {
                    val msg = body?.error ?: "Server returned no draft"
                    Result.failure(Exception(msg))
                }
            } else {
                Result.failure(Exception(resp.errorBody()?.string() ?: "Failed to generate legal draft"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to generate legal draft: ${e.message}", e))
        }
    }
}