package com.example.dynalar_frontend_v1


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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