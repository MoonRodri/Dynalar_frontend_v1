package com.example.dynalar_frontend_v1.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynalar_frontend_v1.model.Appointment
import com.example.dynalar_frontend_v1.ui.theme.ButtonPrimary
import com.example.dynalar_frontend_v1.ui.theme.TextoPrincipal
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayAppointmentsDialog(
    date: LocalDate,
    appointments: List<Appointment>,
    onDismiss: () -> Unit
) {
    val dateStr = date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = "Citas del $dateStr",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextoPrincipal
            )
        },
        text = {
            if (appointments.isEmpty()) {
                Text(text = "No hay citas programadas para este día.", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(appointments) { appointment ->

                        val time = appointment.startTime?.split("T", " ")?.lastOrNull()?.take(5) ?: "--:--"
                        val patientName = "${appointment.patient?.name ?: ""} ${appointment.patient?.lastName ?: ""}".trim()
                        val allergies = appointment.patient?.medicalRecord?.allergies


                        val treatmentName = appointment.treatment?.name

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = time,
                                        fontWeight = FontWeight.Bold,
                                        color = ButtonPrimary
                                    )
                                    Text(
                                        text = patientName.ifEmpty { "Paciente Desconocido" },
                                        fontWeight = FontWeight.Medium,
                                        color = TextoPrincipal
                                    )
                                }


                                if (!treatmentName.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Tractament: $treatmentName",
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                if (!allergies.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Alergias: $allergies",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F) // Rojo para las alergias
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Tancar", color = ButtonPrimary)
            }
        }
    )
}