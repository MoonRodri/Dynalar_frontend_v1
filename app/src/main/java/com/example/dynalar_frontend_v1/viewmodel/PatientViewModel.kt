package com.example.dynalar_frontend_v1.viewmodel

import android.util.Log // <- IMPORTANTE: Añadida la importación para los Logs
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

    // Obtener Pacientes
    fun getPatients() {
        viewModelScope.launch {
            uiStatePatient = InterfaceGlobal.Loading
            try {

                allPatients = patientRepository.getAllPatients()
                filteredPatients = allPatients
                loadedPatients = 0
                loadMorePatients()
            } catch (e: Exception) {
                // Aquí forzamos a que el error completo (e.toString()) viaje a la pantalla
                val errorCompleto = e.toString()
                uiStatePatient = InterfaceGlobal.Error("CHIVATO: $errorCompleto")
            }
        }
    }

    // Cargar más pacientes (scroll infinito)
    fun loadMorePatients() {
        if (loadedPatients >= filteredPatients.size) return

        val next = (loadedPatients + pageSize).coerceAtMost(filteredPatients.size)
        val visiblePatients = filteredPatients.subList(0, next)
        loadedPatients = next

        uiStatePatient = InterfaceGlobal.Success(visiblePatients)
    }

    // Eliminar paciente
    fun deletePatient(id: Long) {
        viewModelScope.launch {
            try {
                patientRepository.deletePatient(id)
                getPatients() // sirve para actualizar al entrar
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("PatientViewModel", "ERROR GRAVE en deletePatient: ${e.message}", e)
                uiStatePatient = InterfaceGlobal.Error(e.message)
            }
        }
    }

    // Actualizar paciente
    fun updatePatient(patient: Patient) {
        viewModelScope.launch {
            try {
                patientRepository.updatePatient(patient)
                getPatients()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("PatientViewModel", "ERROR GRAVE en updatePatient: ${e.message}", e)
                uiStatePatient = InterfaceGlobal.Error(e.message)
            }
        }
    }

    // Crear Pacientes
    fun createPatient(patient: Patient) {
        viewModelScope.launch {
            try {
                patientRepository.createPatient(patient)
                getPatients()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("PatientViewModel", "ERROR GRAVE en createPatient: ${e.message}", e)
                uiStatePatient = InterfaceGlobal.Error(e.message)
            }
        }
    }

    // Buscar pacientes por nombre y apellido
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