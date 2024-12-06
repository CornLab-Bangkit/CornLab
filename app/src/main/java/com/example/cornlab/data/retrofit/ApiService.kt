package com.example.cornlab.data.retrofit

import com.example.cornlab.data.response.DetailResponse
import com.example.cornlab.data.response.RecomendationsResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("notes?active=1")
    suspend fun getCornRecoms(): Response<RecomendationsResponse>

    @GET("notes?active=0")
    suspend fun getHuskRecoms(): Response<RecomendationsResponse>

    @GET("recom/{id}")
    suspend fun getRecomDetails(@Path("id") id: Int): Response<DetailResponse>

}