package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynalar_frontend_v1.R
import com.example.dynalar_frontend_v1.interfaces.InterfaceGlobal
import com.example.dynalar_frontend_v1.model.Appointment
import com.example.dynalar_frontend_v1.ui.components.CustomisableRectangleButton
import com.example.dynalar_frontend_v1.ui.components.DayAppointmentsDialog
import com.example.dynalar_frontend_v1.ui.theme.ButtonPrimary
import com.example.dynalar_frontend_v1.ui.theme.FondoPagina
import com.example.dynalar_frontend_v1.viewmodel.AppointmentViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale



@Composable
fun HomePage(
    viewModel: AppointmentViewModel = viewModel(),
    onNavigateProfileUserProfile: () -> Unit,
    onNavigateListPacient: () -> Unit,
    onNavigateBoxCalendar: () -> Unit,
    onNavigateToAppointmentDetail: (Appointment) -> Unit
) {
    //Pop up
    var selectedDateForDialog by remember { mutableStateOf<LocalDate?>(null) }
    var appointmentsForDialog by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPagina)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Header_HomePage(onNavigateProfileUserProfile = onNavigateProfileUserProfile)

        Spacer(modifier = Modifier.height(50.dp))

        CalendarHomepage(
            viewModel = viewModel,
            // Pasamos una función para manejar el clic en un día
            onDayClick = { date, appointments ->
                if (appointments.isNotEmpty()) {
                    selectedDateForDialog = date
                    appointmentsForDialog = appointments
                }
            }
        )


        Buttons_HomePage(
            modifier = Modifier.weight(1f),
            onNavigateListPacient = onNavigateListPacient,
            onNavigateBoxCalendar = onNavigateBoxCalendar
        )
    }
    if (selectedDateForDialog != null) {
        DayAppointmentsDialog(
            date = selectedDateForDialog!!,
            appointments = appointmentsForDialog,
            onDismiss = {
                selectedDateForDialog = null
                appointmentsForDialog = emptyList()
            },
            // PASAMOS LA FUNCIÓN DE NAVEGACIÓN AQUÍ:
            onAppointmentClick = { appointment ->
                onNavigateToAppointmentDetail(appointment)
            }
        )
    }
}


@Composable
fun Header_HomePage(onNavigateProfileUserProfile: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.avatar_color),
            contentDescription = "Perfil de usuario",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .clickable { onNavigateProfileUserProfile() }
        )
        Image(
            painter = painterResource(id = R.drawable.general_logo),
            contentDescription = "Logo Dynalar",
            modifier = Modifier.size(42.dp),
            alpha = 0.7f
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarHomepage(
    viewModel: AppointmentViewModel,
    onDayClick: (LocalDate, List<Appointment>) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()

    // --- ESTADO PARA EL SELECTOR DE FECHA MANUAL ---
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchCalendar()
    }

    val uiState = viewModel.uiStateCalendar

    // Mapeamos las citas por fecha para acceso rápido
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
                } catch (e: Exception) {
                    // Ignorar error de parseo
                }
            }
            map
        } else {
            emptyMap()
        }
    }

    val weekDays = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
    )

    val firstDay = currentMonth.atDay(1)
    val offset = firstDay.dayOfWeek.value - 1
    val daysInMonth = currentMonth.lengthOfMonth()

    val monthName = currentMonth.month
        .getDisplayName(TextStyle.FULL, Locale("ca"))
        .take(3)
        .replaceFirstChar { it.uppercase() }

    val headerText = "$monthName ${currentMonth.year}"

    val rowHeight = 38.dp
    val dayFontSize = 14.sp
    var offsetX by remember { mutableStateOf(0f) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX > 50) {
                            currentMonth = currentMonth.minusMonths(1)
                        } else if (offsetX < -50) {
                            currentMonth = currentMonth.plusMonths(1)
                        }
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- CABECERA CLICKABLE ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { showDatePicker = true } // Abre el calendario manual
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = headerText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2C2C2C)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "▾", fontSize = 12.sp, color = Color(0xFF888888))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { currentMonth = currentMonth.minusMonths(1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            "Mes anterior",
                            tint = Color(0xFF555555)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { currentMonth = currentMonth.plusMonths(1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            "Mes siguiente",
                            tint = Color(0xFF555555)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                weekDays.forEach { day ->
                    val label = day.getDisplayName(TextStyle.NARROW, Locale.ENGLISH)
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f).height(rowHeight),
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = Color(0xFF888888)
                    )
                }
            }

            val totalCells = offset + daysInMonth
            val rows = (totalCells + 6) / 7

            repeat(rows) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().height(rowHeight)
                ) {
                    repeat(7) { col ->
                        val cellIndex = row * 7 + col
                        val day = cellIndex - offset + 1

                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day in 1..daysInMonth) {
                                val cellDate = currentMonth.atDay(day)
                                val isToday = cellDate == today

                                val dayAppointments = appointmentsByDate[cellDate] ?: emptyList()
                                val hasAppointment = dayAppointments.isNotEmpty()

                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clickable(enabled = hasAppointment) {
                                            onDayClick(cellDate, dayAppointments)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (hasAppointment) {
                                        Surface(
                                            shape = CircleShape,
                                            color = ButtonPrimary,
                                            modifier = Modifier.fillMaxSize()
                                        ) {}
                                    } else if (isToday) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color.Transparent,
                                            border = BorderStroke(1.5.dp, Color(0xFF2C2C2C)),
                                            modifier = Modifier.fillMaxSize()
                                        ) {}
                                    }

                                    Text(
                                        text = day.toString(),
                                        fontSize = dayFontSize,
                                        color = if (hasAppointment) Color.White else Color(
                                            0xFF2C2C2C
                                        ),
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

    //Calendario para cambiar dia manualmente
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = currentMonth.atDay(1)
                .atStartOfDay(java.time.ZoneId.of("UTC")).toInstant().toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Usamos la ruta completa java.time... para evitar conflictos
                        val selectedDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.of("UTC")).toLocalDate()
                        currentMonth = YearMonth.from(selectedDate)
                    }
                    showDatePicker = false
                }) {
                    Text("Acceptar", color = ButtonPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel·lar", color = ButtonPrimary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}



@Composable
fun Buttons_HomePage(
    modifier: Modifier = Modifier,
    onNavigateListPacient: () -> Unit,
    onNavigateBoxCalendar: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        CustomisableRectangleButton(
            title = "Pacients",
            icon = Icons.Default.Person,
            subtitle = "Llista de Pacients",
            circleColor = Color.White,
            onClick = onNavigateListPacient
        )
        CustomisableRectangleButton(
            title = "Agenda",
            icon = Icons.Default.CalendarMonth,
            subtitle = "Gestiona Agenda",
            circleColor = Color.White,
            onClick = onNavigateBoxCalendar
        )
        CustomisableRectangleButton(
            title = "Materials",
            icon = Icons.Default.Inventory,
            subtitle = "Materials disponibles",
            circleColor = Color.White,
            onClick = {}
        )
    }
}