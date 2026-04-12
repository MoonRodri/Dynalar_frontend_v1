package com.example.dynalar_frontend_v1.ui.components

import com.example.dynalar_frontend_v1.R

val patientImages = listOf(
    R.drawable.usuario1,
    R.drawable.usuario2,
    R.drawable.usuario3,
    R.drawable.usuario4,
    R.drawable.usuario5,
    R.drawable.usuario6,
    R.drawable.usuario7,
    R.drawable.usuario8,
    R.drawable.usuario9,
    R.drawable.usuario10,
    R.drawable.usuario11,
    R.drawable.usuario12,
)


fun getPatientImage(patientId: Long?): Int {
    if (patientId == null) return R.drawable.usuario1
    val index = (patientId % patientImages.size).toInt()
    return patientImages[index]
}