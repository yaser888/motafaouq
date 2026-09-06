package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.EducationalStream
import com.example.data.models.PlanConfig

class PlanPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mutafawweq_plan_prefs", Context.MODE_PRIVATE)

    fun getPlanConfig(): PlanConfig {
        val streamId = prefs.getString(KEY_STREAM, EducationalStream.BAC_SCIENTIFIC.id)
        val stream = EducationalStream.fromId(streamId)
        val now = System.currentTimeMillis()
        val defaultEnd = now + (90L * 24 * 60 * 60 * 1000L)
        val startMillis = prefs.getLong(KEY_START_MILLIS, now)
        val endMillis = prefs.getLong(KEY_END_MILLIS, defaultEnd)
        val isConfigured = prefs.getBoolean(KEY_IS_CONFIGURED, false)

        return PlanConfig(
            stream = stream,
            startDateMillis = startMillis,
            endDateMillis = endMillis,
            isConfigured = isConfigured
        )
    }

    fun savePlanConfig(config: PlanConfig) {
        prefs.edit()
            .putString(KEY_STREAM, config.stream.id)
            .putLong(KEY_START_MILLIS, config.startDateMillis)
            .putLong(KEY_END_MILLIS, config.endDateMillis)
            .putBoolean(KEY_IS_CONFIGURED, true)
            .apply()
    }

    companion object {
        private const val KEY_STREAM = "key_educational_stream"
        private const val KEY_START_MILLIS = "key_plan_start_millis"
        private const val KEY_END_MILLIS = "key_plan_end_millis"
        private const val KEY_IS_CONFIGURED = "key_plan_is_configured"
    }
}
