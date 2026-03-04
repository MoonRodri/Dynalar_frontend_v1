data class Dentist(
    override val id: Long? = null,
    override val name: String? = null,
    override val surname: String? = null,
    override val email: String? = null,
    override val role: String? = null,
    val mondayMorning: Boolean? = null,
    val mondayAfternoon: Boolean? = null,
    val tuesdayMorning: Boolean? = null,
    val tuesdayAfternoon: Boolean? = null,
    val wednesdayMorning: Boolean? = null,
    val wednesdayAfternoon: Boolean? = null,
    val thursdayMorning: Boolean? = null,
    val thursdayAfternoon: Boolean? = null,
    val fridayMorning: Boolean? = null,
    val fridayAfternoon: Boolean? = null,
    val treatments: List<Treatment>? = emptyList()
) : User(id, name, surname, email, role)