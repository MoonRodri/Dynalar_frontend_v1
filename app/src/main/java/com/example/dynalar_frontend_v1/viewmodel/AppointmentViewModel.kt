package com.example.dynalar_frontend_v1.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynalar_frontend_v1.interfaces.InterfaceGlobal
import com.example.dynalar_frontend_v1.model.Appointment.Appointment
import com.example.dynalar_frontend_v1.model.Appointment.AutoAssignRequest
import com.example.dynalar_frontend_v1.model.Appointment.SlotRequest
import com.example.dynalar_frontend_v1.repository.AppointmentRepository
import kotlinx.coroutines.launch

class AppointmentViewModel(
    private val repository: AppointmentRepository = AppointmentRepository()
) : ViewModel() {

    var uiStateCalendar by mutableStateOf<InterfaceGlobal<List<Appointment>>>(InterfaceGlobal.Idle)
        private set

    var uiStateSlots by mutableStateOf<InterfaceGlobal<Map<String, List<String>>>>(InterfaceGlobal.Idle)
        private set

    var uiStateAutoAssign by mutableStateOf<InterfaceGlobal<Appointment>>(InterfaceGlobal.Idle)
        private set

    fun fetchCalendar() {
        viewModelScope.launch {
            uiStateCalendar = InterfaceGlobal.Loading
            try {
                val data = repository.getAll()
                uiStateCalendar = if (data.isEmpty()) InterfaceGlobal.NotFound
                else InterfaceGlobal.Success(data)
            } catch (e: Exception) {
                uiStateCalendar = InterfaceGlobal.Error(e.message)
            }
        }
    }

    // Ahora sí recibe treatmentId y un rango de fechas
    fun fetchSlots(treatmentId: Long, startDate: String, endDate: String) {
        viewModelScope.launch {
            uiStateSlots = InterfaceGlobal.Loading
            try {
                val data = repository.getSlots(SlotRequest(treatmentId, startDate, endDate))
                uiStateSlots = if (data.isEmpty()) InterfaceGlobal.NotFound
                else InterfaceGlobal.Success(data)
            } catch (e: Exception) {
                uiStateSlots = InterfaceGlobal.Error(e.message)
            }
        }
    }

    fun autoAssign(patientId: Long, treatmentId: Long, requestedTime: String) {
        viewModelScope.launch {
            uiStateAutoAssign = InterfaceGlobal.Loading
            try {
                val response = repository.autoAssign(
                    AutoAssignRequest(
                        patientId,
                        treatmentId,
                        requestedTime
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    uiStateAutoAssign = InterfaceGlobal.Success(response.body()!!)
                } else {
                    uiStateAutoAssign = InterfaceGlobal.Error("Error ${response.code()}: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                uiStateAutoAssign = InterfaceGlobal.Error(e.message)
            }
        }
    }
}