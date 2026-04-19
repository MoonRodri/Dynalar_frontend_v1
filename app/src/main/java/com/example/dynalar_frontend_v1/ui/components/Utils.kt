package com.example.dynalar_frontend_v1.ui.components

import com.example.dynalar_frontend_v1.R
import com.example.dynalar_frontend_v1.model.patient.Sex

// Lista completa de imágenes disponibles
val patientImages = listOf(
    R.drawable.usuario1,  // Índice 0
    R.drawable.usuario2,  // Índice 1
    R.drawable.usuario3,  // Índice 2
    R.drawable.usuario4,  // Índice 3
    R.drawable.usuario5,  // Índice 4
    R.drawable.usuario6,  // Índice 5
    R.drawable.usuario7,  // Índice 6
    R.drawable.usuario8,  // Índice 7
    R.drawable.usuario9,  // Índice 8
    R.drawable.usuario10, // Índice 9
    R.drawable.usuario11, // Índice 10
    R.drawable.usuario12  // Índice 11
)

/**
 * Devuelve una imagen basada en el sexo y el ID del paciente.
 * Mujer: 1, 4, 5, 6, 7, 8, 11
 * Hombre: 2, 3, 9, 10, 12
 * Other: Cualquiera (1-12)
 */
fun getPatientImage(patientId: Long?, sex: Sex?): Int {
    val id = patientId ?: 0L

    return when (sex) {
        Sex.FEMALE -> {
            // Mapeo a recursos: usuario1, 4, 5, 6, 7, 8, 11
            val femaleOptions = listOf(
                R.drawable.usuario1, R.drawable.usuario4, R.drawable.usuario5,
                R.drawable.usuario6, R.drawable.usuario7, R.drawable.usuario8,
                R.drawable.usuario11
            )
            femaleOptions[(id % femaleOptions.size).toInt()]
        }
        Sex.MALE -> {
            // Mapeo a recursos: usuario2, 3, 9, 10, 12
            val maleOptions = listOf(
                R.drawable.usuario2, R.drawable.usuario3, R.drawable.usuario9,
                R.drawable.usuario10, R.drawable.usuario12
            )
            maleOptions[(id % maleOptions.size).toInt()]
        }
        else -> {
            // Sex.OTHER o null: Cualquiera de las 12 imágenes
            patientImages[(id % patientImages.size).toInt()]
        }
    }
}

// Lista completa de códigos de país
val allCountryCodes = listOf(
    "+34", "+1", "+7", "+20", "+27", "+30", "+31", "+32", "+33", "+36", "+39",
    "+40", "+41", "+43", "+44", "+45", "+46", "+47", "+48", "+49", "+51",
    "+52", "+53", "+54", "+55", "+56", "+57", "+58", "+60", "+61", "+62",
    "+63", "+64", "+65", "+66", "+81", "+82", "+84", "+86", "+90", "+91",
    "+92", "+93", "+94", "+95", "+98", "+211", "+212", "+213", "+216",
    "+218", "+220", "+221", "+222", "+223", "+224", "+225", "+226", "+227",
    "+228", "+229", "+230", "+231", "+232", "+233", "+234", "+235", "+236",
    "+237", "+238", "+239", "+240", "+241", "+242", "+243", "+244", "+245",
    "+246", "+248", "+249", "+250", "+251", "+252", "+253", "+254", "+255",
    "+256", "+257", "+258", "+260", "+261", "+262", "+263", "+264", "+265",
    "+266", "+267", "+268", "+269", "+290", "+291", "+297", "+298", "+299",
    "+350", "+351", "+352", "+353", "+354", "+355", "+356", "+357", "+358",
    "+359", "+370", "+371", "+372", "+373", "+374", "+375", "+376", "+377",
    "+378", "+379", "+380", "+381", "+382", "+383", "+385", "+386", "+387",
    "+389", "+420", "+421", "+423", "+500", "+501", "+502", "+503", "+504",
    "+505", "+506", "+507", "+508", "+509", "+590", "+591", "+592", "+593",
    "+594", "+595", "+596", "+597", "+598", "+599", "+670", "+672", "+673",
    "+674", "+675", "+676", "+677", "+678", "+679", "+680", "+681", "+682",
    "+683", "+685", "+686", "+687", "+688", "+689", "+690", "+691", "+692",
    "+850", "+852", "+853", "+855", "+856", "+880", "+886", "+960", "+961",
    "+962", "+963", "+964", "+965", "+966", "+967", "+968", "+970", "+971",
    "+972", "+973", "+974", "+975", "+976", "+977", "+992", "+993", "+994",
    "+995", "+996", "+998"
)