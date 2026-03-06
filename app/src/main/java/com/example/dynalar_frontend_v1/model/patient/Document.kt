package com.example.dynalar_frontend_v1.model.patient

data class Document(
    val id: Long? = null,
    val patientId: Long? = null,
    val documentType: String? = null,
    val documentUrl: String? = null,
    val creationDate: String? = null
)