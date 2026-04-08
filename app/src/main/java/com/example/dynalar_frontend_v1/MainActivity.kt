package com.example.dynalar_frontend_v1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
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
import com.example.dynalar_frontend_v1.ui.screens.ScheduleAppointmentPage
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
                            onNavigateAddPatient = { navController.navigate(AppRoutes.CreateProfile.route) },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(AppRoutes.CalendarPage.route) {
                        CalendarPage(                              // ← importa la de screens
                            onNavigateBack = { navController.popBackStack() }
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

                    composable(AppRoutes.PatientProfile.route) {
                        val patient = patientViewModel.selectedPatient

                        if (patient != null) {
                            PatientProfilePage(
                                patient = patient,
                                onBackClick = { navController.popBackStack() },
                                onOdontogramClick = { navController.navigate(AppRoutes.OdontogramPage.route) }
                            )
                        } else {
                            Text("Error: No se ha seleccionado ningún paciente.")
                        }
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
                    composable(AppRoutes.CalendarPage.route) {
                        CalendarPage(
                            onNavigateBack = { navController.popBackStack() },
                            onAddAppointmentClick = { hour, minute ->
                                //Navegacion a la pagina, ponla tu rodrigo, pon el nombre de la pagina, cambialo promero en la carpeta navigation

                            }
                        )
                    }

                    /*composable(
                        route = "${AppRoutes.ScheduleAppointment.route}/{hour}/{minute}",
                        arguments = listOf(
                            navArgument("hour") { type = NavType.IntType },
                            navArgument("minute") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val hour = backStackEntry.arguments?.getInt("hour") ?: 9
                        val minute = backStackEntry.arguments?.getInt("minute") ?: 0

                        ScheduleAppointmentPage(
                            initialHour = hour,
                            initialMinute = minute,
                            onNavigateBack = { navController.popBackStack() },
                            onCitaAgendada = {
                                navController.popBackStack() // Vuelve al calendario tras agendar
                            }
                        )

                    }*/
                }
            }
        }
    }
}