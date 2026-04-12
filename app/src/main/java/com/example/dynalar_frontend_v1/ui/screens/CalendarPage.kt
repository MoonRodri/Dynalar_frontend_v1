package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynalar_frontend_v1.interfaces.InterfaceGlobal
import com.example.dynalar_frontend_v1.model.Appointment
import com.example.dynalar_frontend_v1.ui.components.CustomTopBar
import com.example.dynalar_frontend_v1.viewmodel.AppointmentViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

// --- CONSTANTES Y COLORES ---
val TreatmentColors = listOf(
    Color(0xFF4DB6AC), Color(0xFF64B5F6), Color(0xFF81C784), Color(0xFFFFB74D),
    Color(0xFFBA68C8), Color(0xFFFF8A65), Color(0xFFF06292), Color(0xFF4DD0E1),
)

private const val SLOT_HEIGHT_DP = 80
private const val DAY_START_HOUR = 8
private const val TOTAL_HOURS = 14
private const val TOP_MARGIN_DP = 16

@Composable
fun CalendarPage(
    viewModel: AppointmentViewModel = viewModel(),
    onAppointmentClick: (Appointment) -> Unit = {},
    onAddAppointmentClick: (LocalDate, Int, Int) -> Unit = { _, _, _ -> },
    onNavigateBack: () -> Unit = {}
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val sharedScrollState = rememberScrollState()

    // Cargar citas al entrar
    LaunchedEffect(Unit) {
        viewModel.fetchCalendar()
    }

    val uiState = viewModel.uiStateCalendar
    val appointmentsForDay = remember(uiState, selectedDate) {
        if (uiState is InterfaceGlobal.Success) {
            uiState.data.filter { appointment ->
                appointment.startTime?.startsWith(selectedDate.toString()) == true
            }
        } else {
            emptyList()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            // Ponemos la TopBar y la cabecera en un fondo blanco sólido para que nada se vea debajo al hacer scroll
            Column(modifier = Modifier.background(Color.White)) {
                CustomTopBar(title = "Calendari", onNavigateBack = onNavigateBack)

                CalendarHeader(
                    selectedDate = selectedDate,
                    onPrevDay = { selectedDate = selectedDate.minusDays(1) },
                    onNextDay = { selectedDate = selectedDate.plusDays(1) },
                    onTodayClick = { selectedDate = LocalDate.now() }
                )
                Divider(color = Color(0xFFEEEEEE))
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            if (uiState is InterfaceGlobal.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF537895)
                )
            }

            // El scroll comienza aquí, y no hay elementos superpuestos con opacidad
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(sharedScrollState)
            ) {
                HoursColumn()
                AppointmentsColumn(
                    appointments = appointmentsForDay,
                    onAppointmentClick = onAppointmentClick,
                    onSlotClick = { hour, minute ->
                        onAddAppointmentClick(selectedDate, hour, minute)
                    }
                )
            }
        }
    }
}

@Composable
fun CalendarHeader(
    selectedDate: LocalDate,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit,
    onTodayClick: () -> Unit
) {
    val dayName = selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es")).replaceFirstChar { it.uppercase() }
    val dayNum = selectedDate.dayOfMonth
    val monthName = selectedDate.month.getDisplayName(TextStyle.SHORT, Locale("es")).replaceFirstChar { it.uppercase() }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onTodayClick,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF537895)),
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text(text = "Hoy", fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onPrevDay) { Icon(Icons.Default.ChevronLeft, "Atrás") }
        IconButton(onClick = onNextDay) { Icon(Icons.Default.ChevronRight, "Siguiente") }
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "$dayName $dayNum de $monthName", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.DarkGray)
    }
}

@Composable
fun HoursColumn() {
    Column(modifier = Modifier.width(58.dp)) {
        Spacer(modifier = Modifier.height(TOP_MARGIN_DP.dp))
        (DAY_START_HOUR until DAY_START_HOUR + TOTAL_HOURS).forEach { hour ->
            Box(modifier = Modifier.height(SLOT_HEIGHT_DP.dp).fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                Text(text = "%02d:00".format(hour), fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(end = 8.dp, top = 3.dp))
            }
        }
    }
}

