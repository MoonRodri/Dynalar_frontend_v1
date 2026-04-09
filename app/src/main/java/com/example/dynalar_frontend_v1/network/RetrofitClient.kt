package com.example.dynalar_frontend_v1.network

import com.example.dynalar_frontend_v1.service.AppointmentApiService
import com.example.dynalar_frontend_v1.service.OdontogramApiService
import com.example.dynalar_frontend_v1.service.PatientApiService
import com.example.dynalar_frontend_v1.service.UserApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory // <-- ESTE IMPORT ES VITAL

object RetrofitClient {

    private const val BASE_URL = "http://10.118.5.111:8080/"
    private val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // SI ESTA LÍNEA ESTÁ AQUÍ Y NO ESTÁ EN ROJO, ES IMPOSIBLE QUE SALGA ESE ERROR
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApiService: UserApiService by lazy { instance.create(UserApiService::class.java) }
    val patientApiService: PatientApiService by lazy { instance.create(PatientApiService::class.java) }
    val appointmentApiService: AppointmentApiService by lazy { instance.create(AppointmentApiService::class.java) }
    val odontogramApiService: OdontogramApiService by lazy { instance.create(OdontogramApiService::class.java) }
}