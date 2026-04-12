package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
// Hemos eliminado el import de rememberScrollState y verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.example.dynalar_frontend_v1.ui.components.CustomisableRectangleButton
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
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPagina)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header_HomePage(onNavigateProfileUserProfile = onNavigateProfileUserProfile)

        // Un espacio moderado y fijo debajo del header
        Spacer(modifier = Modifier.height(50.dp))

        CalendarHomepage(viewModel = viewModel)



        Buttons_HomePage(
            modifier = Modifier.weight(1f),
            onNavigateListPacient = onNavigateListPacient,
            onNavigateBoxCalendar = onNavigateBoxCalendar
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

@Composable
fun CalendarHomepage(viewModel: AppointmentViewModel) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()

    // --- LÓGICA DE CITAS ---
    LaunchedEffect(Unit) {
        viewModel.fetchCalendar()
    }

    // Extraemos las fechas únicas que tienen citas usando InterfaceGlobal
    val uiState = viewModel.uiStateCalendar
    val datesWithAppointments = remember(uiState) {
        if (uiState is InterfaceGlobal.Success) {
            uiState.data.mapNotNull { appointment ->
                // Cortamos por la "T" que manda Spring Boot
                val dateStr = appointment.startTime?.split("T", " ")?.firstOrNull()
                try {
                    if (dateStr != null) LocalDate.parse(dateStr) else null
                } catch (e: Exception) {
                    null
                }
            }.toSet()
        } else {
            emptySet()
        }
    }
    // -----------------------

    val weekDays = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
    )

    val firstDay = currentMonth.atDay(1)
    val offset = firstDay.dayOfWeek.value - 1
    val daysInMonth = currentMonth.lengthOfMonth()

    val monthName = currentMonth.month
        .getDisplayName(TextStyle.FULL, Locale("ca")) // Catalán para coincidir
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = headerText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2C2C2C)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "▾", fontSize = 10.sp, color = Color(0xFF888888))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { currentMonth = currentMonth.minusMonths(1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Mes anterior", tint = Color(0xFF555555))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { currentMonth = currentMonth.plusMonths(1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Mes siguiente", tint = Color(0xFF555555))
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
                                val hasAppointment = datesWithAppointments.contains(cellDate)

                                Box(
                                    modifier = Modifier.size(30.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (hasAppointment) {
                                        // Usa el color ButtonPrimary de tu tema
                                        Surface(
                                            shape = CircleShape,
                                            color = ButtonPrimary,
                                            modifier = Modifier.fillMaxSize()
                                        ) {}
                                    } else if (isToday) {
                                        // Día de hoy sin citas = borde oscuro
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
}

// 3. Modificamos esta función para que acepte un Modifier y distribuya el espacio dinámicamente
@Composable
fun Buttons_HomePage(
    modifier: Modifier = Modifier, // Añadimos el parámetro modifier
    onNavigateListPacient: () -> Unit,
    onNavigateBoxCalendar: () -> Unit
) {
    Column(
        // Le pasamos el modifier que viene desde HomePage (que trae el weight(1f))
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        // Cambiamos 'spacedBy' por 'SpaceEvenly'.
        // Esto separa los botones automáticamente aprovechando todo el espacio disponible.
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        CustomisableRectangleButton(
            title = "Pacients",
            subtitle = "Llista de Pacients",
            circleColor = Color.White,
            onClick = onNavigateListPacient
        )
        CustomisableRectangleButton(
            title = "Agenda",
            subtitle = "Gestiona Agenda",
            circleColor = Color.White,
            onClick = onNavigateBoxCalendar
        )
        CustomisableRectangleButton(
            title = "Materials",
            subtitle = "Materials disponibles",
            circleColor = Color.White,
            onClick = {}
        )
    }
}