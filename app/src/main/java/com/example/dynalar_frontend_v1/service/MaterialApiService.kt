package com.example.dynalar_frontend_v1.service

import com.example.dynalar_frontend_v1.model.Material
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MaterialApiService {

    @GET("material/index")
    suspend fun getAllMaterials(): List<Material>

    @GET("material/{id}")
    suspend fun getMaterialById(@Path("id") id: Long): Material

    @POST("material/create")
    suspend fun createMaterial(@Body material: Material): Material

    @PUT("material/update/{id}")
    suspend fun updateMaterial(@Path("id") id: Long, @Body material: Material): Material

    @DELETE("material/delete/{id}")
    suspend fun deleteMaterial(@Path("id") id: Long)
}
