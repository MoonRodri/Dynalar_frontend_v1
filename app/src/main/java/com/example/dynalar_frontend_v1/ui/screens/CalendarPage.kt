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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynalar_frontend_v1.R
import com.example.dynalar_frontend_v1.model.Appointment.Appointment
import com.example.dynalar_frontend_v1.ui.components.CustomTopBar
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

val TreatmentColors = listOf(
    Color(0xFF4DB6AC),
    Color(0xFF64B5F6),
    Color(0xFF81C784),
    Color(0xFFFFB74D),
    Color(0xFFBA68C8),
    Color(0xFFFF8A65),
    Color(0xFFF06292),
    Color(0xFF4DD0E1),
)

private const val SLOT_HEIGHT_DP = 80
private const val DAY_START_HOUR = 8
private const val TOTAL_HOURS = 14
private const val TOP_MARGIN_DP = 16

fun colorForTreatment(treatmentId: Long?): Color {
    if (treatmentId == null) return Color(0xFF4DB6AC)
    return TreatmentColors[(treatmentId % TreatmentColors.size).toInt()]
}

fun appointmentHeightDp(appointment: Appointment): Float {
    val durationMinutes = appointment.treatment?.durationMinutes
        ?: run {
            val start = parseTimeToMinutes(appointment.startTime)
            val end = parseTimeToMinutes(appointment.endTime)
            if (start != null && end != null) end - start else 30
        }
    return (durationMinutes * SLOT_HEIGHT_DP / 60f).coerceAtLeast(40f)
}

