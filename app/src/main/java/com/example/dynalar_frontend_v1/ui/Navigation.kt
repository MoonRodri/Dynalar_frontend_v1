package com.example.dynalar_frontend_v1.ui

sealed class AppRoutes(val route: String) {
    object Login : AppRoutes("loginPage")
    object Home : AppRoutes("homePage")

    object ListPatients : AppRoutes("patientsScreen")
    object CreateProfile : AppRoutes("createProfile")
    object OdontogramPage : AppRoutes("odontogramPage/{odontogramId}") {
        fun createRoute(odontogramId: Long) = "odontogramPage/$odontogramId"
    }

    object ToothPage: AppRoutes("toothPage/{odontogramId}/{number}") {
        fun createRoute(odontogramId: Long, number: Int) = "toothPage/$odontogramId/$number"
    }

    object UserProfile : AppRoutes("userProfile")
    object CalendarPage : AppRoutes("calendarPage")
    object PatientProfile : AppRoutes("patientProfile/{patientId}") {
        fun createRoute(patientId: Long) = "patientProfile/$patientId"
    }

    object ScheduleAppointment : AppRoutes("scheduleAppointment/{date}/{hour}/{minute}") {
        fun createRoute(date: String, hour: Int, minute: Int): String {
            return "scheduleAppointment/$date/$hour/$minute"
        }
    }
    object ResumeDate : AppRoutes("resumeDate")
}