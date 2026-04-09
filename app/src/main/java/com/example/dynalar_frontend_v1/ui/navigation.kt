package com.example.dynalar_frontend_v1.ui

sealed class AppRoutes(val route: String) {
    object Login : AppRoutes("loginPage")
    object Home : AppRoutes("homePage")

    object ListPatients : AppRoutes("patientsScreen")
    object CreateProfile : AppRoutes("createProfile")
    object  OdontogramPage : AppRoutes("odontogramPage")

    object UserProfile : AppRoutes("userProfile")

    object CalendarPage : AppRoutes("calendarPage")

    object ScheduleAppointment : AppRoutes("scheduleAppointment")

    object PatientProfile : AppRoutes("patientProfile")

}