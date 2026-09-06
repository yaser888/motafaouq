package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.StudyPlanData
import com.example.data.models.Subject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("متفوق", appName)
    }

    @Test
    fun `verify study plan data months and task structure`() {
        assertEquals(9, StudyPlanData.months.size)
        assertTrue(StudyPlanData.allTasks.isNotEmpty())
        
        // Month 1 should have start week 1 and end week 4
        val m1 = StudyPlanData.months.first()
        assertEquals(1, m1.monthNumber)
        assertEquals(1, m1.startWeek)
        assertEquals(4, m1.endWeek)

        // Verify subjects are properly allocated
        val mathTasks = StudyPlanData.allTasks.filter { it.period1Subject == Subject.MATH || it.period2Subject == Subject.MATH }
        assertTrue(mathTasks.isNotEmpty())
    }
}
