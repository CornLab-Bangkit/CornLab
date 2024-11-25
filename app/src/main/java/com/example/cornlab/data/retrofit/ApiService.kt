package com.example.cornlab.data.retrofit

import com.example.cornlab.data.response.DetailResponse
import com.example.cornlab.data.response.RecomResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("recoms?active=1")
    suspend fun getCornRecoms(): Response<RecomResponse>

    @GET("recoms?active=0")
    suspend fun getHuskRecoms(): Response<RecomResponse>

    @GET("recoms/{id}")
    suspend fun getRecomDetails(@Path("id") id: Int): Response<DetailResponse>

}