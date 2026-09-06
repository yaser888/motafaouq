package com.example.data.cloud

import android.content.Context
import android.util.Log
import com.example.data.db.QuestionEntity
import com.example.data.db.QuestionFeedbackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Vercel Cloud Sync Client:
 * Connects the Android app directly with the Vercel-hosted Admin Panel and Serverless API.
 */
object VercelCloudSync {
    private const val TAG = "VercelCloudSync"
    private const val PREFS_NAME = "vercel_cloud_config"
    private const val KEY_BASE_URL = "vercel_base_url"

    // Default Vercel deployment URL (fallback or placeholder)
    var baseUrl: String = "https://your-admin.vercel.app"
        private set

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        baseUrl = prefs.getString(KEY_BASE_URL, baseUrl) ?: baseUrl
    }

    fun setCustomVercelUrl(context: Context, newUrl: String) {
        var cleanUrl = newUrl.trim()
        if (cleanUrl.endsWith("/")) {
            cleanUrl = cleanUrl.substring(0, cleanUrl.length - 1)
        }
        baseUrl = cleanUrl
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_BASE_URL, cleanUrl)
            .apply()
        Log.d(TAG, "Vercel base URL updated to: $baseUrl")
    }

    /**
     * Upload student's question feedback / note directly to Vercel Cloud API
     */
    suspend fun uploadFeedback(feedback: QuestionFeedbackEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("id", "fb_${feedback.id}_${System.currentTimeMillis()}")
                put("questionId", feedback.questionId)
                put("questionText", feedback.questionText)
                put("subject", feedback.subject)
                put("selectedOption", feedback.selectedOption)
                put("feedbackReason", feedback.feedbackReason)
                put("noteText", feedback.noteText)
                put("timestamp", feedback.timestamp)
            }

            val requestBody = json.toString().toRequestBody(JSON_MEDIA_TYPE)
            val request = Request.Builder()
                .url("$baseUrl/api/feedbacks")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val isSuccess = response.isSuccessful
            Log.d(TAG, "Feedback upload result: code=${response.code}, success=$isSuccess")
            response.close()
            isSuccess
        } catch (e: Exception) {
            Log.w(TAG, "Could not upload feedback to Vercel ($baseUrl): ${e.message}")
            false
        }
    }

    /**
     * Fetch the latest questions from Vercel Cloud API
     */
    suspend fun fetchQuestions(subject: String? = null): List<QuestionEntity> = withContext(Dispatchers.IO) {
        try {
            val url = if (subject != null && subject.isNotEmpty()) {
                "$baseUrl/api/questions?subject=$subject"
            } else {
                "$baseUrl/api/questions"
            }

            val request = Request.Builder().url(url).get().build()
            val response = client.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            response.close()

            if (!response.isSuccessful || bodyString.isEmpty()) {
                return@withContext emptyList()
            }

            val json = JSONObject(bodyString)
            val dataArray = json.optJSONArray("data") ?: JSONArray()
            val list = mutableListOf<QuestionEntity>()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                list.add(
                    QuestionEntity(
                        id = 0L,
                        subject = item.optString("subject", "MATH"),
                        unitOrTopic = item.optString("unitOrTopic", "الوحدة العامة"),
                        questionText = item.optString("questionText", ""),
                        questionType = item.optString("questionType", "MCQ"),
                        optionA = item.optString("optionA", ""),
                        optionB = item.optString("optionB", ""),
                        optionC = item.optString("optionC", ""),
                        optionD = item.optString("optionD", ""),
                        correctAnswer = item.optString("correctAnswer", "A"),
                        explanation = item.optString("explanation", ""),
                        difficulty = item.optString("difficulty", "متوسط"),
                        yearOrSource = item.optString("yearOrSource", "سحابي من لوحة Vercel"),
                        isStarred = false,
                        createdTimestamp = System.currentTimeMillis()
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching questions from Vercel: ${e.message}")
            emptyList()
        }
    }

    /**
     * Fetch announcements published on Vercel Admin Panel
     */
    suspend fun fetchAnnouncements(): List<CloudAnnouncement> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url("$baseUrl/api/announcements").get().build()
            val response = client.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            response.close()

            if (!response.isSuccessful || bodyString.isEmpty()) {
                return@withContext emptyList()
            }

            val json = JSONObject(bodyString)
            val dataArray = json.optJSONArray("data") ?: JSONArray()
            val list = mutableListOf<CloudAnnouncement>()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                list.add(
                    CloudAnnouncement(
                        id = item.optString("id", "ann_$i"),
                        title = item.optString("title", ""),
                        message = item.optString("message", ""),
                        date = item.optString("date", "الآن"),
                        timestamp = item.optLong("timestamp", System.currentTimeMillis()),
                        isImportant = item.optBoolean("isImportant", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching announcements from Vercel: ${e.message}")
            emptyList()
        }
    }
}
