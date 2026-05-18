package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynalar_frontend_v1.R
import com.example.dynalar_frontend_v1.interfaces.InterfaceGlobal
import com.example.dynalar_frontend_v1.model.Appointment
import com.example.dynalar_frontend_v1.ui.components.CardMenuButton
import com.example.dynalar_frontend_v1.ui.components.DayAppointmentsDialog
import com.example.dynalar_frontend_v1.ui.theme.ButtonPrimary
import com.example.dynalar_frontend_v1.ui.theme.FondoPagina
import com.example.dynalar_frontend_v1.viewmodel.AppointmentViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomePage(
    viewModel: AppointmentViewModel = viewModel(),
    onNavigateProfileUserProfile: () -> Unit,
    onNavigateListPacient: () -> Unit,
    onNavigateBoxCalendar: () -> Unit,
    onNavigateToAppointmentDetail: (Appointment) -> Unit,
    onNavigateBoxMaterials: () -> Unit,
    onNavigateToPatientProfile: (Long) -> Unit
) {
    var selectedDateForDialog by remember { mutableStateOf<LocalDate?>(null) }
    var appointmentsForDialog by remember { mutableStateOf<List<Appointment>>(emptyList()) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPagina)
    ) {
        val screenHeight = maxHeight
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .heightIn(min = screenHeight)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Header_HomePage(onNavigateProfileUserProfile = onNavigateProfileUserProfile)

            Spacer(modifier = Modifier.height(40.dp))

            val uiState = viewModel.uiStateCalendar
            val today = LocalDate.now()
            val nowTime = LocalTime.now()

            var citasHoyCount = 0
            var nextAppointment: Appointment? = null

            if (uiState is InterfaceGlobal.Success) {
                val todayAppointments = uiState.data.filter {
                    it.startTime?.startsWith(today.toString()) == true
                }
                citasHoyCount = todayAppointments.size

                nextAppointment = todayAppointments.minByOrNull { appt ->
                    try {
                        val timeStr = appt.startTime?.replace("T", " ")?.split(" ")?.lastOrNull()?.take(5) ?: "23:59"
                        val parts = timeStr.split(":")
                        val apptMinutes = parts[0].toInt() * 60 + parts[1].toInt()
                        val currentMinutes = nowTime.hour * 60 + nowTime.minute

                        if (apptMinutes >= currentMinutes) {
                            apptMinutes - currentMinutes
                        } else {
                            10000 + (currentMinutes - apptMinutes)
                        }
                    } catch (e: Exception) {
                        99999
                    }
                }
            }

            GreetingSection(citasHoy = citasHoyCount)

            Spacer(modifier = Modifier.height(8.dp))

            CalendarHomepage(
                viewModel = viewModel,
                onDayClick = { date, appointments ->
                    if (appointments.isNotEmpty()) {
                        selectedDateForDialog = date
                        appointmentsForDialog = appointments
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Buttons_HomePage(
                onNavigateListPacient = onNavigateListPacient,
                onNavigateBoxCalendar = onNavigateBoxCalendar,
                onNavigateBoxMaterials = onNavigateBoxMaterials
            )

            Spacer(modifier = Modifier.height(40.dp))

            NextAppointmentSection(
                nextAppointment = nextAppointment,
                onPatientClick = { patientId ->
                    onNavigateToPatientProfile(patientId)
                }
            )
        }
    }

    if (selectedDateForDialog != null) {
        DayAppointmentsDialog(
            date = selectedDateForDialog!!,
            appointments = appointmentsForDialog,
            onDismiss = {
                selectedDateForDialog = null
                appointmentsForDialog = emptyList()
            },
            onAppointmentClick = { appointment ->
                onNavigateToAppointmentDetail(appointment)
            }
        )
    }
}

@Composable
fun GreetingSection(citasHoy: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = if (citasHoy == 1) "Tens 1 cita avui" else "Tens $citasHoy cites avui",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            color = Color.Gray
        )
    }
}

