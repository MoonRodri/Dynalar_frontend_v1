package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynalar_frontend_v1.model.patient.Patient
import com.example.dynalar_frontend_v1.ui.components.CustomTopBar
import com.example.dynalar_frontend_v1.ui.components.Navegate_Button

@Composable
fun DateInformationPage(
    patient: Patient,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            // Apliquem el fons i el marge superior directament a la TopBar
            Column() {
                Spacer(modifier = Modifier.height(27.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    CustomTopBar(
                        title = "Historial Mèdic",
                        onNavigateBack = onBackClick
                    )
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(37.dp))

            // Llista de targetes d'informació
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp) // Una mica més d'espai entre camps
            ) {
                InfoCardReadOnly(
                    label = "Historial Familiar",
                    value = patient.medicalRecord?.familyHistory,
                    icon = Icons.Default.FamilyRestroom,
                    iconColor = Color(0xFF5C6BC0)
                )
                InfoCardReadOnly(
                    label = "Malalties i Condicions",
                    value = patient.medicalRecord?.deceases,
                    icon = Icons.Default.MedicalServices,
                    iconColor = Color(0xFFEF5350)
                )
                InfoCardReadOnly(
                    label = "Medicació",
                    value = patient.medicalRecord?.medication,
                    icon = Icons.Default.Medication,
                    iconColor = Color(0xFF66BB6A)
                )
                InfoCardReadOnly(
                    label = "Al·lèrgies",
                    value = patient.medicalRecord?.allergies,
                    icon = Icons.Default.Warning,
                    iconColor = Color(0xFFFFA726)
                )
                InfoCardReadOnly(
                    label = "Malalties Infeccioses",
                    value = patient.medicalRecord?.infectiousDeceases,
                    icon = Icons.Default.Coronavirus,
                    iconColor = Color(0xFFAB47BC)
                )
            }

            // Espai final per no xocar amb el botó inferior
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun InfoCardReadOnly(
    label: String,
    value: String?,
    icon: ImageVector,
    iconColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Títol amb icona
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }

        // El "quadrat" que imita el disseny dels InputFields però de lectura
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
            shadowElevation = 2.dp
        ) {
            Text(
                text = if (value.isNullOrBlank()) "Sense dades" else value,
                modifier = Modifier.padding(16.dp),
                fontSize = 15.sp,
                color = if (value.isNullOrBlank()) Color.LightGray else Color.Black,
                lineHeight = 20.sp
            )
        }
    }
}