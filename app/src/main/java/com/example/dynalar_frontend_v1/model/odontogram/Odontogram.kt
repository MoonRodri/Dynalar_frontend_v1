data class Odontogram(
    val id: Long? = null,
    val creationDate: String? = null,
    val modificationDate: String? = null,
    val odontogramEntries: List<OdontogramEntry>? = emptyList()
)