package com.example.dynalar_frontend_v1.model.patient

data class MedicalRecord(
    val id: Long? = null,
    val allergies: String? = null,
    val medication: String? = null,
    val familyHistory: String? = null,
    val infectiousDecease: String? = null,
    val deceases: String? = null
)