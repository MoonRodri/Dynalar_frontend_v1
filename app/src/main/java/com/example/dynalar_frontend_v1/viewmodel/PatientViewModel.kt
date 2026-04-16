package com.example.dynalar_frontend_v1.viewmodel

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

    // Paciente actualmente seleccionado para ver o editar
    var selectedPatient by mutableStateOf<Patient?>(null)
        private set

    // 1. Obtener todos los pacientes (usando la lógica de logs de tu repositorio)
    fun getPatients() {
        viewModelScope.launch {
            uiStatePatient = InterfaceGlobal.Loading
            try {
                // getAllPatients ya maneja la extracción del cuerpo y errores
                allPatients = patientRepository.getAllPatients()
                filteredPatients = allPatients
                loadedPatients = 0
                loadMorePatients()
            } catch (e: Exception) {
                uiStatePatient = InterfaceGlobal.Error("Error al cargar pacientes: ${e.message}")
            }
        }
    }

    // 2. Cargar paciente por ID (para refrescar datos antes de editar)
    fun getPatientById(id: Long) {
        viewModelScope.launch {
            try {
                val response = patientRepository.getIdPatient(id)
                if (response.isSuccessful) {
                    selectedPatient = response.body()
                } else {
                    Log.e("PatientViewModel", "Error al obtener paciente: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("PatientViewModel", "Excepción al obtener paciente: ${e.message}")
            }
        }
    }

    // 3. ACTUALIZAR PACIENTE (Lógica central de la Issue #59)
    fun updatePatient(patient: Patient) {
        viewModelScope.launch {
            try {
                val response = patientRepository.updatePatient(patient)

                if (response.isSuccessful) {
                    val updatedPatient = response.body()
                    // Actualizamos el paciente seleccionado en memoria para refrescar el Perfil
                    selectedPatient = updatedPatient
                    // Refrescamos la lista general
                    getPatients()
                    Log.d("PatientViewModel", "Paciente actualizado correctamente")
                } else {
                    Log.e("PatientViewModel", "Error API al actualizar: ${response.code()}")
                    uiStatePatient = InterfaceGlobal.Error("No se pudo actualizar el paciente")
                }
            } catch (e: Exception) {
                Log.e("PatientViewModel", "Excepción al actualizar: ${e.message}")
                uiStatePatient = InterfaceGlobal.Error("Error de red al actualizar")
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
                }
            } catch (e: Exception) {
                Log.e("PatientViewModel", "Error eliminando: ${e.message}")
            }
        }
    }

    fun createPatient(patient: Patient) {
        viewModelScope.launch {
            try {
                val response = patientRepository.createPatient(patient)
                if (response.isSuccessful) {
                    getPatients()
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
}