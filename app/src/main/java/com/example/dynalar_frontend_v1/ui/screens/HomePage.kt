package com.example.dynalar_frontend_v1.ui.screens

import android.R.attr.shape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dynalar_frontend_v1.R
import com.example.dynalar_frontend_v1.ui.components.CustomisableRectangleButton


@Composable
fun HomePage(
    onNavigatePatients: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header_HomePage()

        Spacer(modifier = Modifier.height(20.dp))
        CalendarHomepage()

        Spacer(modifier = Modifier.height(20.dp))

        Button_HomePage(
            onGestionarPacientesClick = onNavigatePatients
        )
    }
}

@Composable
fun Header_HomePage() {
    val shape = CircleShape
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp, start = 36.dp, end = 36.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Usuario (derecha)
        Image(
            painter = painterResource(id = R.drawable.usuario_hombre),
            contentDescription = "Perfil de usuario",

            modifier = Modifier.size(50.dp)
                .clip(shape)
                .clickable(onClick = {

                })
        )

        // Logo(Izquierda)
        Image(
            painter = painterResource(id = R.drawable.general_logo),
            contentDescription = "Logo Dynalar",
            modifier = Modifier.size(42.dp),
            alpha = 0.7f
        )
    }
}


//Calendario visual para el homepage
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarHomepage() {
    val datePickerState = rememberDatePickerState()//El calendario

    Surface(
        modifier = Modifier
            .padding(30.dp),
        color = Color.White,
        border = BorderStroke(2.dp, Color(0xFFE5E5E5)),//Trazo
        shape = RoundedCornerShape(20.dp),// Redondear esquinas


    ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                title = null,
                headline = null,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                ),
                dateFormatter = remember {
                    DatePickerDefaults.dateFormatter()
                },
                modifier = Modifier
                    .scale(0.9f)
                    .offset(y = (30).dp)
            )
        }
    }


//Botones HomePage par navegar
@Composable
fun Button_HomePage( onGestionarPacientesClick: () -> Unit){

    CustomisableRectangleButton(
        title = "Afegir Pacient",
        subtitle = "Gestiona pacient",
        circleColor = Color.White,
        onClick = onGestionarPacientesClick
    )
    Spacer(modifier = Modifier.height(35.dp))

    CustomisableRectangleButton(
        title = "Fixar Cita",
        subtitle = "Horari odontolag",
        circleColor = Color.White,
        onClick = {

        }
    )

    Spacer(modifier = Modifier.height(35.dp))


    CustomisableRectangleButton(
        title = "Materials",
        subtitle = "Materials disponibles",
        circleColor = Color.White,
        onClick = {

        }
    )

}

