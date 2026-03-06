package com.example.dynalar_frontend_v1.model.odontogram

data class OdontogramEntry(
    val id: Long? = null,
    val tooth: Tooth? = null,
    val surface: ToothSurface? = null,
    val pathology: Pathology? = null,
    val processType: ProcessType? = null
)