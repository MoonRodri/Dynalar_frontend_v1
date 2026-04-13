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

    var selectedPatient by mutableStateOf<Patient?>(null)
        private set

    // Obtener Pacientes
    fun getPatients() {
        viewModelScope.launch {
            uiStatePatient = InterfaceGlobal.Loading
            try {
                //Descargamos los pacientes
                val rawPatients = patientRepository.getAllPatients()

                //Ordenamos toda la lista ignorando mayúsculas/minúsculas
                allPatients = rawPatients.sortedBy {
                    it.name?.lowercase() ?: ""
                }

                filteredPatients = allPatients
                loadedPatients = 0
                loadMorePatients()
            } catch (e: Exception) {
                val errorCompleto = e.toString()
                uiStatePatient = InterfaceGlobal.Error("CHIVATO: $errorCompleto")
            }
        }
    }

    fun getPatientById(id: Long) {
        viewModelScope.launch {

            val patient = allPatients.find { it.id == id }
            if (patient != null) {
                selectedPatient = patient
            } else {

                try {

                    allPatients = patientRepository.getAllPatients()
                    selectedPatient = allPatients.find { it.id == id }
                } catch (e: Exception) {
                    Log.e("PatientViewModel", "Error cargando paciente individual: ${e.message}")
                }
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
                patientRepository.deletePatient(id)
                getPatients()
            } catch (e: Exception) {
                Log.e("PatientViewModel", "ERROR en deletePatient: ${e.message}")
                uiStatePatient = InterfaceGlobal.Error(e.message)
            }
        }
    }

    fun updatePatient(patient: Patient) {
        viewModelScope.launch {
            try {
                patientRepository.updatePatient(patient)
                getPatients()
            } catch (e: Exception) {
                uiStatePatient = InterfaceGlobal.Error(e.message)
            }
        }
    }

    fun createPatient(patient: Patient) {
        viewModelScope.launch {
            try {
                patientRepository.createPatient(patient)
                getPatients()
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