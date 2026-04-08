package com.example.dynalar_frontend_v1


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dynalar_frontend_v1.interfaces.InterfaceGlobal
import com.example.dynalar_frontend_v1.ui.AppRoutes
import com.example.dynalar_frontend_v1.ui.screens.CalendarPage
import com.example.dynalar_frontend_v1.ui.screens.CreateProfilePage
import com.example.dynalar_frontend_v1.ui.screens.HomePage
import com.example.dynalar_frontend_v1.ui.screens.ListPatientsScreen
import com.example.dynalar_frontend_v1.ui.screens.LoginPage
import com.example.dynalar_frontend_v1.ui.screens.ScheduleAppointmentPage
import com.example.dynalar_frontend_v1.ui.screens.UserProfilePage
import com.example.dynalar_frontend_v1.ui.theme.Dynalar_frontend_v1Theme
import com.example.dynalar_frontend_v1.viewmodel.AppointmentViewModel
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Dynalar_frontend_v1Theme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.Login.route
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
                                navController.navigate(AppRoutes.OdontogramPage.route)
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

                    // --- RUTA CALENDARIO ---
                    composable(AppRoutes.CalendarPage.route) {
                        val appointmentViewModel: AppointmentViewModel = viewModel()

                        LaunchedEffect(Unit) {
                            appointmentViewModel.fetchCalendar()
                        }

                        val calendarState = appointmentViewModel.uiStateCalendar
                        val appointments = if (calendarState is InterfaceGlobal.Success) calendarState.data else emptyList()

                        CalendarPage(
                            appointments = appointments,
                            onNavigateBack = { navController.popBackStack() },
                            onAddAppointmentClick = { date, hour, minute ->
                                navController.navigate("schedule/$date/$hour/$minute")
                            }
                        )
                    }


                    // Dentro de tu NavHost
                    composable(
                        route = "schedule/{date}/{hour}/{minute}",
                        arguments = listOf(
                            navArgument("date") { type = NavType.StringType },
                            navArgument("hour") { type = NavType.IntType },
                            navArgument("minute") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val dateStr = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
                        val hour = backStackEntry.arguments?.getInt("hour") ?: 9
                        val minute = backStackEntry.arguments?.getInt("minute") ?: 0
                        val selectedDate = LocalDate.parse(dateStr)

                        // Obtenemos el ViewModel
                        val appointmentViewModel: AppointmentViewModel = viewModel()

                        ScheduleAppointmentPage(
                            initialDate = selectedDate,
                            initialHour = hour,
                            initialMinute = minute,
                            appointmentViewModel = appointmentViewModel,
                            onBackClick = { navController.popBackStack() },
                            onScheduleClick = { date, h, m, endH, endM, patient, treatment, desc ->
                                // Esta firma coincide con tu @Composable ScheduleAppointmentPage
                                if (patient != null && treatment != null) {
                                    val requestedTime = "${date}T%02d:%02d:00".format(h, m)

                                    appointmentViewModel.autoAssign(
                                        patientId = patient.id!!,
                                        treatmentId = treatment.id!!,
                                        requestedTime = requestedTime
                                        // Nota: si tu backend requiere la hora de fin (endH/endM),
                                        // deberías pasarla aquí también.
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}