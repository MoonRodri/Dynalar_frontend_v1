package com.example.dynalar_frontend_v1.repository

import android.util.Log
import com.example.dynalar_frontend_v1.model.patient.Patient
import com.example.dynalar_frontend_v1.network.RetrofitClient
import retrofit2.Response

class PatientRepository {

    private val patientApiService = RetrofitClient.patientApiService

    suspend fun getAllPatients(): List<Patient> {
        Log.e("CHIVATO", "ENTRADA EN getAllPatients")

        try {
            val response = patientApiService.getAllPatients()

            Log.e("CHIVATO", "CODI RESPOSTA: ${response.code()}")

            if (response.isSuccessful) {
                Log.e("CHIVATO", "RESPOSTA CORRECTE")
                return response.body() ?: emptyList()
            } else {
                Log.e("CHIVATO", "ERROR BODY: ${response.errorBody()?.string()}")
                throw Exception("Error del servidor: ${response.code()}")
            }

        } catch (e: Exception) {
            Log.e("CHIVATO", "EXCEPCIO REAL: ${e.message}", e)
            throw e
        }
    }

    suspend fun getIdPatient(id: Long): Response<Patient> {
        return patientApiService.getIdPatient(id)
    }

    suspend fun deletePatient(id: Long): Response<Unit> {
        return patientApiService.deletePatient(id)
    }

    suspend fun updatePatient(patient: Patient): Response<Patient> {
        return patientApiService.updatePatient(patient)
    }

    suspend fun createPatient(patient: Patient): Response<Patient> {
        return patientApiService.createPatient(patient)
    }
}