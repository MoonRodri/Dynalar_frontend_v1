package com.example.dynalar_frontend_v1.service

import com.android.volley.Response
import com.example.dynalar_frontend_v1.model.Treatment
import retrofit2.http.*
interface TreatmentApiService {

    @GET("treatment/index")
    suspend fun getAllTreatments(): List<Treatment>

    @GET("treatment/{id}")
    suspend fun getTreatmentById(@Path("id") id: Long): retrofit2.Response<Treatment>

    @POST("treatment")
    suspend fun createTreatment(@Body treatment: Treatment): retrofit2.Response<Treatment>

    @PUT("treatment/update")
    suspend fun updateTreatment(@Body treatment: Treatment): retrofit2.Response<Treatment>

    @DELETE("treatment/{id}")
    suspend fun deleteTreatment(@Path("id") id: Long): retrofit2.Response<Unit>
}