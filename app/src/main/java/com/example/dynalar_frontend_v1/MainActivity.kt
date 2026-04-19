package com.example.dynalar_frontend_v1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.dynalar_frontend_v1.ui.screens.EditPatientPage
import com.example.dynalar_frontend_v1.ui.screens.HomePage
import com.example.dynalar_frontend_v1.ui.screens.ListPatientsScreen
import com.example.dynalar_frontend_v1.ui.screens.LoginPage
import com.example.dynalar_frontend_v1.ui.screens.OdontogramPage
import com.example.dynalar_frontend_v1.ui.screens.PatientProfilePage
import com.example.dynalar_frontend_v1.ui.screens.ResumeDateScreen
import com.example.dynalar_frontend_v1.ui.screens.ScheduleAppointmentPage
import com.example.dynalar_frontend_v1.ui.screens.ToothPage
import com.example.dynalar_frontend_v1.ui.screens.UserProfilePage
import com.example.dynalar_frontend_v1.ui.theme.Dynalar_frontend_v1Theme
import com.example.dynalar_frontend_v1.viewmodel.AppointmentViewModel
import com.example.dynalar_frontend_v1.viewmodel.OdontogramViewModel
import com.example.dynalar_frontend_v1.viewmodel.PatientViewModel
import com.example.dynalar_frontend_v1.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Dynalar_frontend_v1Theme {

                val navController = rememberNavController()

                
                val patientViewModel: PatientViewModel = viewModel()
                val appointmentViewModel: AppointmentViewModel = viewModel()
                val odontogramViewModel: OdontogramViewModel = viewModel()
                val userViewModel: UserViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.Home.route
                ) {

                    // PANTALLA DE LOGIN
                    composable(AppRoutes.Login.route) {
                        LoginPage(
                            viewModel = userViewModel,
                            onLoginSuccess = {
                                navController.navigate(AppRoutes.Home.route)
                            },
                        )
                    }

                    // PANTALLA PRINCIPAL
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
                            // --- AÑADE ESTO ---
                            onNavigateToAppointmentDetail = { appointment ->
                                // 1. Guardamos la cita seleccionada en el ViewModel para que ResumeDate la encuentre
                                appointmentViewModel.selectedAppointment = appointment

                                // 2. Navegamos a la ruta de la pantalla de resumen
                                navController.navigate(AppRoutes.ResumeDate.route)
                            }
                        )
                    }

                  
                    composable(AppRoutes.ListPatients.route) {
                        ListPatientsScreen(
                            viewModel = patientViewModel,
                            onNavigateAddPatient = { navController.navigate(AppRoutes.CreateProfile.route) },
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToPatientProfile = { patientId ->
                                navController.navigate(AppRoutes.PatientProfile.createRoute(patientId))
                            }
                        )
                    }

                    
                    composable(
                        route = AppRoutes.PatientProfile.route,
                        arguments = listOf(navArgument("patientId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val patientId = backStackEntry.arguments?.getLong("patientId") ?: -1L

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
                                    val odontogramId = patient.odontogram?.id
                                    if (odontogramId != null) {
                                        navController.navigate(AppRoutes.OdontogramPage.createRoute(odontogramId))
                                    }
                                },
                                
                                onEditClick = { id ->
                                    navController.navigate(AppRoutes.EditPatient.createRoute(id))
                                }
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                   
                    composable(
                        route = AppRoutes.EditPatient.route,
                        arguments = listOf(navArgument("patientId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val patientId = backStackEntry.arguments?.getLong("patientId") ?: -1L

                        LaunchedEffect(patientId) {
                            if (patientViewModel.selectedPatient?.id != patientId) {
                                patientViewModel.getPatientById(patientId)
                            }
                        }

                        val patient = patientViewModel.selectedPatient
                        if (patient != null) {
                            EditPatientPage(
                                patient = patient,
                                patientViewModel = patientViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    // PANTALLA DEL ODONTOGRAMA
                    composable(
                        route = AppRoutes.OdontogramPage.route,
                        arguments = listOf(
                            navArgument("odontogramId") { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        val odontogramId = backStackEntry.arguments?.getLong("odontogramId") ?: 0L

                        OdontogramPage(
                            odontogramId = odontogramId,
                            viewModel = odontogramViewModel,
                            onBack = { navController.popBackStack() },
                            onToothSelected = { toothNumber ->
                                navController.navigate(AppRoutes.ToothPage.createRoute(odontogramId, toothNumber))
                            }
                        )
                    }

                    // PANTALLA DE DIENTE INDIVIDUAL
                    composable(
                        route = AppRoutes.ToothPage.route,
                        arguments = listOf(
                            navArgument("odontogramId") { type = NavType.LongType },
                            navArgument("number") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val odontogramId = backStackEntry.arguments?.getLong("odontogramId") ?: 0L
                        val number = backStackEntry.arguments?.getInt("number") ?: 0

                        ToothPage(
                            number = number,
                            odontogramId = odontogramId,
                            viewModel = odontogramViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // PERFIL DE USUARIO 
                    composable(AppRoutes.UserProfile.route) {
                        UserProfilePage(
                            viewModel = userViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    // CREAR NUEVO PACIENTE
                    composable(AppRoutes.CreateProfile.route) {
                        CreateProfilePage(
                            onNavigateOdontogramaPage = {
                                navController.navigate(AppRoutes.OdontogramPage.route)
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            patientViewModel = patientViewModel
                        )
                    }


                    composable(AppRoutes.CalendarPage.route) {
                        CalendarPage(
                            viewModel = appointmentViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onAddAppointmentClick = { date, hour, minute ->
                                navController.navigate(AppRoutes.ScheduleAppointment.createRoute(date.toString(), hour, minute))
                            },

                            onAppointmentClick = { appointment ->
                                appointmentViewModel.selectedAppointment = appointment 
                                navController.navigate(AppRoutes.ResumeDate.route)  
                            }
                        )
                    }

                    composable(
                        route = AppRoutes.ScheduleAppointment.route,
                        arguments = listOf(
                            navArgument("date") { type = NavType.StringType },
                            navArgument("hour") { type = NavType.IntType },
                            navArgument("minute") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val dateStr = backStackEntry.arguments?.getString("date") ?: ""
                        val hour = backStackEntry.arguments?.getInt("hour") ?: 9
                        val minute = backStackEntry.arguments?.getInt("minute") ?: 0

                        ScheduleAppointmentPage(
                            initialDate = java.time.LocalDate.parse(dateStr),
                            initialHour = hour,
                            initialMinute = minute,
                            patientViewModel = patientViewModel,
                            appointmentViewModel = appointmentViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                  
                    composable(AppRoutes.ResumeDate.route) {
                        
                        val appointment = appointmentViewModel.selectedAppointment

                        if (appointment != null) {
                            ResumeDateScreen(
                                appointment = appointment,
                                onBackClick = { navController.popBackStack() },
                                onPatientClick = { patientId ->
                                   
                                    navController.navigate(AppRoutes.PatientProfile.createRoute(patientId))
                                }
                            )
                        } else {
                          
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }

                }
            }
        }
    }
}