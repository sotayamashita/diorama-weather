package com.example.dioramaweather.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * Worker for updating weather widget in the background
 */
class WeatherWidgetWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "WeatherWidgetWorker"
        private const val WORK_NAME = "weather_widget_update"

        // Update interval: 30 minutes
        private const val UPDATE_INTERVAL_MINUTES = 30L

        /**
         * Schedule periodic weather updates
         */
        fun schedule(context: Context) {
            Log.d(TAG, "Scheduling weather widget updates every $UPDATE_INTERVAL_MINUTES minutes")

            val workRequest = PeriodicWorkRequestBuilder<WeatherWidgetWorker>(
                UPDATE_INTERVAL_MINUTES, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        /**
         * Cancel scheduled updates
         */
        fun cancel(context: Context) {
            Log.d(TAG, "Cancelling weather widget updates")
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting weather widget update")

        return try {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(WeatherWidget::class.java)

            if (glanceIds.isEmpty()) {
                Log.d(TAG, "No widgets found, skipping update")
                return Result.success()
            }

            Log.d(TAG, "Updating ${glanceIds.size} widget(s)")

            glanceIds.forEach { glanceId ->
                WeatherWidget().update(context, glanceId)
            }

            Log.d(TAG, "Weather widget update completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update weather widget", e)
            Result.retry()
        }
    }
}
