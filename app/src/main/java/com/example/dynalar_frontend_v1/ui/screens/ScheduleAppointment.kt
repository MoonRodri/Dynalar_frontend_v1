package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.background
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
    onBackClick: () -> Unit
) {
    // --- ESTADOS PRINCIPALES ---
    var selectedDate by remember { mutableStateOf(initialDate) }
    var hour by remember { mutableIntStateOf(initialHour) }
    var minute by remember { mutableIntStateOf(initialMinute) }

    // Hora fin (calculada dinámicamente)
    var endHour by remember { mutableIntStateOf(initialHour) }
    var endMinute by remember { mutableIntStateOf(initialMinute) }

    // Control de diálogos
    var showCalendar by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
    var selectedTreatment by remember { mutableStateOf<Treatment?>(null) }
    var description by remember { mutableStateOf("") }

    // --- LÓGICA DE TIEMPOS (MARGEN DE 5 MIN) ---
    val margenLimpieza = 5
    val baseDurationPlusMargen = (selectedTreatment?.durationMinutes ?: 0) + margenLimpieza

    // Duración real entre los chips seleccionados
    val realDurationMinutes by remember(hour, minute, endHour, endMinute) {
        derivedStateOf { (endHour * 60 + endMinute) - (hour * 60 + minute) }
    }

    // --- EFECTOS: CARGA Y SINCRONIZACIÓN ---
    LaunchedEffect(Unit) {
        patientViewModel.getPatients()
        treatmentViewModel.getTreatments()
    }

    // Cuando cambia tratamiento o fecha: Pedir slots y ajustar hora fin inicial
    LaunchedEffect(selectedTreatment, selectedDate) {
        selectedTreatment?.id?.let { id ->
            val start = selectedDate.with(java.time.DayOfWeek.MONDAY)
            val end = start.plusDays(6)
            appointmentViewModel.fetchSlots(id, start, end)

            // Auto-ajustar hora fin: Inicio + (Tratamiento + 5min)
            val totalMin = hour * 60 + minute + baseDurationPlusMargen
            endHour = totalMin / 60
            endMinute = totalMin % 60
        }
    }

    // Si el usuario cambia la hora de inicio, arrastramos la de fin manteniendo el margen
    LaunchedEffect(hour, minute) {
        val totalMin = hour * 60 + minute + realDurationMinutes.coerceAtLeast(baseDurationPlusMargen)
        endHour = totalMin / 60
        endMinute = totalMin % 60
    }

    // Navegación al tener éxito
    LaunchedEffect(appointmentViewModel.uiStateAutoAssign) {
        if (appointmentViewModel.uiStateAutoAssign is InterfaceGlobal.Success) {
            appointmentViewModel.resetAutoAssignState()

            appointmentViewModel.fetchCalendar()

            onBackClick()
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Column(modifier = Modifier.padding(24.dp).background(Color.White)) {
                val state = appointmentViewModel.uiStateAutoAssign
                if (state is InterfaceGlobal.Error) {
                    Text(
                        text = state.message ?: "Error al confirmar la cita",
                        color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                val isLoading = state is InterfaceGlobal.Loading
                val canConfirm = selectedPatient != null && selectedTreatment != null && !isLoading

                Navegate_Button(
                    text = if (isLoading) "Assignant..." else "Confirmar Cita",
                    onClick = {
                        if (canConfirm) {
                            appointmentViewModel.autoAssign(
                                patientId = selectedPatient!!.id!!,
                                treatmentId = selectedTreatment!!.id!!,
                                date = selectedDate,
                                hour = hour,
                                minute = minute
                            )
                        }
                    },
                    backgroundColor = if (canConfirm) ButtonPrimary else Color.Gray,
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
            CustomTopBar(title = "Nova Cita", onNavigateBack = onBackClick)

            // Resumen visual del rango horario
            Text(
                text = if (selectedTreatment != null)
                    "%02d:%02d → %02d:%02d".format(hour, minute, endHour, endMinute)
                else "%02d:%02d → --:--".format(hour, minute),
                fontSize = 14.sp,
                color = Color(0xFF90A4AE),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 35.dp, bottom = 20.dp)
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                // ── 1. DÍA Y HORA (EDITABLES) ──
                SectionLabel(icon = R.drawable.visita_tiempo, text = "Día y hora de la visita")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EditableChip(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("EEE, d 'de' MMM", Locale("es"))),
                        onClick = { showCalendar = true }
                    )
                    Spacer(Modifier.width(10.dp))
                    EditableChip(
                        text = "%02d:%02d".format(hour, minute),
                        onClick = { showTimePicker = true }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("→", color = Color(0xFF90A4AE), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    EditableChip(
                        text = if (selectedTreatment != null) "%02d:%02d".format(endHour, endMinute) else "--:--",
                        onClick = { if (selectedTreatment != null) showEndTimePicker = true }
                    )
                }

                // Aviso de margen y tiempo extra
                if (selectedTreatment != null) {
                    Spacer(Modifier.height(6.dp))
                    if (realDurationMinutes > baseDurationPlusMargen) {
                        Text(
                            text = "+${realDurationMinutes - baseDurationPlusMargen} min extra manuales (Total: $realDurationMinutes min)",
                            fontSize = 11.sp, color = Color(0xFFFFB74D)
                        )
                    } else {
                        Text(
                            text = "Inclou 5 min de marge de seguretat",
                            fontSize = 11.sp, color = Color.Gray
                        )
                    }
                }

                // ── 2. PACIENTE ──
                Spacer(Modifier.height(28.dp))
                SectionLabel(icon = R.drawable.visita_paciente, text = "Paciente")
                when (val pState = patientViewModel.uiStatePatient) {
                    is InterfaceGlobal.Success -> {
                        CustomisableDynamicDropdownMenu(
                            selectedItem = selectedPatient,
                            options = pState.data,
                            label = "Seleccionar pacient",
                            displayText = { "${it.name ?: ""} ${it.lastName ?: ""}".trim() },
                            onItemSelected = { selectedPatient = it }
                        )
                    }
                    is InterfaceGlobal.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                    else -> UnavailableChip("No s'han pogut carregar els pacients")
                }

                // ── 3. TRATAMIENTO ──
                Spacer(Modifier.height(24.dp))
                SectionLabel(icon = R.drawable.visita_tratamientos, text = "Tratamiento")
                when (val tState = treatmentViewModel.uiStateTreatment) {
                    is InterfaceGlobal.Success -> {
                        CustomisableDynamicDropdownMenu(
                            selectedItem = selectedTreatment,
                            options = tState.data,
                            label = "Seleccionar tractament",
                            displayText = { it.name ?: "" },
                            onItemSelected = { selectedTreatment = it }
                        )
                    }
                    is InterfaceGlobal.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                    else -> UnavailableChip("No hi ha tractaments disponibles")
                }

                // ── 4. HORARIOS DISPONIBLES (SLOTS) ──
                if (selectedTreatment != null) {
                    Spacer(Modifier.height(28.dp))
                    SectionLabel(icon = R.drawable.visita_tiempo, text = "Horaris Disponibles")
                    when (val slotsState = appointmentViewModel.uiStateSlots) {
                        is InterfaceGlobal.Loading -> CircularProgressIndicator(Modifier.size(24.dp))
                        is InterfaceGlobal.Success -> {
                            val todaySlots = slotsState.data[selectedDate.toString()] ?: emptyList()
                            if (todaySlots.isEmpty()) {
                                UnavailableChip("Sense forats lliures per aquest dia")
                            } else {
                                TimeSlotGrid(todaySlots, hour, minute) { h, m ->
                                    hour = h; minute = m
                                }
                            }
                        }
                        else -> UnavailableChip("Consultant disponibilitat...")
                    }
                }

                // ── 5. NOTAS ──
                Spacer(Modifier.height(28.dp))
                SectionLabel(icon = R.drawable.visita_descripcion, text = "Notas")
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Afegeix una descripció...") },
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

    // ── DIÁLOGOS DE SELECCIÓN ──

    if (showCalendar) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.of("UTC")).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showCalendar = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.of("UTC")).toLocalDate()
                    }
                    showCalendar = false
                }) { Text("Acceptar", color = ButtonPrimary) }
            },
            dismissButton = { TextButton(onClick = { showCalendar = false }) { Text("Cancel·lar") } }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        TimePickerDialogCustom(hour, minute, { showTimePicker = false }) { h, m ->
            hour = h; minute = m; showTimePicker = false
        }
    }

    if (showEndTimePicker) {
        TimePickerDialogCustom(endHour, endMinute, { showEndTimePicker = false }) { h, m ->
            // Validar que el fin sea después del inicio
            if (h * 60 + m > hour * 60 + minute) {
                endHour = h; endMinute = m
            }
            showEndTimePicker = false
        }
    }
}

// ── COMPONENTES AUXILIARES (UI) ──

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
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = text, fontSize = 14.sp, color = ButtonPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun SectionLabel(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 10.dp)) {
        Icon(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.size(20.dp), tint = ButtonPrimary)
        Spacer(Modifier.width(8.dp))
        Text(text = text.uppercase(), fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
    }
}

@Composable
fun UnavailableChip(text: String) {
    Surface(
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = text, fontSize = 14.sp, color = Color.LightGray)
        }
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
    val timeState = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute, is24Hour = true)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(timeState.hour, timeState.minute) }) {
                Text("Acceptar", color = ButtonPrimary)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel·lar") } },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Seleccionar hora", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 20.dp))
                TimePicker(state = timeState)
            }
        }
    )
}