@Composable
fun AppointmentsColumn(
    appointments: List<Appointment>,
    onAppointmentClick: (Appointment) -> Unit,
    onSlotClick: (hour: Int, minute: Int) -> Unit
) {
    val totalHeightDp = TOTAL_HOURS * SLOT_HEIGHT_DP

    // USAMOS BoxWithConstraints PARA SABER EL ANCHO DE LA PANTALLA
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().padding(top = TOP_MARGIN_DP.dp).height(totalHeightDp.dp)
    ) {
        val columnMaxWidth = maxWidth // Guardamos el ancho disponible

        // 1. DIBUJAMOS EL FONDO Y LAS LÍNEAS HORARIAS
        Column(modifier = Modifier.height(totalHeightDp.dp).fillMaxWidth()) {
            (0 until TOTAL_HOURS).forEach { hourOffset ->
                val hour = DAY_START_HOUR + hourOffset
                Box(
                    modifier = Modifier
                        .height(SLOT_HEIGHT_DP.dp)
                        .fillMaxWidth()
                        .clickable { onSlotClick(hour, 0) }
                        .drawBehind {
                            drawLine(color = Color(0xFFDDDDDD), start = Offset(0f, 0f), end = Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
                            val midY = size.height / 2f
                            val nativePaint = android.graphics.Paint().apply {
                                isAntiAlias = true
                                color = android.graphics.Color.parseColor("#E8E8E8")
                                strokeWidth = 1.dp.toPx()
                                pathEffect = android.graphics.DashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()), 0f)
                            }
                            drawContext.canvas.nativeCanvas.drawLine(0f, midY, size.width, midY, nativePaint)
                        }
                )
            }
        }

        // 2. DIBUJAMOS LAS CITAS (CALCULANDO LOS SOLAPAMIENTOS)
        appointments.forEach { appointment ->
            val startMinutes = parseTimeToMinutes(appointment.startTime)
            val duration = appointment.treatment?.durationMinutes ?: 30

            if (startMinutes != null) {
                val endMinutes = startMinutes + duration

                // --- LÓGICA MÁGICA DE SOLAPAMIENTO ---
                // Buscamos qué otras citas chocan con esta en el tiempo
                val overlappingApps = appointments.filter { other ->
                    val oStart = parseTimeToMinutes(other.startTime) ?: 0
                    val oEnd = oStart + (other.treatment?.durationMinutes ?: 30)
                    // Condición matemática: Inicio1 < Fin2 y Fin1 > Inicio2
                    startMinutes < oEnd && endMinutes > oStart
                }.sortedBy { it.id ?: 0L } // Ordenamos para que siempre salgan en el mismo orden

                val totalColumns = overlappingApps.size
                val columnIndex = overlappingApps.indexOf(appointment).coerceAtLeast(0)

                // Dividimos el ancho de la pantalla entre el número de citas que chocan
                val cardWidth = columnMaxWidth / totalColumns
                val offsetX = cardWidth * columnIndex
                // --------------------------------------

                val topOffsetMinutes = startMinutes - (DAY_START_HOUR * 60)
                if (topOffsetMinutes >= 0) {
                    val topDp = topOffsetMinutes * SLOT_HEIGHT_DP / 60f
                    val heightDp = appointmentHeightDp(appointment)

                    AppointmentCard(
                        appointment = appointment,
                        color = colorForTreatment(appointment.treatment?.id),
                        modifier = Modifier
                            .width(cardWidth) // <-- Ya no es fillMaxWidth, es el ancho calculado
                            .offset(x = offsetX, y = topDp.dp) // <-- La desplazamos a su "columna"
                            .height(heightDp.dp)
                            .padding(end = 2.dp), // Un pequeñísimo margen para que no se peguen
                        onClick = { onAppointmentClick(appointment) }
                    )
                }
            }
        }
    }
}
@Composable
fun AppointmentCard(
    appointment: Appointment,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Cálculo de la hora
    val startLabel = formatTime(appointment.startTime)
    val endLabel = run {
        val startMin = parseTimeToMinutes(appointment.startTime)
        val duration = appointment.treatment?.durationMinutes
        if (startMin != null && duration != null) {
            val endMin = startMin + duration
            "%02d:%02d".format(endMin / 60, endMin % 60)
        } else {
            formatTime(appointment.endTime)
        }
    }

    // Datos a mostrar
    val patientName = "${appointment.patient?.name ?: ""} ${appointment.patient?.lastName ?: ""}".trim()
    val doctorName = "Dr/a. ${appointment.dentist?.surname ?: ""}".trim()
    val treatmentName = appointment.treatment?.name ?: ""

    // Extraer alergias del historial médico (MedicalRecord)
    val allergies = appointment.patient?.medicalRecord?.allergies
    val hasAllergies = !allergies.isNullOrBlank()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(width = 3.dp, color = color, shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
            .clickable { onClick() }
            .padding(start = 8.dp, top = 4.dp, end = 4.dp, bottom = 4.dp)
    ) {
        Column {
            // 1. Hora
            Text(
                text = "$startLabel - $endLabel",
                fontSize = 11.sp,
                color = color,
                fontWeight = FontWeight.SemiBold
            )

            // 2. Nombre del Paciente (Destacado)
            Text(
                text = patientName.ifEmpty { "Pacient Desconegut" },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 3. Alertas Médicas (Alergias en Rojo)
            if (hasAllergies) {
                Text(
                    text = "Al·lèrgies: $allergies",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD32F2F), // Color rojo alerta
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 4. Tratamiento y Doctor asignado
            if (treatmentName.isNotBlank()) {
                Text(
                    text = "$treatmentName | $doctorName",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// --- UTILS ---
fun colorForTreatment(treatmentId: Long?): Color {
    if (treatmentId == null) return Color(0xFF4DB6AC)
    return TreatmentColors[(treatmentId % TreatmentColors.size).toInt()]
}


//Utils
fun appointmentHeightDp(appointment: Appointment): Float {
    val durationMinutes = appointment.treatment?.durationMinutes ?: 30
    return (durationMinutes * SLOT_HEIGHT_DP / 60f).coerceAtLeast(40f)
}

// ESTA FUNCIÓN ES LA CLAVE PARA QUE NO EXPLOTE CON LA "T" DE SPRING BOOT
fun extractTimeOnly(dateTimeString: String?): String? {
    if (dateTimeString == null) return null
    return if (dateTimeString.contains("T")) {
        dateTimeString.split("T").lastOrNull()
    } else {
        dateTimeString.split(" ").lastOrNull()
    }
}

fun parseTimeToMinutes(time: String?): Int? {
    val cleanTime = extractTimeOnly(time) ?: return null
    return try {
        val parts = cleanTime.split(":")
        parts[0].toInt() * 60 + parts[1].toInt()
    } catch (e: Exception) { null }
}

fun formatTime(time: String?): String {
    val cleanTime = extractTimeOnly(time) ?: return ""
    return try {
        val parts = cleanTime.split(":")
        "%02d:%02d".format(parts[0].toInt(), parts[1].toInt())
    } catch (e: Exception) { cleanTime }
}