package com.example.data

import com.example.data.models.DayTask
import com.example.data.models.EducationalStream
import com.example.data.models.MonthInfo
import com.example.data.models.PlanConfig

/**
 * StudyPlanData:
 * Now dynamically generates and manages the intensive study plan based on
 * the student's selected educational stream (Grade 9, Bac Scientific, Bac Literary)
 * and custom start/end dates.
 */
object StudyPlanData {

    private var activeConfig: PlanConfig = PlanConfig(
        stream = EducationalStream.BAC_SCIENTIFIC,
        startDateMillis = System.currentTimeMillis(),
        endDateMillis = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000L),
        isConfigured = false
    )

    private var generatedMonths: List<MonthInfo> = emptyList()
    private var generatedTasks: List<DayTask> = emptyList()

    init {
        // Generate initial plan with default configuration
        rebuildPlan()
    }

    val currentConfig: PlanConfig
        get() = activeConfig

    val currentStream: EducationalStream
        get() = activeConfig.stream

    val months: List<MonthInfo>
        get() = generatedMonths

    val allTasks: List<DayTask>
        get() = generatedTasks

    fun configurePlan(config: PlanConfig) {
        activeConfig = config
        rebuildPlan()
    }

    private fun rebuildPlan() {
        val (mList, tList) = IntensivePlanGenerator.generatePlan(activeConfig)
        generatedMonths = mList
        generatedTasks = tList
    }

    fun getDayTaskById(id: String): DayTask? {
        return generatedTasks.firstOrNull { it.id == id }
    }
}
