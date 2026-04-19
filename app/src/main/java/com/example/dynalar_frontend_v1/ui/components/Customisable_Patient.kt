package com.example.dynalar_frontend_v1.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynalar_frontend_v1.model.patient.Patient
import com.example.dynalar_frontend_v1.ui.theme.ButtonPrimary
import com.example.dynalar_frontend_v1.ui.theme.TextoPrincipal

@Composable
fun PatientHeaderSection(

    patient: Patient) {
    // Extraiem les al·lèrgies del medicalRecord del pacient
    val allergies = patient.medicalRecord?.allergies

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // --- 1. CONTENIDOR DE LA FOTO ---
            Surface(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false,
                        ambientColor = ButtonPrimary.copy(alpha = 0.2f),
                        spotColor = ButtonPrimary.copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFF0F0F0))
            ) {
                Image(
                    painter = painterResource(id = getPatientImage(patient.id ?: 0)),
                    contentDescription = "Foto del pacient",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // --- 2. INFORMACIÓ DEL PACIENT ---
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${patient.name ?: ""} ${patient.lastName ?: ""}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                // Etiqueta de DNI estilizada
                Surface(
                    color = ButtonPrimary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "DNI: ${patient.dni ?: "No registrat"}",
                        fontSize = 13.sp,
                        color = ButtonPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Telèfon
                Text(
                    text = "Telf: ${patient.phone ?: "Sense telèfon"}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
    }
}

@Composable
fun PatientHeaderSectionApp(
    patient: Patient,
    onClick: () -> Unit = {} // <-- 1. AÑADIMOS EL PARÁMETRO AQUÍ
) {
    // Extraemos las alergias del medicalRecord del paciente
    val allergies = patient.medicalRecord?.allergies

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp)) // <-- 2. CLIP PARA EL EFECTO DE CLIC
            .clickable { onClick() },        // <-- 3. HACEMOS QUE LA TARJETA SEA CLICABLE
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // --- 1. CONTENEDOR DE LA FOTO ---
            Surface(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false,
                        ambientColor = ButtonPrimary.copy(alpha = 0.2f),
                        spotColor = ButtonPrimary.copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFF0F0F0))
            ) {
                Image(
                    painter = painterResource(id = getPatientImage(patient.id ?: 0)),
                    contentDescription = "Foto del pacient",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // --- 2. INFORMACIÓN DEL PACIENTE ---
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${patient.name ?: ""} ${patient.lastName ?: ""}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                // Etiqueta de DNI
                Surface(
                    color = ButtonPrimary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "DNI: ${patient.dni ?: "No registrat"}",
                        fontSize = 13.sp,
                        color = ButtonPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Teléfono
                Text(
                    text = "Telf: ${patient.phone ?: "Sense telèfon"}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 2.dp)
                )

                // Alergias
                Text(
                    text = "Alerg: ${allergies ?: "Cap coneguda"}",
                    fontSize = 14.sp,
                    color = if (!allergies.isNullOrEmpty()) Color(0xFFD32F2F) else Color.Gray,
                    modifier = Modifier.padding(start = 2.dp),
                    fontWeight = if (!allergies.isNullOrEmpty()) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}
