package com.example.cornlab.data.retrofit

import com.example.cornlab.data.response.DetailResponse
import com.example.cornlab.data.response.RecomendationsResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("recoms?active=1")
    suspend fun getCornRecoms(): Response<RecomendationsResponse>

    @GET("recoms?active=0")
    suspend fun getHuskRecoms(): Response<RecomendationsResponse>

    @GET("recoms/{id}")
    suspend fun getRecomDetails(@Path("id") id: Int): Response<DetailResponse>

}