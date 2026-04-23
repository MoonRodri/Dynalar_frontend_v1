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
import com.example.dynalar_frontend_v1.ui.screens.HomePage
import com.example.dynalar_frontend_v1.ui.screens.ListPatientsScreen
import com.example.dynalar_frontend_v1.ui.screens.LoginPage
import com.example.dynalar_frontend_v1.ui.screens.MaterialsHome
import com.example.dynalar_frontend_v1.ui.screens.OdontogramPage
import com.example.dynalar_frontend_v1.ui.screens.PatientProfilePage
import com.example.dynalar_frontend_v1.ui.screens.PatientFileUploadPage
import com.example.dynalar_frontend_v1.ui.screens.PatientFilesPage
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

                    composable(AppRoutes.Login.route) {
                        LoginPage(
                            viewModel = userViewModel,
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
                            onNavigateToAppointmentDetail = { appointment ->
                                appointmentViewModel.selectedAppointment = appointment
                                navController.navigate(AppRoutes.ResumeDate.route)
                            },
                            onNavigateBoxMaterials = {
                                navController.navigate(AppRoutes.MaterialsHome.route)
                            }
                        )
                    }

                    composable(AppRoutes.MaterialsHome.route) {
                        MaterialsHome(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateStock = {
                                // TODO: Wire stock route when stock module routes are defined.
                            },
                            onNavigateProtocolo = {
                                // TODO: Wire protocol route when protocol module routes are defined.
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
                            val patientIdForFiles = patient.id ?: -1L
                            val filesCount = patient.documents?.size ?: 0

                            PatientProfilePage(
                                patient = patient,
                                onBackClick = { navController.popBackStack() },
                                onOdontogramClick = {
                                        val odontogramId = patient.odontogram?.id
                                    if (odontogramId != null) {
                                        navController.navigate(AppRoutes.OdontogramPage.createRoute(odontogramId))
                                    }
                                },
                                onFilesClick = {
                                    if (patientIdForFiles != -1L) {
                                        navController.navigate(AppRoutes.PatientFiles.createRoute(patientIdForFiles))
                                    }
                                },
                                filesCount = filesCount
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }


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


                    composable(AppRoutes.UserProfile.route) {
                        UserProfilePage(
                            viewModel = userViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

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

                    composable(
                        route = AppRoutes.PatientFiles.route,
                        arguments = listOf(navArgument("patientId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val patientId = backStackEntry.arguments?.getLong("patientId") ?: -1L

                        PatientFilesPage(
                            patientId = patientId,
                            patientViewModel = patientViewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateUpload = {
                                navController.navigate(AppRoutes.PatientFileUpload.createRoute(patientId))
                            }
                        )
                    }

                    composable(
                        route = AppRoutes.PatientFileUpload.route,
                        arguments = listOf(navArgument("patientId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val patientId = backStackEntry.arguments?.getLong("patientId") ?: -1L

                        PatientFileUploadPage(
                            patientId = patientId,
                            patientViewModel = patientViewModel,
                            onBackClick = { navController.popBackStack() },
                            onUploadDone = {
                                navController.popBackStack()
                            }
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
                }
            }
        }
    }
}