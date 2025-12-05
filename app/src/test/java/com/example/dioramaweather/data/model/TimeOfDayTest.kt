package com.example.dioramaweather.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeOfDayTest {

    @Test
    fun `fromHour returns MORNING for hours 6-11`() {
        assertEquals(TimeOfDay.MORNING, TimeOfDay.fromHour(6))
        assertEquals(TimeOfDay.MORNING, TimeOfDay.fromHour(9))
        assertEquals(TimeOfDay.MORNING, TimeOfDay.fromHour(11))
    }

    @Test
    fun `fromHour returns AFTERNOON for hours 12-16`() {
        assertEquals(TimeOfDay.AFTERNOON, TimeOfDay.fromHour(12))
        assertEquals(TimeOfDay.AFTERNOON, TimeOfDay.fromHour(14))
        assertEquals(TimeOfDay.AFTERNOON, TimeOfDay.fromHour(16))
    }

    @Test
    fun `fromHour returns EVENING for hours 17-19`() {
        assertEquals(TimeOfDay.EVENING, TimeOfDay.fromHour(17))
        assertEquals(TimeOfDay.EVENING, TimeOfDay.fromHour(18))
        assertEquals(TimeOfDay.EVENING, TimeOfDay.fromHour(19))
    }

    @Test
    fun `fromHour returns NIGHT for hours 20-23 and 0-5`() {
        assertEquals(TimeOfDay.NIGHT, TimeOfDay.fromHour(20))
        assertEquals(TimeOfDay.NIGHT, TimeOfDay.fromHour(22))
        assertEquals(TimeOfDay.NIGHT, TimeOfDay.fromHour(23))
        assertEquals(TimeOfDay.NIGHT, TimeOfDay.fromHour(0))
        assertEquals(TimeOfDay.NIGHT, TimeOfDay.fromHour(3))
        assertEquals(TimeOfDay.NIGHT, TimeOfDay.fromHour(5))
    }

    @Test
    fun `TimeOfDay has correct id values`() {
        assertEquals("morning", TimeOfDay.MORNING.id)
        assertEquals("afternoon", TimeOfDay.AFTERNOON.id)
        assertEquals("evening", TimeOfDay.EVENING.id)
        assertEquals("night", TimeOfDay.NIGHT.id)
    }
}
