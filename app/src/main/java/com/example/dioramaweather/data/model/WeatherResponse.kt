package com.example.dioramaweather.data.model

import com.google.gson.annotations.SerializedName

/**
 * OpenWeatherMap API response model
 * https://openweathermap.org/current
 */
data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val sys: Sys,
    val timezone: Int
)

data class Main(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val humidity: Int
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)
