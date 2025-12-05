package com.example.dioramaweather.data.api

import com.example.dioramaweather.data.model.TimeOfDay
import com.example.dioramaweather.data.model.WeatherType

/**
 * Maps OpenWeatherMap weather codes to app's weather types
 * https://openweathermap.org/weather-conditions
 */
object WeatherCodeMapper {

    /**
     * Map OpenWeatherMap weather code to internal weather type
     */
    fun mapWeatherCode(code: Int): WeatherType {
        return when (code) {
            in 200..232 -> WeatherType.THUNDERSTORM
            in 300..321 -> WeatherType.LIGHT_RAIN
            in 500..504 -> WeatherType.RAIN
            511 -> WeatherType.SLEET
            in 520..531 -> WeatherType.RAIN
            in 600..602 -> WeatherType.SNOW
            in 611..616 -> WeatherType.SLEET
            in 620..622 -> WeatherType.SNOW
            in 615..616 -> WeatherType.BLIZZARD
            in 701..741 -> WeatherType.FOG
            in 751..781 -> WeatherType.FOG
            800 -> WeatherType.CLEAR
            801 -> WeatherType.SUNNY
            in 802..804 -> WeatherType.CLOUDY
            else -> WeatherType.CLOUDY
        }
    }

    /**
     * Get text color based on time of day and weather
     * Returns hex color string
     */
    fun getTextColor(timeOfDay: TimeOfDay, weatherType: WeatherType): String {
        return when {
            // Morning with clear/sunny weather -> dark text
            timeOfDay == TimeOfDay.MORNING && weatherType in listOf(
                WeatherType.CLEAR,
                WeatherType.SUNNY
            ) -> "#333333"
            // All other cases -> white text
            else -> "#FFFFFF"
        }
    }

    /**
     * Get shadow color based on time of day and weather
     * Returns hex color string
     */
    fun getShadowColor(timeOfDay: TimeOfDay, weatherType: WeatherType): String {
        return when {
            // Morning with clear/sunny weather -> white shadow
            timeOfDay == TimeOfDay.MORNING && weatherType in listOf(
                WeatherType.CLEAR,
                WeatherType.SUNNY
            ) -> "#FFFFFF"
            // All other cases -> black shadow
            else -> "#000000"
        }
    }

    /**
     * Get the image file name for the given time of day and weather
     * Format: {time_of_day}_{weather}.png
     */
    fun getImageFileName(timeOfDay: TimeOfDay, weatherType: WeatherType): String {
        return "${timeOfDay.id}_${weatherType.id}"
    }
}
