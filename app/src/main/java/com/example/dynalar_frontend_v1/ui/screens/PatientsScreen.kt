package com.example.dynalar_frontend_v1.ui.screens


import Patient
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynalar_frontend_v1.R
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynalar_frontend_v1.ui.components.Generic_Button
import com.example.dynalar_frontend_v1.ui.components.SwipeToDeleteContainer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientsScreen(
    onBackClick: () -> Unit = {},
    onUserProfile: () -> Unit
) {
    val patients = remember {
        listOf(
            Patient(id = 1, name = "Ana López"),
            Patient(id = 2, name = "Andrés Ruiz"),
            Patient(id = 3, name = "Brenda Gómez"),
            Patient(id = 4, name = "Carlos Pérez"),
            Patient(id = 5, name = "Carla Díaz")
        )
    }

    val textFieldState = rememberTextFieldState()

    val filteredPatients = remember(textFieldState.text) {
        if (textFieldState.text.isBlank()) patients
        else patients.filter {
            it.name?.contains(textFieldState.text, ignoreCase = true) == true
        }
    }

    val groupedPatients = filteredPatients
        .filter { !it.name.isNullOrBlank() }
        .groupBy { it.name!!.first() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {

        item {
            PatientsTopBar(
                onUserProfile = onUserProfile,
                onBackClick = onBackClick,
                backIconRes = R.drawable.general_volver
            )
        }

            item {
                SearchPatientBar(textFieldState)
            }


        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        groupedPatients.forEach { (initial, patientsList) ->

            stickyHeader {
                CharacterHeader(initial)
            }

            items(
                patientsList,
                key = { it.id ?: 0 }
            ) { patient ->

                SwipeToDeleteContainer(
                    onDelete = {
                        patient.id?.let {
                            //viewModel.deletePatient(it)
                        }
                    }
                ) {

                    PatientItem(
                        patient = patient,
                        onClick = {}
                    )
                }
            }
        }
    }
}

//Buscador
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPatientBar(
    textFieldState: TextFieldState
) {

    SearchBar(
        query = textFieldState.text.toString(),
        onQueryChange = {
            textFieldState.edit {
                replace(0, length, it)
            }
        },
        onSearch = {},
        active = false,
        onActiveChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // ⭐ solo horizontal

        placeholder = {
            Text("Buscar pacientes...")
        },

        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        colors = SearchBarDefaults.colors(
            containerColor = Color(0xFFEAF3FF) , //azul muy suave
            dividerColor = Color(0xFFD6E4F5)
        )
    ) {
        //No necesitas contenido expandido porque active = false
    }
}


//Encabezado
@Composable
fun PatientsTopBar(
    onUserProfile: () -> Unit,
    onBackClick: () -> Unit,
    backIconRes: Int
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 50.dp,
                bottom = 6.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row( modifier = Modifier.clickable { onBackClick() },
            verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(id = backIconRes),
                contentDescription = "Volver",
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = "Llista de Pacients",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

        }

        Spacer(modifier = Modifier.width(70.dp))

        Generic_Button(
            text = "+",
            onClick = onUserProfile
        )
    }

}
//Encabezado por letra
@Composable
fun CharacterHeader(initial: Char) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF4F6F9),
        tonalElevation = 1.dp
    ) {

        Text(
            text = initial.toString(),
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF6F7A8A)
        )
    }
}
@Composable
fun PatientItem(
    patient: Patient,
    onClick: (Patient) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp)
            .clickable { onClick(patient) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.usuario_hombre),
                contentDescription = "Pacient",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {


                Text(
                    text = patient.name ?: "",
                    fontSize = 16.sp
                )

            }
        }
    }

}

