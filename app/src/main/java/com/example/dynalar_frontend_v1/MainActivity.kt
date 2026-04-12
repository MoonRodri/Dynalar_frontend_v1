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
import com.example.dynalar_frontend_v1.ui.screens.PatientProfilePage
import com.example.dynalar_frontend_v1.ui.screens.ScheduleAppointmentPage
import com.example.dynalar_frontend_v1.ui.screens.UserProfilePage
import com.example.dynalar_frontend_v1.ui.theme.Dynalar_frontend_v1Theme
import com.example.dynalar_frontend_v1.viewmodel.AppointmentViewModel
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
                                onOdontogramClick = { navController.navigate(AppRoutes.OdontogramPage.route) }
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
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

                    composable(AppRoutes.CalendarPage.route) {
                        CalendarPage(
                            viewModel = appointmentViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onAddAppointmentClick = { date, hour, minute ->
                                navController.navigate(AppRoutes.ScheduleAppointment.createRoute(date.toString(), hour, minute))
                            },
                            onAppointmentClick = { appointment -> }
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
                }
            }
        }
    }
}