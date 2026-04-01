package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynalar_frontend_v1.R
import com.example.dynalar_frontend_v1.ui.components.BackButton
import com.example.dynalar_frontend_v1.ui.components.Navegate_Button
import com.example.dynalar_frontend_v1.ui.theme.ButtonPrimary
import com.example.dynalar_frontend_v1.ui.theme.Dynalar_frontend_v1Theme

/**
 * Pantalla para crear una nueva cita.
 *
 * - [onBackClick]: callback para volver a la pantalla anterior.
 * - [onScheduleClick]: callback para confirmar/agendar la cita.
 */
@Composable
fun ScheduleAppointmentPage(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onScheduleClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        // Barra inferior fija con la acción principal de la pantalla.
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                // Boton reutilizado.
                Navegate_Button(
                    text = stringResource(id = R.string.btn_schedule_appointment),
                    onClick = onScheduleClick,
                    backgroundColor = ButtonPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Botón reutilizado.
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.CenterStart) {
                BackButton(
                    onNavigateBack = onBackClick,
                    iconRes = R.drawable.general_volver
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sección visual de fecha/hora seleccionada.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.visita_tiempo),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(16.dp))
                DateChip(text = stringResource(id = R.string.sample_date_view))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 52.dp)
            ) {
                DateChip(text = stringResource(id = R.string.sample_date_start))
                Text(
                    text = " - ",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                DateChip(text = stringResource(id = R.string.sample_date_end))
            }

            Spacer(modifier = Modifier.height(56.dp))

            // Campos tipo selector (actualmente visuales; listos para conectar lógica dinámica).
            AppointmentDropdownField(
                iconRes = R.drawable.visita_paciente,
                label = stringResource(id = R.string.label_afegir_client)
            )
            Spacer(modifier = Modifier.height(24.dp))
            AppointmentDropdownField(
                iconRes = R.drawable.visita_tratamientos,
                label = stringResource(id = R.string.label_afegir_tratament)
            )
            Spacer(modifier = Modifier.height(24.dp))
            AppointmentDropdownField(
                iconRes = R.drawable.visita_descripcion,
                label = stringResource(id = R.string.label_afegir_descripcio)
            )
        }
    }
}

/**
 * Chip de lectura para mostrar una fecha o valor corto de calendario.
 */
@Composable
private fun DateChip(text: String) {
    Surface(
        color = Color(0xFFE0E0E0),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            fontSize = 15.sp,
            color = Color.Black
        )
    }
}

/**
 * Fila con icono + caja de selección.
 *
 * Nota para diana: este componente ahora es de presentación; la interacción real
 * (abrir menú, seleccionar valor, etc.) se puede añadir después.
 */
@Composable
private fun AppointmentDropdownField(
    iconRes: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Icon(
                    painter = painterResource(id = R.drawable.general_flecha_delado),
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(90f),
                    tint = Color.Black
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleAppointmentPagePreview() {
    // Preview para validar rápidamente estructura y estilos.
    Dynalar_frontend_v1Theme {
        ScheduleAppointmentPage()
    }
}
