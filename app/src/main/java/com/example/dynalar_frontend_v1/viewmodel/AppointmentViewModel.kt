package com.example.dynalar_frontend_v1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynalar_frontend_v1.interfaces.InterfaceGlobal
import com.example.dynalar_frontend_v1.model.Appointment
import com.example.dynalar_frontend_v1.model.AutoAssignRequest
import com.example.dynalar_frontend_v1.model.SlotRequest
import com.example.dynalar_frontend_v1.network.RetrofitClient
import kotlinx.coroutines.launch
import java.time.LocalDate


class AppointmentViewModel : ViewModel() {

    // --- ESTADOS ---

    // NUEVO: Guarda la fecha seleccionada en el calendario para que no tenga "amnesia"
    var selectedCalendarDate by mutableStateOf(LocalDate.now())

    var selectedAppointment by mutableStateOf<Appointment?>(null)

    // Estado para la lista de citas del calendario
    var uiStateCalendar: InterfaceGlobal<List<Appointment>> by mutableStateOf(InterfaceGlobal.Loading)
        private set

    // Estado para los huecos (slots) disponibles
    var uiStateSlots: InterfaceGlobal<Map<String, List<String>>> by mutableStateOf(InterfaceGlobal.Loading)
        private set

    // Estado para el resultado de la asignación automática (crear cita)
    var uiStateAutoAssign: InterfaceGlobal<Appointment> by mutableStateOf(InterfaceGlobal.Idle)
        private set

    private val apiService = RetrofitClient.appointmentApiService


    /**
     * Obtiene todas las citas para mostrar en el calendario
     */
    fun fetchCalendar() {
        uiStateCalendar = InterfaceGlobal.Loading
        viewModelScope.launch {
            try {
                val response = apiService.getAllAppointments()
                uiStateCalendar = InterfaceGlobal.Success(response)
            } catch (e: Exception) {
                uiStateCalendar =
                    InterfaceGlobal.Error("Error al cargar el calendario: ${e.message}")
            }
        }
    }

    /**
     * Busca huecos disponibles para un tratamiento y rango de fechas
     */
    fun fetchSlots(treatmentId: Long, startDate: LocalDate, endDate: LocalDate) {
        uiStateSlots = InterfaceGlobal.Loading
        viewModelScope.launch {
            try {
                val request = SlotRequest(
                    treatmentId = treatmentId,
                    startDate = startDate.toString(),
                    endDate = endDate.toString()
                )
                val response = apiService.getAvailableSlots(request)

                if (response.isEmpty()) {
                    uiStateSlots = InterfaceGlobal.NotFound
                } else {
                    uiStateSlots = InterfaceGlobal.Success(response)
                }
            } catch (e: Exception) {
                uiStateSlots = InterfaceGlobal.Error("Error al buscar horarios: ${e.message}")
            }
        }
    }

    /**
     * Crea una cita usando la lógica de auto-asignación del backend
     */
    fun autoAssign(patientId: Long, treatmentId: Long, date: LocalDate, hour: Int, minute: Int,reason: String) {
        uiStateAutoAssign = InterfaceGlobal.Loading
        viewModelScope.launch {
            try {
                // Formateamos la fecha a texto con el formato estricto ISO que pide Java: "YYYY-MM-DDTHH:MM:00"
                val requestedTimeStr =
                    String.format("%sT%02d:%02d:00", date.toString(), hour, minute)

                val request = AutoAssignRequest(
                    patientId = patientId,
                    treatmentId = treatmentId,
                    requestedTime = requestedTimeStr,
                    reason = if (reason.isBlank()) "Cap observació" else reason
                )

                val response = apiService.autoAssignAppointment(request)

                if (response.isSuccessful && response.body() != null) {
                    uiStateAutoAssign = InterfaceGlobal.Success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error en la asignación"
                    uiStateAutoAssign = InterfaceGlobal.Error(errorMsg)
                }
            } catch (e: Exception) {
                uiStateAutoAssign = InterfaceGlobal.Error("Fallo de red: ${e.message}")
            }
        }
    }

    /**
     * Limpia el estado de creación para permitir nuevas citas
     */
    fun resetAutoAssignState() {
        uiStateAutoAssign = InterfaceGlobal.Idle
    }


    /**
     * Actualiza una cita existente
     */
    // En AppointmentViewModel.kt

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                val response = apiService.updateAppointment(appointment)

                if (response.isSuccessful && response.body() != null) {
                    // 1. IMPORTANTE: Actualizamos la cita seleccionada con la respuesta del servidor
                    selectedAppointment = response.body()

                    // 2. Refrescamos la lista global para que el calendario esté al día
                    fetchCalendar()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error al actualitzar la cita"
                    uiStateAutoAssign = InterfaceGlobal.Error(errorMsg)
                }
            } catch (e: Exception) {
                uiStateAutoAssign = InterfaceGlobal.Error("Error de xarxa: ${e.message}")
            }
        }
    }

    // Dentro de AppointmentViewModel
    fun updateSelectedDate(newDate: LocalDate) {
        selectedCalendarDate = newDate
    }
    fun deleteAppointment(id: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteAppointment(id)
                if (response.isSuccessful) {
                    fetchCalendar()
                } else {
                    uiStateAutoAssign = InterfaceGlobal.Error("Error al eliminar")
                }
            } catch (e: Exception) {
                uiStateAutoAssign = InterfaceGlobal.Error("Error de xarxa: ${e.message}")
            }
        }
    }
}