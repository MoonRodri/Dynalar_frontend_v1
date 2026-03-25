package com.example.dynalar_frontend_v1.ui.screens




import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynalar_frontend_v1.R
import com.example.dynalar_frontend_v1.interfaces.InterfaceGlobal
import com.example.dynalar_frontend_v1.model.patient.Patient
import com.example.dynalar_frontend_v1.ui.components.AddButton
import com.example.dynalar_frontend_v1.ui.components.CustomTopBar
import com.example.dynalar_frontend_v1.ui.components.ErrorScreenWithImage
import com.example.dynalar_frontend_v1.ui.components.SwipeToDeleteContainer
import com.example.dynalar_frontend_v1.viewmodel.PatientViewModel

// --- Imagenes de pacientes ---
val patientImages = listOf(
    R.drawable.usuario1,
    R.drawable.usuario2,
    R.drawable.usuario3,
    R.drawable.usuario4,
    R.drawable.usuario5,
    R.drawable.usuario6,
    R.drawable.usuario7,
    R.drawable.usuario8,
    R.drawable.usuario9,
    R.drawable.usuario10,
    R.drawable.usuario11,
    R.drawable.usuario12,
)

fun getPatientImage(patientId: Long?): Int {
    if (patientId == null) return R.drawable.usuario1
    val index = (patientId % patientImages.size).toInt()
    return patientImages[index]
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPatientsScreen(
    viewModel: PatientViewModel = viewModel(),
    onNavigateAddPatient: () -> Unit,
    onNavigateBack: () -> Unit

) {
    val uiState = viewModel.uiStatePatient
    val textFieldState = rememberTextFieldState()

    LaunchedEffect(Unit) {
        viewModel.getPatients() // carga inicial
    }

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)){

            PatientsTopBar(
                onNavigateAddPatient = onNavigateAddPatient,
                onNavigateBack = onNavigateBack
            )
            // SearchBar
            SearchPatientBar(textFieldState = textFieldState, viewModel = viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // --- Contenido principal según estado ---
            when (uiState) {
                is InterfaceGlobal.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                is InterfaceGlobal.Success -> {
                    val patients = uiState.data
                        .filter { !it.name.isNullOrBlank() } // filtra nombres vacíos
                        .sortedBy { it.name } // orden alfabético
                        .groupBy { it.name!!.first().uppercaseChar() } // agrupa por primera letra

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {

                        patients.forEach { (initial, patientList) ->

                            item {
                                CharacterHeader(initial)
                            }

                            items(patientList, key = { it.id ?: 0L }) { patient ->
                                SwipeToDeleteContainer(
                                    onDelete = { viewModel.deletePatient(patient.id ?: 0L) }
                                ) {
                                    PatientItem(
                                        patient = patient,
                                        onClick = { /* navegar al perfil del paciente */ }
                                    )
                                }
                            }
                        }
                    }
                }

                is InterfaceGlobal.Error -> {
                    // Quitamos el filtro 'friendlyMessage' temporalmente
                    // para forzar a que salga el error real en la pantalla
                    ErrorScreenWithImage(
                        message = "ESTO_ES_NUEVO: ${uiState.message}"
                    )
                }

                InterfaceGlobal.NotFound -> {
                    ErrorScreenWithImage(
                        message = "Dades no trobades"
                    )
                }

                InterfaceGlobal.Idle -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPatientBar(
    textFieldState: TextFieldState,
    viewModel: PatientViewModel

) {

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = textFieldState.text.toString(),
                onQueryChange = { text ->
                    // Actualiza el TextField
                    textFieldState.edit { replace(0, length, text) }
                    // Llama al ViewModel para filtrar la lista
                    viewModel.searchPatients(text)
                },
                onSearch = {},
                expanded = false,
                onExpandedChange = {},
                placeholder = { Text("Busca pacients…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
        },
        expanded = false,
        onExpandedChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        content = {}
    )
}
@Composable
fun AddPatientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AddButton(
        onClick = onClick,
        iconRes = R.drawable.person_add,
        modifier = modifier
    )
}


//Encabezado
@Composable
fun PatientsTopBar(
    onNavigateBack: () -> Unit,
    onNavigateAddPatient: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Barra izquierda: siempre respeta sus propios paddings
        CustomTopBar(
            title = "Llista de Pacients",
            onNavigateBack = onNavigateBack,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        // Botón Add: control total de su posición
        AddPatientButton(
            onClick = onNavigateAddPatient,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 50.dp, top = 25.dp) // margen derecho
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
                painter = painterResource(id = getPatientImage(patient.id)),
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
