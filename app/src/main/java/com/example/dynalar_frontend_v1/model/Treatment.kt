data class Treatment(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val durationMinutes: Int? = null,
    val dentists: List<Dentist>? = emptyList()
)