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

    private val patientRepository = PatientRepository()
    private var allPatientsList = mutableListOf<Patient>()
    private var currentPage = 0
    private var isLastPage = false
    var isFetching by mutableStateOf(false)
        private set
    private var currentQuery = ""
    private val pageSize = 20

    var selectedPatient by mutableStateOf<Patient?>(null)
        private set

    var uploadState by mutableStateOf<InterfaceGlobal<Unit>>(InterfaceGlobal.Idle)
        private set

    var isDeleteHintShown by mutableStateOf(false)

    fun getPatients() {
        if (isFetching) return
        viewModelScope.launch {
            isFetching = true
            uiStatePatient = InterfaceGlobal.Loading
            currentPage = 0
            isLastPage = false
            currentQuery = ""
            allPatientsList.clear()

            try {
                val pageResponse = patientRepository.getAllPatients(page = currentPage, size = pageSize)
                allPatientsList.addAll(pageResponse.content)
                
                val totalPags = pageResponse.pageMetadata?.totalPages ?: pageResponse.totalPages
                isLastPage = currentPage >= (totalPags - 1)
                uiStatePatient = if (allPatientsList.isEmpty()) {
                    InterfaceGlobal.NotFound
                } else {
                    InterfaceGlobal.Success(allPatientsList.toList())
                }
            } catch (e: Exception) {
                Log.e("PatientViewModel", "Error loading patients: ${e.message}")
                uiStatePatient = InterfaceGlobal.Error(e.message)
            } finally {
                isFetching = false
            }
        }
    }

    fun loadNextPage() {
        if (isFetching) {
            Log.d("Pagination", "loadNextPage abortat: Ja s'està carregant (isFetching = true)")
            return
        }
        if (isLastPage) {
            Log.d("Pagination", "loadNextPage abortat: Ja s'ha arribat a l'última pàgina (isLastPage = true)")
            return
        }

        viewModelScope.launch {
            isFetching = true
            val nextPage = currentPage + 1
            Log.d("Pagination", ">>> Iniciant carga de pàgina: $nextPage (Query: '$currentQuery')")
            
            try {
                val response = if (currentQuery.isNotEmpty()) {
                    patientRepository.searchPatients(currentQuery, page = nextPage, size = pageSize)
                } else {
                    patientRepository.getAllPatients(page = nextPage, size = pageSize)
                }

                Log.d("Pagination", "Resposta rebuda. Pacients en pàgina: ${response.content.size}, Total pàgines: ${response.totalPages}")

                if (response.content.isEmpty()) {
                    isLastPage = true
                    Log.d("Pagination", "La pàgina està buida. Marcant com a última pàgina.")
                } else {
                    val currentPatients = (uiStatePatient as? InterfaceGlobal.Success)?.data ?: emptyList()
                    
                    val newPatients = response.content.filter { new ->
                        currentPatients.none { it.id == new.id } 
                    }
                    
                    Log.d("Pagination", "Pacients nous detectats (sense duplicats): ${newPatients.size}")

                    // Actualitzem la pàgina actual SEMPRE que la resposta no sigui buida
                    currentPage = nextPage
                    
                    if (newPatients.isNotEmpty()) {
                        Log.d("Pagination", "Afegint ${newPatients.size} pacients nous a la llista de ${allPatientsList.size}")
                        allPatientsList.addAll(newPatients)
                        
                        // Creem una COPIA nova de la llista per forçar a Compose a refrescar
                        val updatedList = allPatientsList.toList()
                        uiStatePatient = InterfaceGlobal.Success(updatedList)
                        Log.d("Pagination", "UI State actualitzat amb ${updatedList.size} pacients totals.")
                    } else {
                        Log.d("Pagination", "No hi ha pacients realment nous per afegir (tots eren duplicats).")
                    }
                }
            } catch (e: Exception) {
                Log.e("Pagination", "ERROR en loadNextPage: ${e.message}", e)
            } finally {
                isFetching = false
            }
        }
    }

    fun searchPatients(query: String) {
        if (isFetching) return
        currentQuery = query
        
        if (query.isBlank()) {
            getPatients()
            return
        }
        viewModelScope.launch {
            isFetching = true
            uiStatePatient = InterfaceGlobal.Loading
            currentPage = 0
            isLastPage = false
            allPatientsList.clear()

            try {
                val pageResponse = patientRepository.searchPatients(query = query, page = currentPage, size = pageSize)
                allPatientsList.addAll(pageResponse.content)
                isLastPage = currentPage >= pageResponse.totalPages - 1

                if (allPatientsList.isEmpty()) {
                    uiStatePatient = InterfaceGlobal.NotFound
                } else {
                    uiStatePatient = InterfaceGlobal.Success(allPatientsList.toList())
                }
            } catch (e: Exception) {
                Log.e("PatientViewModel", "Error searching patients: ${e.message}")
                uiStatePatient = InterfaceGlobal.Error(e.message)
            } finally {
                isFetching = false
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
                    selectedPatient = allPatientsList.find { it.id == id }
                }
            } catch (e: Exception) {
                Log.e("PatientViewModel", "Error loading single patient: ${e.message}")
                selectedPatient = allPatientsList.find { it.id == id }
            }
        }
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

    fun createPatient(patient: Patient, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = patientRepository.createPatient(patient)
                if (response.isSuccessful) {
                    getPatients()
                    onSuccess()
                } else {
                    uiStatePatient = InterfaceGlobal.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                uiStatePatient = InterfaceGlobal.Error(e.message)
            }
        }
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