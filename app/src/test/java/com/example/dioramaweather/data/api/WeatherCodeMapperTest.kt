package com.example.dioramaweather.data.api

import com.example.dioramaweather.data.model.TimeOfDay
import com.example.dioramaweather.data.model.WeatherType
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherCodeMapperTest {

    @Test
    fun `mapWeatherCode returns THUNDERSTORM for codes 200-232`() {
        assertEquals(WeatherType.THUNDERSTORM, WeatherCodeMapper.mapWeatherCode(200))
        assertEquals(WeatherType.THUNDERSTORM, WeatherCodeMapper.mapWeatherCode(215))
        assertEquals(WeatherType.THUNDERSTORM, WeatherCodeMapper.mapWeatherCode(232))
    }

    @Test
    fun `mapWeatherCode returns LIGHT_RAIN for codes 300-321`() {
        assertEquals(WeatherType.LIGHT_RAIN, WeatherCodeMapper.mapWeatherCode(300))
        assertEquals(WeatherType.LIGHT_RAIN, WeatherCodeMapper.mapWeatherCode(310))
        assertEquals(WeatherType.LIGHT_RAIN, WeatherCodeMapper.mapWeatherCode(321))
    }

    @Test
    fun `mapWeatherCode returns RAIN for codes 500-504 and 520-531`() {
        assertEquals(WeatherType.RAIN, WeatherCodeMapper.mapWeatherCode(500))
        assertEquals(WeatherType.RAIN, WeatherCodeMapper.mapWeatherCode(502))
        assertEquals(WeatherType.RAIN, WeatherCodeMapper.mapWeatherCode(504))
        assertEquals(WeatherType.RAIN, WeatherCodeMapper.mapWeatherCode(520))
        assertEquals(WeatherType.RAIN, WeatherCodeMapper.mapWeatherCode(531))
    }

    @Test
    fun `mapWeatherCode returns SLEET for code 511 and 611-616`() {
        assertEquals(WeatherType.SLEET, WeatherCodeMapper.mapWeatherCode(511))
        assertEquals(WeatherType.SLEET, WeatherCodeMapper.mapWeatherCode(611))
        assertEquals(WeatherType.SLEET, WeatherCodeMapper.mapWeatherCode(613))
    }

    @Test
    fun `mapWeatherCode returns SNOW for codes 600-602 and 620-622`() {
        assertEquals(WeatherType.SNOW, WeatherCodeMapper.mapWeatherCode(600))
        assertEquals(WeatherType.SNOW, WeatherCodeMapper.mapWeatherCode(601))
        assertEquals(WeatherType.SNOW, WeatherCodeMapper.mapWeatherCode(602))
        assertEquals(WeatherType.SNOW, WeatherCodeMapper.mapWeatherCode(620))
        assertEquals(WeatherType.SNOW, WeatherCodeMapper.mapWeatherCode(622))
    }

    @Test
    fun `mapWeatherCode returns FOG for codes 701-781`() {
        assertEquals(WeatherType.FOG, WeatherCodeMapper.mapWeatherCode(701))
        assertEquals(WeatherType.FOG, WeatherCodeMapper.mapWeatherCode(721))
        assertEquals(WeatherType.FOG, WeatherCodeMapper.mapWeatherCode(741))
        assertEquals(WeatherType.FOG, WeatherCodeMapper.mapWeatherCode(751))
        assertEquals(WeatherType.FOG, WeatherCodeMapper.mapWeatherCode(781))
    }

    @Test
    fun `mapWeatherCode returns CLEAR for code 800`() {
        assertEquals(WeatherType.CLEAR, WeatherCodeMapper.mapWeatherCode(800))
    }

    @Test
    fun `mapWeatherCode returns SUNNY for code 801`() {
        assertEquals(WeatherType.SUNNY, WeatherCodeMapper.mapWeatherCode(801))
    }

    @Test
    fun `mapWeatherCode returns CLOUDY for codes 802-804`() {
        assertEquals(WeatherType.CLOUDY, WeatherCodeMapper.mapWeatherCode(802))
        assertEquals(WeatherType.CLOUDY, WeatherCodeMapper.mapWeatherCode(803))
        assertEquals(WeatherType.CLOUDY, WeatherCodeMapper.mapWeatherCode(804))
    }

    @Test
    fun `mapWeatherCode returns CLOUDY for unknown codes`() {
        assertEquals(WeatherType.CLOUDY, WeatherCodeMapper.mapWeatherCode(999))
        assertEquals(WeatherType.CLOUDY, WeatherCodeMapper.mapWeatherCode(0))
        assertEquals(WeatherType.CLOUDY, WeatherCodeMapper.mapWeatherCode(-1))
    }

    @Test
    fun `getTextColor returns dark color for morning sunny weather`() {
        assertEquals("#333333", WeatherCodeMapper.getTextColor(TimeOfDay.MORNING, WeatherType.CLEAR))
        assertEquals("#333333", WeatherCodeMapper.getTextColor(TimeOfDay.MORNING, WeatherType.SUNNY))
    }

    @Test
    fun `getTextColor returns white for other conditions`() {
        assertEquals("#FFFFFF", WeatherCodeMapper.getTextColor(TimeOfDay.AFTERNOON, WeatherType.CLEAR))
        assertEquals("#FFFFFF", WeatherCodeMapper.getTextColor(TimeOfDay.EVENING, WeatherType.CLOUDY))
        assertEquals("#FFFFFF", WeatherCodeMapper.getTextColor(TimeOfDay.NIGHT, WeatherType.RAIN))
        assertEquals("#FFFFFF", WeatherCodeMapper.getTextColor(TimeOfDay.MORNING, WeatherType.CLOUDY))
    }

    @Test
    fun `getShadowColor returns white for morning sunny weather`() {
        assertEquals("#FFFFFF", WeatherCodeMapper.getShadowColor(TimeOfDay.MORNING, WeatherType.CLEAR))
        assertEquals("#FFFFFF", WeatherCodeMapper.getShadowColor(TimeOfDay.MORNING, WeatherType.SUNNY))
    }

    @Test
    fun `getShadowColor returns black for other conditions`() {
        assertEquals("#000000", WeatherCodeMapper.getShadowColor(TimeOfDay.AFTERNOON, WeatherType.CLEAR))
        assertEquals("#000000", WeatherCodeMapper.getShadowColor(TimeOfDay.EVENING, WeatherType.CLOUDY))
        assertEquals("#000000", WeatherCodeMapper.getShadowColor(TimeOfDay.NIGHT, WeatherType.RAIN))
        assertEquals("#000000", WeatherCodeMapper.getShadowColor(TimeOfDay.MORNING, WeatherType.CLOUDY))
    }

    @Test
    fun `getImageFileName returns correct format`() {
        assertEquals("morning_clear", WeatherCodeMapper.getImageFileName(TimeOfDay.MORNING, WeatherType.CLEAR))
        assertEquals("afternoon_sunny", WeatherCodeMapper.getImageFileName(TimeOfDay.AFTERNOON, WeatherType.SUNNY))
        assertEquals("evening_rain", WeatherCodeMapper.getImageFileName(TimeOfDay.EVENING, WeatherType.RAIN))
        assertEquals("night_snow", WeatherCodeMapper.getImageFileName(TimeOfDay.NIGHT, WeatherType.SNOW))
    }
}
