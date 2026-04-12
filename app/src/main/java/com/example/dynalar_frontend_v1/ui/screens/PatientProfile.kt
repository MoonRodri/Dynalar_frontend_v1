package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynalar_frontend_v1.model.patient.Patient
import com.example.dynalar_frontend_v1.ui.components.CustomTopBar
import com.example.dynalar_frontend_v1.ui.components.getPatientImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfilePage(
    patient: Patient,
    onBackClick: () -> Unit,
    onOdontogramClick: () -> Unit,
) {
    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(27.dp))
                CustomTopBar(
                    title = "Perfil del Pacient",
                    onNavigateBack = onBackClick
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 30.dp) // Este margen aplica a la lista, no al TopBar
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                PatientHeaderSection(patient = patient)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                ActionGridSection(patient = patient, onOdontogramClick = onOdontogramClick)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    text = "Citas / Tratamientos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            val appointments = patient.appointments ?: emptyList()

            if (appointments.isEmpty()) {
                item {
                    Text(text = "No hay citas registradas", color = Color.Gray, modifier = Modifier.padding(8.dp))
                }
            } else {
                items(appointments) { appointment ->
                    TreatmentItemRow(
                        title = appointment.toString(),
                        date = "Fecha programada"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

// --- 1. Cabecera Dinámica ---
@Composable
fun PatientHeaderSection(patient: Patient) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = getPatientImage(patient.id)),
            contentDescription = "Foto del paciente",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${patient.name ?: ""} ${patient.lastName ?: ""}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "DNI: ${patient.dni ?: "No registrado"}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "Telf: ${patient.phone ?: "Sin teléfono"}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

// --- 2. Grid de Acciones ---
@Composable
fun ActionGridSection(patient: Patient, onOdontogramClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionCard(
                title = "Odontograma",
                icon = Icons.Default.Face,
                modifier = Modifier.weight(1f),
                onClick = onOdontogramClick
            )
            ActionCard(
                title = "Calendario",
                icon = Icons.Default.DateRange,
                badgeCount = patient.appointments?.size ?: 0,
                modifier = Modifier.weight(1f),
                onClick = { /* TODO */ }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionCard(
                title = "Archivos",
                icon = Icons.Default.Folder,
                badgeCount = patient.documents?.size ?: 0,
                modifier = Modifier.weight(1f),
                onClick = { /* TODO */ }
            )
            ActionCard(
                title = "Historial Clínico",
                icon = Icons.Default.List,
                modifier = Modifier.weight(1f),
                onClick = { /* TODO */ }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(
    title: String,
    icon: ImageVector,
    badgeCount: Int = 0,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            if (badgeCount > 0) {
                Badge(
                    modifier = Modifier.align(Alignment.TopEnd),
                    containerColor = Color.Red
                ) {
                    Text(badgeCount.toString(), color = Color.White)
                }
            }
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF0D47A1),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B)
                )
            }
        }
    }
}

@Composable
fun TreatmentItemRow(title: String, date: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = Color(0xFF0D47A1)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(text = date, color = Color.Gray, fontSize = 14.sp)
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver detalles",
                tint = Color.Gray
            )
        }
    }
}