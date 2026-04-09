package com.example.dynalar_frontend_v1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dynalar_frontend_v1.ui.AppRoutes
import com.example.dynalar_frontend_v1.ui.screens.CalendarPage
import com.example.dynalar_frontend_v1.ui.screens.CreateProfilePage
import com.example.dynalar_frontend_v1.ui.screens.HomePage
import com.example.dynalar_frontend_v1.ui.screens.ListPatientsScreen
import com.example.dynalar_frontend_v1.ui.screens.LoginPage
import com.example.dynalar_frontend_v1.ui.screens.PatientProfilePage
import com.example.dynalar_frontend_v1.ui.screens.UserProfilePage
import com.example.dynalar_frontend_v1.ui.theme.Dynalar_frontend_v1Theme
import com.example.dynalar_frontend_v1.viewmodel.PatientViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Dynalar_frontend_v1Theme {

                val navController = rememberNavController()
                val patientViewModel: PatientViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.Home.route
                ) {

                    composable(AppRoutes.Login.route) {
                        LoginPage(
                            onLoginSuccess = {
                                navController.navigate(AppRoutes.Home.route)
                            },
                        )
                    }

                    composable(AppRoutes.Home.route) {
                        HomePage(
                            onNavigateListPacient = {
                                navController.navigate(AppRoutes.ListPatients.route)
                            },
                            onNavigateProfileUserProfile = {
                                navController.navigate(AppRoutes.UserProfile.route)
                            },
                            onNavigateBoxCalendar = {
                                navController.navigate(AppRoutes.CalendarPage.route)
                            },
                        )
                    }

                    composable(AppRoutes.ListPatients.route) {
                        ListPatientsScreen(
                            viewModel = patientViewModel,
                            onNavigateAddPatient = { navController.navigate(AppRoutes.CreateProfile.route) },
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToPatientProfile = { patientId ->
                                // Navega usando la ruta con el ID
                                navController.navigate(AppRoutes.PatientProfile.createRoute(patientId))
                            }
                        )
                    }

                    // --- PERFIL DEL PACIENTE (CORREGIDO) ---
                    composable(
                        route = AppRoutes.PatientProfile.route,
                        arguments = listOf(
                            navArgument("patientId") { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        // Extraemos el ID de los argumentos
                        val patientId = backStackEntry.arguments?.getLong("patientId") ?: -1L

                        // Usamos LaunchedEffect para cargar el paciente cuando entramos a esta pantalla
                        LaunchedEffect(patientId) {
                            if (patientId != -1L) {
                                patientViewModel.getPatientById(patientId)
                            }
                        }

                        val patient = patientViewModel.selectedPatient

                        if (patient != null) {
                            PatientProfilePage(
                                patient = patient,
                                onBackClick = { navController.popBackStack() },
                                onOdontogramClick = {
                                    // Aquí podrías querer pasar también el ID al odontograma en el futuro
                                    navController.navigate(AppRoutes.OdontogramPage.route)
                                }
                            )
                        } else {
                            // Mientras carga, mostramos un indicador de progreso
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    composable(AppRoutes.CalendarPage.route) {
                        CalendarPage(
                            onNavigateBack = { navController.popBackStack() },
                            onAddAppointmentClick = { hour, minute ->
                                // Navegación para agendar cita
                            }
                        )
                    }

                    composable(AppRoutes.CreateProfile.route) {
                        CreateProfilePage(
                            onNavigateOdontogramaPage = {
                                navController.navigate(AppRoutes.ListPatients.route)
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(
                        route = "${AppRoutes.UserProfile.route}/{userId}",
                        arguments = listOf(
                            navArgument("userId") { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
                        UserProfilePage(
                            userId = userId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}