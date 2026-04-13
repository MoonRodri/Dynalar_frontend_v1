package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.dynalar_frontend_v1.ui.components.DeleteConfirmationDialog
import com.example.dynalar_frontend_v1.ui.components.ErrorScreenWithImage
import com.example.dynalar_frontend_v1.ui.components.SwipeToDeleteContainer
import com.example.dynalar_frontend_v1.ui.components.getPatientImage
import com.example.dynalar_frontend_v1.ui.theme.ButtonPrimary
import com.example.dynalar_frontend_v1.viewmodel.PatientViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPatientsScreen(
    viewModel: PatientViewModel = viewModel(),
    onNavigateAddPatient: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToPatientProfile: (Long) -> Unit
) {
    val uiState = viewModel.uiStatePatient
    val textFieldState = rememberTextFieldState()

// Estados para controlar el pop-up de confirmación de borrado
    var showDeleteDialog by remember { mutableStateOf(false) }
    var patientToDelete by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(Unit) {
        viewModel.getPatients()
    }

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            PatientsTopBar(
                onNavigateAddPatient = onNavigateAddPatient,
                onNavigateBack = onNavigateBack
            )
            // SearchBar
            SearchPatientBar(textFieldState = textFieldState, viewModel = viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState) {
                is InterfaceGlobal.Loading, InterfaceGlobal.Idle -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ButtonPrimary)
                    }
                }

                is InterfaceGlobal.Success -> {
                    val patients = uiState.data
                        .filter { !it.name.isNullOrBlank() }
                        .groupBy { it.name!!.first().uppercaseChar() }

                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        patients.forEach { (initial, patientList) ->
                            item { CharacterHeader(initial) }

                            items(patientList, key = { it.id ?: 0L }) { patient ->
                                SwipeToDeleteContainer(
                                    onDelete = {
                                        // En lugar de borrar directamente, abrimos el pop-up
                                        patientToDelete = patient.id
                                        showDeleteDialog = true
                                    }
                                ) {
                                    PatientItem(
                                        patient = patient,
                                        onClick = { selectedPatient ->
                                            selectedPatient.id?.let { onNavigateToPatientProfile(it) }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                is InterfaceGlobal.Error -> {
                    ErrorScreenWithImage(
                        message = uiState.message ?: "No s'han pogut carregar els pacients",
                        modifier = Modifier.weight(1f)
                    )
                }

                InterfaceGlobal.NotFound -> {
                    ErrorScreenWithImage(
                        message = "No hi ha pacients registrats.",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    //PopUp Eliminar
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            message = "¿Estàs segur que vols eliminar aquest pacient?",
            onConfirm = {
                // Borramos y cerramos
                patientToDelete?.let { id -> viewModel.deletePatient(id) }
                showDeleteDialog = false
                patientToDelete = null
            },
            onDismiss = {
                // Solo cerramos si cancela
                showDeleteDialog = false
                patientToDelete = null
            }
        )
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
                    textFieldState.edit { replace(0, length, text) }
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

@Composable
fun PatientsTopBar(
    onNavigateBack: () -> Unit,
    onNavigateAddPatient: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        CustomTopBar(
            title = "Llista de Pacients",
            onNavigateBack = onNavigateBack,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        AddPatientButton(
            onClick = onNavigateAddPatient,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 50.dp, top = 25.dp)
        )
    }
}

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
            color = ButtonPrimary
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
            .clickable { onClick(patient) }, // Detecta el clic y ejecuta la acción
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
                    .size(65.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
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