package com.example.cornlab.data.retrofit

import com.example.cornlab.data.response.AnalyzeResponse
import retrofit2.http.Multipart
import retrofit2.http.POST
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Part

interface AnalyzeApiService {
    @Multipart
    @POST("predict")
    suspend fun startAnalyze(
        @Part file: MultipartBody.Part
    ): Response<AnalyzeResponse>
}

