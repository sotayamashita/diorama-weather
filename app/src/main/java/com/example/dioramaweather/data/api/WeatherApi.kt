package com.example.dioramaweather.data.api

import com.example.dioramaweather.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * OpenWeatherMap API interface
 * https://openweathermap.org/current
 */
interface WeatherApi {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "ja"
    ): WeatherResponse

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    }
}
