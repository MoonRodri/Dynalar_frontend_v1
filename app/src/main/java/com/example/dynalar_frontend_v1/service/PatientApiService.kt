package com.example.dynalar_frontend_v1.service

import com.example.dynalar_frontend_v1.model.patient.Patient
import retrofit2.Response
import retrofit2.http.*

interface PatientApiService {


    @GET("patient/all")
    suspend fun getAllPatients(): Response <List<Patient>>


    @GET("patient/{id}")
    suspend fun getIdPatient(@Path("id") id: Long): Response<Patient>

    @POST("patient")
    suspend fun createPatient(@Body patient: Patient): Response<Patient>

    @PUT("patient/update")
    suspend fun updatePatient(@Body patient: Patient): Response<Patient>

    @DELETE("patient/{id}")
    suspend fun deletePatient(@Path("id") id: Long): Response<Unit>
}