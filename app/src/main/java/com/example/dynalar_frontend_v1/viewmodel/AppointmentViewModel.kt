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
import java.time.LocalDateTime


class AppointmentViewModel : ViewModel() {



    var selectedCalendarDate by mutableStateOf(LocalDate.now())

    var selectedAppointment by mutableStateOf<Appointment?>(null)


    var uiStateCalendar: InterfaceGlobal<List<Appointment>> by mutableStateOf(InterfaceGlobal.Loading)
        private set

    var uiStateSlots: InterfaceGlobal<Map<String, List<String>>> by mutableStateOf(InterfaceGlobal.Loading)
        private set


    var uiStateAutoAssign: InterfaceGlobal<Appointment> by mutableStateOf(InterfaceGlobal.Idle)
        private set

    private val apiService = RetrofitClient.appointmentApiService



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


    fun fetchSlots(patientId: Long, treatmentId: Long, startDate: LocalDate, endDate: LocalDate) {
        uiStateSlots = InterfaceGlobal.Loading
        viewModelScope.launch {
            try {
                val request = SlotRequest(
                    patientId = patientId,
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


    fun autoAssign(patientId: Long, treatmentId: Long, date: LocalDate, hour: Int, minute: Int,reason: String) {
        uiStateAutoAssign = InterfaceGlobal.Loading
        viewModelScope.launch {
            try {

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


    fun getNextAppointment(appointments: List<Appointment>): Appointment? {
        val now = LocalDateTime.now()

        return appointments.mapNotNull { appointment ->
            try {
                val timeStr = appointment.startTime ?: return@mapNotNull null


                var cleanStr = timeStr.replace(" ", "T").substringBefore(".")


                if (cleanStr.count { it == ':' } == 1) {
                    cleanStr += ":00"
                }

                val appTime = LocalDateTime.parse(cleanStr)


                if (appTime.isAfter(now)) {
                    Pair(appointment, appTime)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
            .minByOrNull { it.second }
            ?.first
    }


    fun resetAutoAssignState() {
        uiStateAutoAssign = InterfaceGlobal.Idle
    }




    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                val response = apiService.updateAppointment(appointment)

                if (response.isSuccessful && response.body() != null) {

                    selectedAppointment = response.body()


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
