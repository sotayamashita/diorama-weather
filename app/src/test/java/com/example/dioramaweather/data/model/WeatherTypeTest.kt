package com.example.dioramaweather.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherTypeTest {

    @Test
    fun `fromId returns correct WeatherType`() {
        assertEquals(WeatherType.CLEAR, WeatherType.fromId("clear"))
        assertEquals(WeatherType.SUNNY, WeatherType.fromId("sunny"))
        assertEquals(WeatherType.CLOUDY, WeatherType.fromId("cloudy"))
        assertEquals(WeatherType.FOG, WeatherType.fromId("fog"))
        assertEquals(WeatherType.LIGHT_RAIN, WeatherType.fromId("light_rain"))
        assertEquals(WeatherType.RAIN, WeatherType.fromId("rain"))
        assertEquals(WeatherType.THUNDERSTORM, WeatherType.fromId("thunderstorm"))
        assertEquals(WeatherType.SNOW, WeatherType.fromId("snow"))
        assertEquals(WeatherType.BLIZZARD, WeatherType.fromId("blizzard"))
        assertEquals(WeatherType.SLEET, WeatherType.fromId("sleet"))
    }

    @Test
    fun `fromId returns CLOUDY for unknown id`() {
        assertEquals(WeatherType.CLOUDY, WeatherType.fromId("unknown"))
        assertEquals(WeatherType.CLOUDY, WeatherType.fromId(""))
    }

    @Test
    fun `WeatherType has correct icons`() {
        assertEquals("\u2600\uFE0F", WeatherType.CLEAR.icon)      // ☀️
        assertEquals("\uD83C\uDF24\uFE0F", WeatherType.SUNNY.icon) // 🌤️
        assertEquals("\u2601\uFE0F", WeatherType.CLOUDY.icon)     // ☁️
        assertEquals("\uD83C\uDF27\uFE0F", WeatherType.RAIN.icon)  // 🌧️
        assertEquals("\uD83C\uDF28\uFE0F", WeatherType.SNOW.icon)  // 🌨️
    }
}
