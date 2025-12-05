package com.example.dioramaweather.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.dioramaweather.BuildConfig
import com.example.dioramaweather.R
import com.example.dioramaweather.data.model.WeatherData
import com.example.dioramaweather.data.repository.WeatherRepository
import com.example.dioramaweather.location.LocationService

class WeatherWidget : GlanceAppWidget() {

    companion object {
        private const val TAG = "WeatherWidget"

        // Default location (Tokyo) for fallback
        private const val DEFAULT_LAT = 35.6762
        private const val DEFAULT_LON = 139.6503
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val weatherData = fetchWeatherData(context)

        provideContent {
            GlanceTheme {
                if (weatherData != null) {
                    WeatherWidgetContent(context, weatherData)
                } else {
                    WeatherWidgetLoading()
                }
            }
        }
    }

    private suspend fun fetchWeatherData(context: Context): WeatherData? {
        return try {
            val locationService = LocationService(context)
            val repository = WeatherRepository(BuildConfig.OPENWEATHERMAP_API_KEY)

            val location = locationService.getCurrentLocation()
            val lat = location?.latitude ?: DEFAULT_LAT
            val lon = location?.longitude ?: DEFAULT_LON

            Log.d(TAG, "Fetching weather for lat=$lat, lon=$lon")

            val result = repository.getWeather(lat, lon)
            result.getOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch weather data", e)
            null
        }
    }
}

@Composable
private fun WeatherWidgetContent(context: Context, data: WeatherData) {
    val textColor = Color(android.graphics.Color.parseColor(data.textColor))

    // Get background image resource ID
    val backgroundImageName = data.getBackgroundImageName()
    val backgroundResId = getBackgroundResourceId(context, backgroundImageName)

    Log.d("WeatherWidget", "Background image: $backgroundImageName, resId: $backgroundResId")

    Box(
        modifier = GlanceModifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Background image
        if (backgroundResId != 0) {
            Image(
                provider = ImageProvider(backgroundResId),
                contentDescription = "Weather background",
                modifier = GlanceModifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Fallback background color
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFF87CEEB))
            ) {}
        }

        // Text overlay
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.Top
        ) {
            // City name - Bold, large
            Text(
                text = data.city,
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Weather icon (emoji)
            Text(
                text = data.icon,
                style = TextStyle(
                    fontSize = 36.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Date - Regular, small
            Text(
                text = data.date,
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            // Temperature - Medium, large
            Text(
                text = "${data.temperature}°",
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

/**
 * Get drawable resource ID for background image
 * Looks for {time_of_day}_{weather}.png in drawable folder
 * Falls back to default image if specific one not found
 */
private fun getBackgroundResourceId(context: Context, imageName: String): Int {
    // Try to find specific image
    var resId = context.resources.getIdentifier(
        imageName,
        "drawable",
        context.packageName
    )

    if (resId == 0) {
        // Try fallback image
        resId = context.resources.getIdentifier(
            "widget_background_default",
            "drawable",
            context.packageName
        )
    }

    return resId
}

@Composable
private fun WeatherWidgetLoading() {
    val backgroundColor = Color(0xFFE0E0E0)
    val textColor = Color(0xFF666666)

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Loading...",
            style = TextStyle(
                color = ColorProvider(textColor),
                fontSize = 14.sp
            )
        )
    }
}
