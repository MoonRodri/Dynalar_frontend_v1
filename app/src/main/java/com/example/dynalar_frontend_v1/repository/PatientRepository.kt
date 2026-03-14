package com.example.dynalar_frontend_v1.repository

import com.example.dynalar_frontend_v1.model.patient.Patient
import com.example.dynalar_frontend_v1.network.RetrofitClient
import retrofit2.Response


public class PatientRepository {


    private val patientApiService = RetrofitClient.patientApiService

    suspend fun getAllPatients(): List<Patient> {
        return patientApiService.getAllPatients()
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