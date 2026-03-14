package com.example.dynalar_frontend_v1.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynalar_frontend_v1.ui.components.BackButton
import com.example.dynalar_frontend_v1.ui.components.Navegate_Button
import com.example.dynalar_frontend_v1.R
import com.example.dynalar_frontend_v1.ui.components.CustomTopBar
import com.example.dynalar_frontend_v1.ui.components.InputFieldEditable


@Composable
fun CreateProfilePage(
    onNavigateOdontogramaPage: () -> Unit,
    onNavigateBack: () -> Unit
) {
    CreateProfileForm(
        onNavigateOdontogramaPage = onNavigateOdontogramaPage,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun CreateProfileForm(
    onNavigateOdontogramaPage: () -> Unit,
    onNavigateBack: () -> Unit
)
{
    var selectedTab by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var medicalNotes by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            // Navegador de pestañas
            Header_ButtonNavigator(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateBack = onNavigateBack,
            )

            Spacer(modifier = Modifier.height(70.dp))


            Box(modifier = Modifier
                .weight(1f)
                .padding(horizontal = 30.dp)) {
                if (selectedTab == 0) {
                    InformationPersonal(
                        name = name, onNameChange = { name = it },
                        lastName = lastName, onLastNameChange = { lastName = it },
                        email = email, onEmailChange = { email = it },
                        dni = dni, onDniChange = { dni = it },
                        phone = phone, onPhoneChange = { phone = it }
                    )
                } else {
                    InformationMedical(
                        allergies = allergies, onAllergiesChange = { allergies = it },
                        medicalNotes = medicalNotes, onMedicalNotesChange = { medicalNotes = it }
                    )
                }
            }
            // Botón Guardar Global
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Navegate_Button(
                    text = "Guarda i Continua",
                    onClick = {
                        if (name.isBlank() || lastName.isBlank() || email.isBlank() || dni.isBlank() || phone.isBlank()) {

                            Toast.makeText(context, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                        } else {
                            onNavigateOdontogramaPage()
                        }
                    },
                )

            }
        }
    }
}

@Composable
fun Header_ButtonNavigator(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit,

) {
    Column{
        CustomTopBar(
            title = "Nou Pacient",
            titleFontSize = 20.sp,
            onNavigateBack = onNavigateBack
        )

        Spacer(modifier = Modifier.height(35.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabButton(
                text = "Informació",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "Historial Clínic",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )
    } }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {

    Button(
        onClick = onClick,
        modifier = modifier.height(45.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF537895) else Color(0xFFE0E0E0),
            contentColor = if (isSelected) Color.White else Color.DarkGray
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun InformationPersonal(
    name: String, onNameChange: (String) -> Unit,
    lastName: String, onLastNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    dni: String, onDniChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit
) {
    
    Column (verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()){
        InputFieldEditable(label = "Nombre",
            value = name,
            onValueChange = onNameChange,
            placeholder = "Nombre")
        InputFieldEditable(label = "Apellidos",
            value = lastName,
            onValueChange = onLastNameChange,
            placeholder = "Apellidos")
        InputFieldEditable(label = "Email",
            value = email,
            onValueChange = onEmailChange,
            placeholder = "correo@ejemplo.com")
        InputFieldEditable(label = "DNI",
            value = dni,
            onValueChange = onDniChange,
            placeholder = "12345678X")
        InputFieldEditable(label = "Teléfono",
            value = phone,
            onValueChange = onPhoneChange,
            placeholder = "600000000")
    }
}

@Composable
fun InformationMedical(
    allergies: String, onAllergiesChange: (String) -> Unit,
    medicalNotes: String, onMedicalNotesChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()) {
        InputFieldEditable(label = "Historial Familiar", value = allergies, onValueChange = onAllergiesChange, placeholder = "Ej: Observacions...")
        InputFieldEditable(label = "Condicions Dentals", value = allergies, onValueChange = onAllergiesChange, placeholder = "Ej: Mal de dents")
        InputFieldEditable(label = "Medicació", value = medicalNotes, onValueChange = onMedicalNotesChange, placeholder = "Paracetamol")
        InputFieldEditable(label = "Alergias", value = allergies, onValueChange = onAllergiesChange, placeholder = "Ej: Penicilina")

    }
}
