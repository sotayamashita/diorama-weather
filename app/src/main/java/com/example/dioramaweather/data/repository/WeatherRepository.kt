package com.example.dioramaweather.data.repository

import android.util.Log
import com.example.dioramaweather.data.api.WeatherApiService
import com.example.dioramaweather.data.api.WeatherCodeMapper
import com.example.dioramaweather.data.model.TimeOfDay
import com.example.dioramaweather.data.model.WeatherData
import com.example.dioramaweather.data.model.WeatherResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Repository for fetching and transforming weather data
 */
class WeatherRepository(
    private val apiKey: String
) {
    companion object {
        private const val TAG = "WeatherRepository"
    }

    /**
     * Fetch weather data for the given coordinates
     */
    suspend fun getWeather(latitude: Double, longitude: Double): Result<WeatherData> {
        return try {
            val response = WeatherApiService.api.getCurrentWeather(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey
            )
            Log.d(TAG, "Weather API response: $response")

            val weatherData = transformResponse(response)
            Result.success(weatherData)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch weather", e)
            Result.failure(e)
        }
    }

    /**
     * Transform API response to app's WeatherData model
     */
    private fun transformResponse(response: WeatherResponse): WeatherData {
        val weatherCode = response.weather.firstOrNull()?.id ?: 800
        val weatherType = WeatherCodeMapper.mapWeatherCode(weatherCode)

        // Calculate local time based on timezone offset
        val localTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            add(Calendar.SECOND, response.timezone)
        }
        val hour = localTime.get(Calendar.HOUR_OF_DAY)
        val timeOfDay = TimeOfDay.fromHour(hour)

        // Format date in Japanese style
        val dateFormat = SimpleDateFormat("M月d日", Locale.JAPANESE)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val adjustedTime = Date(System.currentTimeMillis() + response.timezone * 1000L)
        val formattedDate = dateFormat.format(adjustedTime)

        return WeatherData(
            city = response.name,
            temperature = response.main.temp.toInt(),
            weather = weatherType.id,
            timeOfDay = timeOfDay,
            icon = weatherType.icon,
            textColor = WeatherCodeMapper.getTextColor(timeOfDay, weatherType),
            shadowColor = WeatherCodeMapper.getShadowColor(timeOfDay, weatherType),
            date = formattedDate
        )
    }
}
