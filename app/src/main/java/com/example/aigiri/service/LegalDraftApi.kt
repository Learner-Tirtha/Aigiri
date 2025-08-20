package com.example.aigiri.service

import com.example.aigiri.model.LegalDraftRequest
import com.example.aigiri.model.LegalDraftResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


    interface LegalDraftApi {
        @POST("process_complaint/")
        suspend fun processComplaint(@Body body: LegalDraftRequest): Response<LegalDraftResponse>
    }
