package com.example.dynalar_frontend_v1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dynalar_frontend_v1.ui.screens.HomePage
import com.example.dynalar_frontend_v1.ui.screens.PatientsScreen
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
                    startDestination = "homePage"
                ) {

                    composable("homePage") {
                        HomePage(
                            onNavigatePatients = {
                                navController.navigate("patientsScreen")
                            }
                        )
                    }

                    composable("homePage") {
                        HomePage(
                            onNavigatePatients = {
                                navController.navigate("patientsScreen")
                            }
                        )
                    }

                    composable("patientsScreen") {
                        PatientsScreen(
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}