@Composable
fun NextAppointmentSection(
    nextAppointment: Appointment?,
    onPatientClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Pròxima Cita",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50),
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (nextAppointment != null) {
            val timeStr = nextAppointment.startTime?.split("T", " ")?.lastOrNull()?.substring(0, 5) ?: "--:--"
            val patientName = "${nextAppointment.patient?.name ?: "Pacient"} ${nextAppointment.patient?.lastName ?: ""}".trim()
            val treatmentName = nextAppointment.treatment?.name ?: "Sense especificar"

            // Extraer Alergias y Enfermedades Infecciosas
            val allergies = nextAppointment.patient?.medicalRecord?.allergies
            val infectiousDeceases = nextAppointment.patient?.medicalRecord?.infectiousDeceases

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            nextAppointment.patient?.id?.let { patientId ->
                                onPatientClick(patientId)
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(55.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = timeStr,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A5BB2),
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = patientName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = treatmentName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Alertas Médicas en Rojo
                        if (!allergies.isNullOrBlank()) {
                            Text(
                                text = "Al·lèrgies: $allergies",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFF57C00),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (!infectiousDeceases.isNullOrBlank()) {
                            Text(
                                text = "Infeccioses: $infectiousDeceases",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFD32F2F),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Veure perfil",
                        tint = Color(0xFF1A5BB2)
                    )
                }
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "No tens més cites avui",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                        Text(
                            text = "Has completat la teva jornada.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Header_HomePage(onNavigateProfileUserProfile: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable { onNavigateProfileUserProfile() }
                .padding(end = 8.dp, top = 4.dp, bottom = 4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar_color),
                contentDescription = "Perfil de usuario",
                modifier = Modifier.size(45.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("El meu Perfil", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text("Usuari", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
            }
            Icon(Icons.Default.KeyboardArrowDown, "Veure perfil", tint = Color.Gray)
        }
        Image(
            painter = painterResource(id = R.drawable.general_logo),
            contentDescription = "Logo Dynalar",
            modifier = Modifier.size(42.dp),
            alpha = 0.8f
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarHomepage(viewModel: AppointmentViewModel, onDayClick: (LocalDate, List<Appointment>) -> Unit) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.fetchCalendar() }
    val uiState = viewModel.uiStateCalendar

    val appointmentsByDate = remember(uiState) {
        if (uiState is InterfaceGlobal.Success) {
            val map = mutableMapOf<LocalDate, MutableList<Appointment>>()
            uiState.data.forEach { appointment ->
                val dateStr = appointment.startTime?.split("T", " ")?.firstOrNull()
                try {
                    if (dateStr != null) {
                        val date = LocalDate.parse(dateStr)
                        map.getOrPut(date) { mutableListOf() }.add(appointment)
                    }
                } catch (e: Exception) {}
            }
            map
        } else emptyMap()
    }

    val weekDays = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    val firstDay = currentMonth.atDay(1)
    val offset = firstDay.dayOfWeek.value - 1
    val daysInMonth = currentMonth.lengthOfMonth()
    val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("ca")).replaceFirstChar { it.uppercase() }
    val headerText = "$monthName ${currentMonth.year}"

    var offsetX by remember { mutableFloatStateOf(0f) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX > 50) currentMonth = currentMonth.minusMonths(1)
                        else if (offsetX < -50) currentMonth = currentMonth.plusMonths(1)
                        offsetX = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount
                }
            },
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFE8E8E8)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable { showDatePicker = true }.padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(headerText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2C2C2C))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("▾", fontSize = 12.sp, color = Color(0xFF888888))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Mes anterior", tint = Color(0xFF555555))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Mes siguiente", tint = Color(0xFF555555))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDays.forEach { day ->
                    Text(day.getDisplayName(TextStyle.NARROW, Locale.ENGLISH), modifier = Modifier.weight(1f).height(38.dp), textAlign = TextAlign.Center, fontSize = 13.sp, color = Color(0xFF888888))
                }
            }

            val totalCells = offset + daysInMonth
            val rows = (totalCells + 6) / 7
            repeat(rows) { row ->
                Row(modifier = Modifier.fillMaxWidth().height(38.dp)) {
                    repeat(7) { col ->
                        val cellIndex = row * 7 + col
                        val day = cellIndex - offset + 1
                        Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                            if (day in 1..daysInMonth) {
                                val cellDate = currentMonth.atDay(day)
                                val isToday = cellDate == today
                                val dayAppointments = appointmentsByDate[cellDate] ?: emptyList()
                                val hasAppointment = dayAppointments.isNotEmpty()


                                val hasInfectious = dayAppointments.any { appt ->
                                    !appt.patient?.medicalRecord?.infectiousDeceases.isNullOrBlank()
                                }
                                val circleColor = if (hasInfectious) Color(0xFFD32F2F) else ButtonPrimary

                                Box(
                                    modifier = Modifier.size(30.dp).clickable(enabled = hasAppointment) { onDayClick(cellDate, dayAppointments) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (hasAppointment) Surface(shape = CircleShape, color = circleColor, modifier = Modifier.fillMaxSize()) {}
                                    else if (isToday) Surface(shape = CircleShape, color = Color.Transparent, border = BorderStroke(1.5.dp, Color(0xFF2C2C2C)), modifier = Modifier.fillMaxSize()) {}

                                    Text(
                                        day.toString(),
                                        fontSize = 14.sp,
                                        color = if (hasAppointment) Color.White else Color(0xFF2C2C2C),
                                        fontWeight = if (hasAppointment || isToday) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = currentMonth.atDay(1).atStartOfDay(java.time.ZoneId.of("UTC")).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.of("UTC")).toLocalDate()
                        currentMonth = YearMonth.from(selectedDate)
                    }
                    showDatePicker = false
                }) { Text("Acceptar", color = ButtonPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel·lar", color = ButtonPrimary) }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
fun Buttons_HomePage(modifier: Modifier = Modifier, onNavigateListPacient: () -> Unit, onNavigateBoxCalendar: () -> Unit, onNavigateBoxMaterials: () -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CardMenuButton(
                icon = Icons.Default.Person,
                title = "Pacients",
                onClick = onNavigateListPacient,
                modifier = Modifier.weight(1f)
            )
            CardMenuButton(
                icon = Icons.Default.CalendarMonth,
                title = "Agenda",
                onClick = onNavigateBoxCalendar,
                modifier = Modifier.weight(1f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CardMenuButton(
                icon = Icons.Default.Inventory,
                title = "Materials",
                onClick = onNavigateBoxMaterials,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}