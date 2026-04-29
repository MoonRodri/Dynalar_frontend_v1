package com.example.dynalar_frontend_v1.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynalar_frontend_v1.model.patient.Patient
import com.example.dynalar_frontend_v1.interfaces.InterfaceGlobal
import com.example.dynalar_frontend_v1.repository.PatientRepository
import kotlinx.coroutines.launch

class PatientViewModel: ViewModel() {

    var uiStatePatient by mutableStateOf<InterfaceGlobal<List<Patient>>>(InterfaceGlobal.Idle)

    private var filteredPatients: List<Patient> = emptyList()
    private val patientRepository = PatientRepository()
    private var allPatients: List<Patient> = emptyList()
    private val pageSize = 10
    private var loadedPatients = 0

    var selectedPatient by mutableStateOf<Patient?>(null)
        private set

    var uploadState by mutableStateOf<InterfaceGlobal<Unit>>(InterfaceGlobal.Idle)
        private set

    // Obtener Pacientes
    fun getPatients() {
        viewModelScope.launch {
            uiStatePatient = InterfaceGlobal.Loading
            try {
                //Descargamos los pacientes
                val sourcePatients = patientRepository.getAllPatients()

                //Ordenamos toda la lista ignorando mayúsculas/minúsculas
                allPatients = sourcePatients.sortedBy {
                    it.name?.lowercase() ?: ""
                }

                filteredPatients = allPatients
                loadedPatients = 0
                loadMorePatients()
            } catch (e: Exception) {
                Log.e("PatientViewModel", "Error loading patients: ${e.message}")
                uiStatePatient = InterfaceGlobal.Error(e.message)
            }
        }
    }

    fun getPatientById(id: Long) {
        viewModelScope.launch {
            try {
                val response = patientRepository.getIdPatient(id)
                if (response.isSuccessful) {
                    selectedPatient = response.body()
                } else {
                    Log.e("PatientViewModel", "Error loading patient by id: ${response.code()}")
                    selectedPatient = allPatients.find { it.id == id }
                }
            } catch (e: Exception) {
                Log.e("PatientViewModel", "Error loading single patient: ${e.message}")
                selectedPatient = allPatients.find { it.id == id }
            }
        }
    }

    fun loadMorePatients() {
        if (loadedPatients >= filteredPatients.size) return
        val next = (loadedPatients + pageSize).coerceAtMost(filteredPatients.size)
        val visiblePatients = filteredPatients.subList(0, next)
        loadedPatients = next
        uiStatePatient = InterfaceGlobal.Success(visiblePatients)
    }

    fun deletePatient(id: Long) {
        viewModelScope.launch {
            try {
                val response = patientRepository.deletePatient(id)
                if (response.isSuccessful) {
                    getPatients()
                } else {
                    uiStatePatient = InterfaceGlobal.Error("No se pudo eliminar el paciente")
                }
            } catch (e: Exception) {
                Log.e("PatientViewModel", "ERROR en deletePatient: ${e.message}")
                uiStatePatient = InterfaceGlobal.Error(e.message)
            }
        }
    }

    fun updatePatient(patient: Patient) {
        viewModelScope.launch {
            try {
                val response = patientRepository.updatePatient(patient)

                if (response.isSuccessful) {
                    selectedPatient = response.body()
                    getPatients()
                    Log.d("PatientViewModel", "Paciente actualizado correctamente")
                } else {
                    Log.e("PatientViewModel", "Error API al actualizar: ${response.code()}")
                    uiStatePatient = InterfaceGlobal.Error("No se pudo actualizar el paciente")
                }
            } catch (e: Exception) {
                Log.e("PatientViewModel", "Excepción al actualizar: ${e.message}")
                uiStatePatient = InterfaceGlobal.Error(e.message)
            }
        }
    }

    fun createPatient(patient: Patient) {
        viewModelScope.launch {
            try {
                val response = patientRepository.createPatient(patient)
                if (response.isSuccessful) {
                    getPatients()
                } else {
                    uiStatePatient = InterfaceGlobal.Error("No se pudo crear el paciente")
                }
            } catch (e: Exception) {
                uiStatePatient = InterfaceGlobal.Error(e.message)
            }
        }
    }

    fun searchPatients(query: String) {
        filteredPatients = if (query.isBlank()) allPatients
        else allPatients.filter {
            it.name?.contains(query, ignoreCase = true) == true ||
                    it.lastName?.contains(query, ignoreCase = true) == true
        }
        loadedPatients = 0
        loadMorePatients()
    }

    fun selectPatient(patient: Patient) {
        selectedPatient = patient
    }

    fun uploadPatientFiles(
        context: Context,
        patientId: Long,
        uris: List<Uri>,
        onSuccess: () -> Unit
    ) {
        if (uris.isEmpty()) return

        viewModelScope.launch {
            uploadState = InterfaceGlobal.Loading
            try {
                patientRepository.uploadPatientDocuments(
                    contentResolver = context.contentResolver,
                    patientId = patientId,
                    uris = uris
                )
                getPatientById(patientId)
                uploadState = InterfaceGlobal.Success(Unit)
                onSuccess()
            } catch (e: Exception) {
                uploadState = InterfaceGlobal.Error(e.message)
            }
        }
    }

    fun deletePatientDocument(patientId: Long, documentId: Long) {
        viewModelScope.launch {
            try {
                val response = patientRepository.deletePatientDocument(documentId)
                if (!response.isSuccessful) {
                    throw Exception("Error borrando archivo: ${response.code()}")
                }
                getPatientById(patientId)
            } catch (e: Exception) {
                Log.e("PatientViewModel", "ERROR en deletePatientDocument: ${e.message}")
                selectedPatient = selectedPatient?.let { current ->
                    if (current.id == patientId) current else current
                }
            }
        }
    }
}