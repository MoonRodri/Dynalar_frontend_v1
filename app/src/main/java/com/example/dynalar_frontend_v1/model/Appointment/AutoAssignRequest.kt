package com.example.dynalar_frontend_v1.model.Appointment

data class AutoAssignRequest(
    val patientId: Long,
    val treatmentId: Long,
    val requestedTime: String // "yyyy-MM-ddTHH:mm:ss"
)
