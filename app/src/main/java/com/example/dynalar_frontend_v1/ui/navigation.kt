package com.example.dynalar_frontend_v1.ui

sealed class AppRoutes(val route: String) {
    object Login : AppRoutes("loginPage")
    object Home : AppRoutes("homePage")

    object ListPatients : AppRoutes("patientsScreen")
    object CreateProfile : AppRoutes("createProfile")
    object  OdontogramPage : AppRoutes("odontogramPage")


    object UserProfile : AppRoutes("userProfile/{userId}") {
        fun createRoute(userId: Long) = "userProfile/$userId"
    }

    object CalendarPage : AppRoutes("calendarPage")
    object PatientProfile : AppRoutes("patientProfile")

    object ScheduleAppointment : AppRoutes("scheduleAppointment/{date}/{hour}/{minute}") {

        fun createRoute(date: String, hour: Int, minute: Int): String {
            return "scheduleAppointment/$date/$hour/$minute"
        }
    }
}