// ─────────────────────────────────────────────────────────────────────────────
// CalendarPage
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CalendarPage(
    appointments: List<Appointment> = emptyList(),
    onAppointmentClick: (Appointment) -> Unit = {},
    onAddAppointmentClick: (date: LocalDate, hour: Int, minute: Int) -> Unit = { _, _, _ -> },
    onNavigateBack: () -> Unit = {}
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val sharedScrollState = rememberScrollState()

    // Filtramos las citas del día seleccionado
    val appointmentsForDay = remember(appointments, selectedDate) {
        appointments.filter { appointment ->
            // El backend devuelve startTime como "HH:mm:ss", no tiene fecha
            // Comparamos por fecha si el modelo la tiene, si no mostramos todas
            // TODO: cuando el backend incluya fecha en Appointment, filtrar aquí por date
            true
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            CustomTopBar(
                title = "Calendari",
                onNavigateBack = onNavigateBack
            )

            CalendarHeader(
                selectedDate = selectedDate,
                onPrevDay = { selectedDate = selectedDate.minusDays(1) },
                onNextDay = { selectedDate = selectedDate.plusDays(1) },
                onTodayClick = { selectedDate = LocalDate.now() }
            )

            Divider(color = Color(0xFFEEEEEE))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
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

// ─────────────────────────────────────────────────────────────────────────────
// CalendarHeader
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CalendarHeader(
    selectedDate: LocalDate = LocalDate.now(),
    onPrevDay: () -> Unit = {},
    onNextDay: () -> Unit = {},
    onTodayClick: () -> Unit = {}
) {
    val dayName = selectedDate.dayOfWeek
        .getDisplayName(TextStyle.SHORT, Locale("es"))
        .replaceFirstChar { it.uppercase() }
    val dayNum = selectedDate.dayOfMonth
    val monthName = selectedDate.month
        .getDisplayName(TextStyle.SHORT, Locale("es"))
        .replaceFirstChar { it.uppercase() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onTodayClick,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF537895)
            ),
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text(text = "Hoy", fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onPrevDay) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Día anterior")
        }
        IconButton(onClick = onNextDay) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Día siguiente")
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "$dayName $dayNum de $monthName",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// HoursColumn
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HoursColumn() {
    Column(modifier = Modifier.width(58.dp)) {
        Spacer(modifier = Modifier.height(TOP_MARGIN_DP.dp))
        (DAY_START_HOUR until DAY_START_HOUR + TOTAL_HOURS).forEach { hour ->
            Box(
                modifier = Modifier
                    .height(SLOT_HEIGHT_DP.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = "%02d:00".format(hour),
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp, top = 3.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AppointmentsColumn
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AppointmentsColumn(
    appointments: List<Appointment>,
    onAppointmentClick: (Appointment) -> Unit,
    onSlotClick: (hour: Int, minute: Int) -> Unit
) {
    val totalHeightDp = TOTAL_HOURS * SLOT_HEIGHT_DP

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = TOP_MARGIN_DP.dp)
            .height(totalHeightDp.dp)
    ) {
        // Fondo con líneas
        Column(
            modifier = Modifier
                .height(totalHeightDp.dp)
                .fillMaxWidth()
        ) {
            (0 until TOTAL_HOURS).forEach { hourOffset ->
                val hour = DAY_START_HOUR + hourOffset
                Box(
                    modifier = Modifier
                        .height(SLOT_HEIGHT_DP.dp)
                        .fillMaxWidth()
                        .clickable { onSlotClick(hour, 0) }
                        .drawBehind {
                            drawLine(
                                color = Color(0xFFDDDDDD),
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                            val midY = size.height / 2f
                            val nativePaint = android.graphics.Paint().apply {
                                isAntiAlias = true
                                color = android.graphics.Color.parseColor("#E8E8E8")
                                strokeWidth = 1.dp.toPx()
                                pathEffect = android.graphics.DashPathEffect(
                                    floatArrayOf(6.dp.toPx(), 4.dp.toPx()), 0f
                                )
                            }
                            drawContext.canvas.nativeCanvas.drawLine(
                                0f, midY, size.width, midY, nativePaint
                            )
                        }
                )
            }
        }

        // Cards de citas encima
        appointments.forEach { appointment ->
            val startMinutes = parseTimeToMinutes(appointment.startTime)
            if (startMinutes != null) {
                val topOffsetMinutes = startMinutes - (DAY_START_HOUR * 60)
                if (topOffsetMinutes >= 0) {
                    val topDp = topOffsetMinutes * SLOT_HEIGHT_DP / 60f
                    val heightDp = appointmentHeightDp(appointment)
                    val color = colorForTreatment(appointment.treatment?.id)

                    AppointmentCard(
                        appointment = appointment,
                        color = color,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 8.dp)
                            .offset(y = topDp.dp)
                            .height(heightDp.dp)
                            .fillMaxWidth(),
                        onClick = { onAppointmentClick(appointment) }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AppointmentCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AppointmentCard(
    appointment: Appointment,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
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

    val doctorName = appointment.dentist?.name ?: "Doctor"
    val doctorSurname = appointment.dentist?.surname ?: ""

    val treatmentName = appointment.treatment?.name ?: ""
    val durationLabel = appointment.treatment?.durationMinutes?.let {
        when {
            it < 60 -> "${it}min"
            it % 60 == 0 -> "${it / 60}h"
            else -> "${it / 60}h ${it % 60}min"
        }
    } ?: ""

    val patientName = listOfNotNull(
        appointment.patient?.name,
        appointment.patient?.lastName
    ).joinToString(" ")

    // TODO: descomentar cuando el campo allergy esté disponible en Patient
    // val patientAllergy = appointment.patient?.allergy

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(
                width = 3.dp,
                color = color,
                shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)
            )
            .clickable { onClick() }
            .padding(start = 8.dp, top = 4.dp, end = 4.dp, bottom = 4.dp)
    ) {
        Column {
            // ── Hora inicio → fin ─────────────────────────────────────
            Text(
                text = "$startLabel - $endLabel",
                fontSize = 11.sp,
                color = color,
                fontWeight = FontWeight.SemiBold
            )

            // ── Doctor ────────────────────────────────────────────────
            Text(
                text = "Dr/a. $doctorName $doctorSurname",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // ── Tratamiento · duración ────────────────────────────────
            if (treatmentName.isNotBlank()) {
                Text(
                    text = if (durationLabel.isNotBlank()) "$treatmentName · $durationLabel"
                    else treatmentName,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ── Paciente ──────────────────────────────────────────────
            if (patientName.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = patientName,
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ── Alergia ───────────────────────────────────────────────
            // TODO: descomentar cuando allergy esté en Patient y en el backend
            /*
            if (!patientAllergy.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_warning),
                        contentDescription = "Alergia",
                        modifier = Modifier.size(10.dp),
                        tint = Color(0xFFE53935)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = patientAllergy,
                        fontSize = 10.sp,
                        color = Color(0xFFE53935),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            */
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Utils
// ─────────────────────────────────────────────────────────────────────────────

fun parseTimeToMinutes(time: String?): Int? {
    if (time == null) return null
    return try {
        val parts = time.split(":")
        parts[0].toInt() * 60 + parts[1].toInt()
    } catch (e: Exception) { null }
}

fun formatTime(time: String?): String {
    if (time == null) return ""
    return try {
        val parts = time.split(":")
        "%02d:%02d".format(parts[0].toInt(), parts[1].toInt())
    } catch (e: Exception) { time }
}