package app.minimal.fasting.fasting

import kotlinx.serialization.Serializable

@Serializable
data class FastingPrefs(
    // Time to start fasting
    val startingTimeHour: Int = 17,
    val sartingTimeMinute: Int = 0,
    // Fasting goal in hours
    val fastingHours: Int = 16,
    // lunes, martes, miercoles, viernes, sabado, domingo
    val fastingDays: Map<String, Boolean> = mapOf(
        "monday" to true,
        "tuesday" to true,
        "wednesday" to true,
        "thursday" to true,
        "friday" to true,
        "saturday" to true,
        "sunday" to true,
    )
)
