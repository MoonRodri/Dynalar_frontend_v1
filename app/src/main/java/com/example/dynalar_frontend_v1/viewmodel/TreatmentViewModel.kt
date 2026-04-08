package com.example.dynalar_frontend_v1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynalar_frontend_v1.interfaces.InterfaceGlobal
import com.example.dynalar_frontend_v1.model.Treatment
import com.example.dynalar_frontend_v1.repository.TreatmentRepository
import kotlinx.coroutines.launch

// Asumo que tienes una instancia de Retrofit o similar, si no, aquí la lógica de red:
class TreatmentViewModel(
    private val repository: TreatmentRepository = TreatmentRepository()
) : ViewModel() {

    var uiStateTreatment by mutableStateOf<InterfaceGlobal<List<Treatment>>>(InterfaceGlobal.Idle)
        private set

    fun getTreatments() {
        viewModelScope.launch {
            uiStateTreatment = InterfaceGlobal.Loading
            try {
                val data = repository.getAllTreatments()
                uiStateTreatment = if (data.isEmpty()) InterfaceGlobal.NotFound
                else InterfaceGlobal.Success(data)
            } catch (e: Exception) {
                uiStateTreatment = InterfaceGlobal.Error(e.message ?: "Error desconocido")
            }
        }
    }
}