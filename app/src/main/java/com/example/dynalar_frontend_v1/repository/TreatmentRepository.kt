package com.example.dynalar_frontend_v1.repository


import com.example.dynalar_frontend_v1.model.Treatment
import com.example.dynalar_frontend_v1.network.RetrofitClient
import retrofit2.Response

class TreatmentRepository {

    // Obtenemos el servicio desde tu RetrofitClient centralizado
    private val treatmentApiService = RetrofitClient.treatmentApiService

    suspend fun getAllTreatments(): List<Treatment> {
        // En tu controlador de Java es /treatment/index
        return treatmentApiService.getAllTreatments()
    }

    suspend fun getTreatmentById(id: Long): Response<Treatment> {
        return treatmentApiService.getTreatmentById(id)
    }

    suspend fun createTreatment(treatment: Treatment): Response<Treatment> {
        return treatmentApiService.createTreatment(treatment)
    }

    suspend fun updateTreatment(treatment: Treatment): Response<Treatment> {
        return treatmentApiService.updateTreatment(treatment)
    }

    suspend fun deleteTreatment(id: Long): Response<Unit> {
        return treatmentApiService.deleteTreatment(id)
    }
}