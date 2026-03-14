package com.example.dynalar_frontend_v1.service

import com.example.dynalar_frontend_v1.model.patient.Patient
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PatientApiService {


    @GET("patient/index")
    suspend fun getAllPatients(): List<Patient>  // Lista de pacientes

    @GET("patient/{id}")
    suspend fun getIdPatient(@Path("id") id: Long): Response<Patient>  // Paciente por ID

    @POST("patient")
    suspend fun createPatient(@Body patient: Patient): Response<Patient>  // Crear paciente

    @PUT("patient/update")
    suspend fun updatePatient(@Body patient: Patient): Response<Patient>  // Actualizar paciente
    @DELETE("patient/{id}")
    suspend fun deletePatient(@Path("id") id: Long): Response<Unit>  // Borrar paciente

    @GET("patient/name")
    suspend fun findNurseByName(
        @Query("name") name: String
    ): Response<Patient>
}
