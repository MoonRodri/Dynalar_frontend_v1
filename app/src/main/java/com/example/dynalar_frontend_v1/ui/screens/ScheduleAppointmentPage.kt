package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynalar_frontend_v1.R
import com.example.dynalar_frontend_v1.interfaces.InterfaceGlobal
import com.example.dynalar_frontend_v1.model.Treatment
import com.example.dynalar_frontend_v1.model.patient.Patient
import com.example.dynalar_frontend_v1.ui.components.CustomTopBar
import com.example.dynalar_frontend_v1.ui.components.CustomisableDynamicDropdownMenu
import com.example.dynalar_frontend_v1.ui.components.Navegate_Button
import com.example.dynalar_frontend_v1.ui.theme.ButtonPrimary
import com.example.dynalar_frontend_v1.viewmodel.AppointmentViewModel
import com.example.dynalar_frontend_v1.viewmodel.PatientViewModel
import com.example.dynalar_frontend_v1.viewmodel.TreatmentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScheduleAppointmentPage(
    initialDate: LocalDate = LocalDate.now(),
    initialHour: Int = 9,
    initialMinute: Int = 0,
    patientViewModel: PatientViewModel = viewModel(),
    treatmentViewModel: TreatmentViewModel = viewModel(),
    appointmentViewModel: AppointmentViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onScheduleClick: (LocalDate, Int, Int, Int, Int, Patient?, Treatment?, String) -> Unit = { _, _, _, _, _, _, _, _ -> }
    //                                              ↑ endHour  ↑ endMinute  para saber la duración real
) {
    // --- ESTADOS ---
    var selectedDate by remember { mutableStateOf(initialDate) }
    var hour by remember { mutableIntStateOf(initialHour) }
    var minute by remember { mutableIntStateOf(initialMinute) }

    // Hora fin — se inicializa cuando se selecciona tratamiento
    var endHour by remember { mutableIntStateOf(initialHour) }
    var endMinute by remember { mutableIntStateOf(initialMinute) }

    var showCalendar by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
    var selectedTreatment by remember { mutableStateOf<Treatment?>(null) }
    var description by remember { mutableStateOf("") }

    // Duración base del tratamiento seleccionado
    val baseDuration = selectedTreatment?.durationMinutes ?: 0

    // Duración real que el usuario puede haber ampliado
    val realDurationMinutes by remember(hour, minute, endHour, endMinute) {
        derivedStateOf { (endHour * 60 + endMinute) - (hour * 60 + minute) }
    }

    // --- CARGA INICIAL ---
    LaunchedEffect(Unit) {
        patientViewModel.getPatients()
        treatmentViewModel.getTreatments()
    }

    // --- Cuando cambia el tratamiento: recalcular hora fin y pedir slots ---
    LaunchedEffect(selectedTreatment) {
        selectedTreatment?.let { treatment ->
            // Recalcular hora fin con la duración base
            val totalMinutes = hour * 60 + minute + (treatment.durationMinutes ?: 0)
            endHour = totalMinutes / 60
            endMinute = totalMinutes % 60

            val start = selectedDate.with(java.time.DayOfWeek.MONDAY)
            val end = start.plusDays(6)
            appointmentViewModel.fetchSlots(
                treatmentId = treatment.id!!,
                startDate = start.toString(),
                endDate = end.toString()
            )
        }
    }

    // --- Cuando cambia la fecha: pedir slots de la nueva semana ---
    LaunchedEffect(selectedDate) {
        selectedTreatment?.id?.let { id ->
            val start = selectedDate.with(java.time.DayOfWeek.MONDAY)
            val end = start.plusDays(6)
            appointmentViewModel.fetchSlots(
                treatmentId = id,
                startDate = start.toString(),
                endDate = end.toString()
            )
        }
    }

    // --- Cuando cambia la hora inicio: ajustar hora fin manteniendo la duración real ---
    LaunchedEffect(hour, minute) {
        val totalMinutes = hour * 60 + minute + realDurationMinutes.coerceAtLeast(baseDuration)
        endHour = totalMinutes / 60
        endMinute = totalMinutes % 60
    }

    //  NAVEGACIÓN final
    LaunchedEffect(appointmentViewModel.uiStateAutoAssign) {
        if (appointmentViewModel.uiStateAutoAssign is InterfaceGlobal.Success) {
            onBackClick()
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                if (appointmentViewModel.uiStateAutoAssign is InterfaceGlobal.Error) {
                    Text(
                        text = (appointmentViewModel.uiStateAutoAssign as InterfaceGlobal.Error).message
                            ?: "Error al confirmar la cita",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                val isLoading = appointmentViewModel.uiStateAutoAssign is InterfaceGlobal.Loading
                Navegate_Button(
                    text = if (isLoading) "Confirmando..." else "Confirmar Cita",
                    onClick = {
                        if (selectedPatient != null && selectedTreatment != null && !isLoading) {
                            onScheduleClick(
                                selectedDate, hour, minute, endHour, endMinute,
                                selectedPatient, selectedTreatment, description
                            )
                        }
                    },
                    backgroundColor = when {
                        isLoading -> Color.Gray
                        selectedPatient != null && selectedTreatment != null -> ButtonPrimary
                        else -> Color.Gray
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── TOP BAR ───────────────────────────────────────────────
            CustomTopBar(
                title = "Nova Cita",
                onNavigateBack = onBackClick
            )

            // ── SUBTÍTULO HORA INICIO → FIN ───────────────────────────
            Text(
                text = if (selectedTreatment != null)
                    "%02d:%02d → %02d:%02d".format(hour, minute, endHour, endMinute)
                else
                    "%02d:%02d → --:--".format(hour, minute),
                fontSize = 14.sp,
                color = Color(0xFF90A4AE),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 35.dp, bottom = 20.dp)
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                // ── FECHA Y HORA ──────────────────────────────────────
                SectionLabel(icon = R.drawable.visita_tiempo, text = "Día y hora de la visita")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Chip fecha
                    EditableChip(
                        text = selectedDate.format(
                            DateTimeFormatter.ofPattern("EEE, d 'de' MMM", Locale("es"))
                        ),
                        onClick = { showCalendar = true }
                    )
                    Spacer(Modifier.width(10.dp))
                    // Chip hora inicio
                    EditableChip(
                        text = "%02d:%02d".format(hour, minute),
                        onClick = { showTimePicker = true }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "→",
                        fontSize = 16.sp,
                        color = Color(0xFF90A4AE),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    // Chip hora fin — editable para añadir tiempo extra
                    EditableChip(
                        text = if (selectedTreatment != null)
                            "%02d:%02d".format(endHour, endMinute)
                        else "--:--",
                        onClick = {
                            if (selectedTreatment != null) showEndTimePicker = true
                        }
                    )
                }

                // Aviso de tiempo extra en naranja
                if (realDurationMinutes > baseDuration && baseDuration > 0) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.visita_tiempo),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFFFFB74D)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "+${realDurationMinutes - baseDuration} min extra · " +
                                    "duración total $realDurationMinutes min",
                            fontSize = 11.sp,
                            color = Color(0xFFFFB74D)
                        )
                    }
                }

                // ── PACIENTE ──────────────────────────────────────────
                Spacer(Modifier.height(28.dp))
                SectionLabel(icon = R.drawable.visita_paciente, text = "Paciente")
                when (val state = patientViewModel.uiStatePatient) {
                    is InterfaceGlobal.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                    is InterfaceGlobal.Success -> {
                        if (state.data.isEmpty()) {
                            UnavailableChip(text = "No hay pacientes disponibles")
                        } else {
                            CustomisableDynamicDropdownMenu(
                                selectedItem = selectedPatient,
                                options = state.data,
                                label = "Añadir cliente",
                                displayText = { "${it.name} ${it.lastName}" },
                                onItemSelected = { selectedPatient = it }
                            )
                        }
                    }
                    is InterfaceGlobal.Error -> UnavailableChip(text = "No disponible")
                    else -> UnavailableChip(text = "No disponible")
                }

                // ── TRATAMIENTO ───────────────────────────────────────
                Spacer(Modifier.height(20.dp))
                SectionLabel(icon = R.drawable.visita_tratamientos, text = "Tratamiento")
                when (val state = treatmentViewModel.uiStateTreatment) {
                    is InterfaceGlobal.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                    is InterfaceGlobal.Success -> {
                        if (state.data.isEmpty()) {
                            UnavailableChip(text = "No hay tratamientos disponibles")
                        } else {
                            CustomisableDynamicDropdownMenu(
                                selectedItem = selectedTreatment,
                                options = state.data,
                                label = "Añadir tratamiento",
                                displayText = { it.name ?: "" },
                                onItemSelected = { selectedTreatment = it }
                            )
                        }
                    }
                    is InterfaceGlobal.Error -> UnavailableChip(text = "No disponible")
                    else -> UnavailableChip(text = "No disponible")
                }

                // ── HORARIOS DISPONIBLES (solo si hay tratamiento) ────
                if (selectedTreatment != null) {
                    Spacer(Modifier.height(28.dp))
                    SectionLabel(icon = R.drawable.visita_tiempo, text = "Horarios disponibles")
                    Text(
                        text = "Huecos libres para \"${selectedTreatment!!.name}\" · " +
                                selectedDate.format(
                                    DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es"))
                                ),
                        fontSize = 12.sp,
                        color = Color(0xFF90A4AE),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    when (val slotsState = appointmentViewModel.uiStateSlots) {
                        is InterfaceGlobal.Loading -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = ButtonPrimary
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = "Buscando doctores disponibles...",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        is InterfaceGlobal.Success -> {
                            val todaySlots = slotsState.data[selectedDate.toString()] ?: emptyList()
                            val morningSlots = todaySlots.filter { it.split(":")[0].toInt() < 14 }
                            val afternoonSlots = todaySlots.filter { it.split(":")[0].toInt() >= 15 }

                            if (todaySlots.isEmpty()) {
                                UnavailableChip(
                                    text = "Sin doctores disponibles este día para este tratamiento"
                                )
                            } else {
                                if (morningSlots.isNotEmpty()) {
                                    Text(
                                        text = "Mañana",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                    TimeSlotGrid(
                                        slots = morningSlots,
                                        selH = hour,
                                        selM = minute,
                                        onSelect = { h, m -> hour = h; minute = m }
                                    )
                                    Spacer(Modifier.height(12.dp))
                                }
                                if (afternoonSlots.isNotEmpty()) {
                                    Text(
                                        text = "Tarde",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                    TimeSlotGrid(
                                        slots = afternoonSlots,
                                        selH = hour,
                                        selM = minute,
                                        onSelect = { h, m -> hour = h; minute = m }
                                    )
                                }
                            }
                        }
                        is InterfaceGlobal.NotFound -> UnavailableChip(
                            text = "Sin doctores disponibles este día para este tratamiento"
                        )
                        is InterfaceGlobal.Error -> UnavailableChip(
                            text = "No se pudo consultar la disponibilidad"
                        )
                        else -> {}
                    }
                }

                // ── NOTAS ─────────────────────────────────────────────
                Spacer(Modifier.height(28.dp))
                SectionLabel(icon = R.drawable.visita_descripcion, text = "Notas")
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Añade una descripción...") },
                    modifier = Modifier.fillMaxWidth().height(110.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF9F9F9),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
                Spacer(Modifier.height(100.dp))
            }
        }
    }

    // ── DIÁLOGO: MINI CALENDARIO ──────────────────────────────────────
    if (showCalendar) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(java.time.ZoneId.of("UTC"))
                .toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showCalendar = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = java.time.Instant.ofEpochMilli(it)
                            .atZone(java.time.ZoneId.of("UTC")).toLocalDate()
                    }
                    showCalendar = false
                }) { Text("Aceptar", color = ButtonPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showCalendar = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // ── DIÁLOGO: HORA INICIO ──────────────────────────────────────────
    if (showTimePicker) {
        TimePickerDialogCustom(
            initialHour = hour,
            initialMinute = minute,
            onDismiss = { showTimePicker = false },
            onConfirm = { h, m ->
                hour = h
                minute = m
                showTimePicker = false
            }
        )
    }

    // ── DIÁLOGO: HORA FIN (tiempo extra) ─────────────────────────────
    if (showEndTimePicker) {
        TimePickerDialogCustom(
            initialHour = endHour,
            initialMinute = endMinute,
            onDismiss = { showEndTimePicker = false },
            onConfirm = { h, m ->
                // Solo aceptar si la hora fin es posterior a la inicio
                if (h * 60 + m > hour * 60 + minute) {
                    endHour = h
                    endMinute = m
                }
                showEndTimePicker = false
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTES AUXILIARES
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeSlotGrid(slots: List<String>, selH: Int, selM: Int, onSelect: (Int, Int) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        slots.forEach { time ->
            val parts = time.split(":")
            val h = parts[0].toInt()
            val m = parts[1].toInt()
            val isSelected = selH == h && selM == m
            Surface(
                onClick = { onSelect(h, m) },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) ButtonPrimary else Color(0xFFF0F4F8),
                modifier = Modifier.width(74.dp)
            ) {
                Text(
                    text = time,
                    modifier = Modifier.padding(vertical = 10.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else Color(0xFF455A64)
                )
            }
        }
    }
}

@Composable
fun EditableChip(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color(0xFFE8EEF1),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.height(44.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 18.dp)
        ) {
            Text(text = text, fontSize = 14.sp, color = ButtonPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun UnavailableChip(text: String) {
    Surface(
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(text = text, fontSize = 14.sp, color = Color(0xFFAAAAAA))
        }
    }
}

@Composable
fun SectionLabel(icon: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = ButtonPrimary
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text.uppercase(),
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogCustom(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timeState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(timeState.hour, timeState.minute) }) {
                Text("Aceptar", color = ButtonPrimary)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Seleccionar hora",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                TimePicker(state = timeState)
            }
        }
    )
}