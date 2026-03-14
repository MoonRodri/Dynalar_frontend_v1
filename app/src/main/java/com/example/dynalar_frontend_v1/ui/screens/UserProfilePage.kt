package com.example.dynalar_frontend_v1.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynalar_frontend_v1.ui.components.BannerGenericProfile
import com.example.dynalar_frontend_v1.ui.components.InputField
import com.example.dynalar_frontend_v1.ui.theme.ButtonPrimary
import com.example.dynalar_frontend_v1.ui.theme.Dynalar_frontend_v1Theme
import com.example.dynalar_frontend_v1.R
import com.example.dynalar_frontend_v1.model.LoginUiState
import com.example.dynalar_frontend_v1.model.user.User
import com.example.dynalar_frontend_v1.viewmodel.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfilePage(
    userId: Long = 0L,
    viewModel: UserViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.userUiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.getUserById(userId)
    }

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            // Banner siempre arriba
            val bannerUserName = if (uiState is LoginUiState.Success) (uiState as LoginUiState.Success).user.name ?: "Usuario" else "Usuario"
            val bannerUserRole = if (uiState is LoginUiState.Success) (uiState as LoginUiState.Success).user.role ?: "Usuario" else "Usuario"

            BannerGenericProfile(
                userName = bannerUserName,
                userRole = bannerUserRole,
                profileImage = {
                    Image(
                        painter = painterResource(R.drawable.avatar_color),
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                },
                onNavigateBack = onNavigateBack,
                content = {} // contenido principal se manejará más abajo
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Contenido principal según estado ---
            when (uiState) {
                is LoginUiState.Loading, LoginUiState.Idle -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LoginUiState.Success -> {
                    val user = (uiState as LoginUiState.Success).user
                    UserInfoContent(user)
                }

                is LoginUiState.Error -> {
                    val message = (uiState as LoginUiState.Error).message ?: "Error desconocido"
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun UserInfoContent(user: User) {
    var countryCode by remember { mutableStateOf("+34") }


    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        InputField(label = "Nombre", value = user.name ?: "")
        InputField(label = "Apellido", value = user.surname ?: "")
        InputField(label = "DNI", value = user.password ?: "")
        InputField(label = "Email", value = user.email ?: "")

        Row(horizontalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .padding(top = 22.dp, end = 5.dp)
                    .height(48.dp)
                    .width(75.dp)
                    .background(ButtonPrimary, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = countryCode,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

           /* InputField(
                label = "Teléfono",
                value = phoneNumber
            )*/

        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfilePagePreview() {
    Dynalar_frontend_v1Theme {
        UserProfilePage()
    }
}