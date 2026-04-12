package com.example.dynalar_frontend_v1.service




import com.example.dynalar_frontend_v1.model.Appointment
import com.example.dynalar_frontend_v1.model.AutoAssignRequest
import com.example.dynalar_frontend_v1.model.SlotRequest
import retrofit2.Response
import retrofit2.http.*


interface AppointmentApiService {

    @GET("appointment/index")
    suspend fun getAllAppointments(): List<Appointment>

    @POST("appointment/available-slots")
    suspend fun getAvailableSlots(@Body request: SlotRequest): Map<String, List<String>>

    @POST("appointment/auto-assign")
    suspend fun autoAssignAppointment(@Body request: AutoAssignRequest): Response<Appointment>

    @DELETE("appointment/{id}")
    suspend fun deleteAppointment(@Path("id") id: Long): Response<Unit>
}