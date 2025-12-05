package com.example.dioramaweather.data.model

/**
 * App internal weather data model
 * Used for widget display
 */
data class WeatherData(
    val city: String,
    val temperature: Int,
    val weather: String,        // "clear", "sunny", "cloudy", etc.
    val timeOfDay: TimeOfDay,
    val icon: String,           // Emoji
    val textColor: String,      // Hex color e.g. "#FFFFFF"
    val shadowColor: String,    // Hex color e.g. "#000000"
    val date: String            // Formatted date e.g. "12月4日"
) {
    /**
     * Get background image resource name
     * Format: {time_of_day}_{weather}
     * e.g. "afternoon_sunny", "night_clear"
     */
    fun getBackgroundImageName(): String {
        return "${timeOfDay.id}_$weather"
    }
}

enum class TimeOfDay(val id: String) {
    MORNING("morning"),      // 6:00 - 11:59
    AFTERNOON("afternoon"),  // 12:00 - 16:59
    EVENING("evening"),      // 17:00 - 19:59
    NIGHT("night");          // 20:00 - 5:59

    companion object {
        fun fromHour(hour: Int): TimeOfDay {
            return when (hour) {
                in 6..11 -> MORNING
                in 12..16 -> AFTERNOON
                in 17..19 -> EVENING
                else -> NIGHT
            }
        }
    }
}

enum class WeatherType(
    val id: String,
    val icon: String
) {
    CLEAR("clear", "\u2600\uFE0F"),           // ☀️
    SUNNY("sunny", "\uD83C\uDF24\uFE0F"),     // 🌤️
    CLOUDY("cloudy", "\u2601\uFE0F"),         // ☁️
    FOG("fog", "\uD83C\uDF2B\uFE0F"),         // 🌫️
    LIGHT_RAIN("light_rain", "\uD83C\uDF27\uFE0F"), // 🌧️
    RAIN("rain", "\uD83C\uDF27\uFE0F"),       // 🌧️
    THUNDERSTORM("thunderstorm", "\u26C8\uFE0F"), // ⛈️
    SNOW("snow", "\uD83C\uDF28\uFE0F"),       // 🌨️
    BLIZZARD("blizzard", "\u2744\uFE0F"),     // ❄️
    SLEET("sleet", "\uD83C\uDF28\uFE0F");     // 🌨️

    companion object {
        fun fromId(id: String): WeatherType {
            return entries.find { it.id == id } ?: CLOUDY
        }
    }
}
