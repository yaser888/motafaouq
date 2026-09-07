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
 * Supabase Cloud Sync Client:
 * Connects the Android app directly with the Supabase Realtime Database & Storage REST API.
 */
object SupabaseCloudSync {
    private const val TAG = "SupabaseCloudSync"
    private const val PREFS_NAME = "supabase_cloud_config"
    private const val KEY_BASE_URL = "supabase_base_url"
    private const val KEY_ANON_KEY = "supabase_anon_key"

    // Default Supabase project URL & Key
    var baseUrl: String = "https://aqhfysxthxhjnvqbdqkf.supabase.co"
        private set

    var apiKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFxaGZ5c3h0aHhoam52cWJkcWtmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODg3MTc2ODQsImV4cCI6MjEwNDI5MzY4NH0.bkjTl9iMjKsABr1dCowfgPb3bMR-8jRUHM-dvZUAdao"
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
        apiKey = prefs.getString(KEY_ANON_KEY, apiKey) ?: apiKey
    }

    fun setCustomSupabaseConfig(context: Context, newUrl: String, newKey: String = apiKey) {
        var cleanUrl = newUrl.trim()
        if (cleanUrl.endsWith("/")) {
            cleanUrl = cleanUrl.substring(0, cleanUrl.length - 1)
        }
        baseUrl = cleanUrl
        apiKey = newKey.trim()

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_BASE_URL, cleanUrl)
            .putString(KEY_ANON_KEY, apiKey)
            .apply()
        Log.d(TAG, "Supabase config updated: url=$baseUrl")
    }

    /**
     * Upload student's question feedback directly to Supabase table
     */
    suspend fun uploadFeedback(feedback: QuestionFeedbackEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("id", "fb_${feedback.id}_${System.currentTimeMillis()}")
                put("question_id", feedback.questionId)
                put("question_text", feedback.questionText)
                put("subject", feedback.subject)
                put("selected_option", feedback.selectedOption)
                put("feedback_reason", feedback.feedbackReason)
                put("note_text", feedback.noteText)
                put("timestamp", feedback.timestamp)
            }

            val requestBody = json.toString().toRequestBody(JSON_MEDIA_TYPE)
            val request = Request.Builder()
                .url("$baseUrl/rest/v1/feedbacks")
                .header("apikey", apiKey)
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .header("Prefer", "return=minimal")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val isSuccess = response.isSuccessful || response.code == 201
            Log.d(TAG, "Supabase Feedback upload result: code=${response.code}, success=$isSuccess")
            response.close()
            isSuccess
        } catch (e: Exception) {
            Log.w(TAG, "Could not upload feedback to Supabase ($baseUrl): ${e.message}")
            false
        }
    }

    /**
     * Fetch the latest questions from Supabase database table
     */
    suspend fun fetchQuestions(subject: String? = null): List<QuestionEntity> = withContext(Dispatchers.IO) {
        try {
            val url = if (subject != null && subject.isNotEmpty()) {
                "$baseUrl/rest/v1/questions?subject=eq.$subject&select=*"
            } else {
                "$baseUrl/rest/v1/questions?select=*"
            }

            val request = Request.Builder()
                .url(url)
                .header("apikey", apiKey)
                .header("Authorization", "Bearer $apiKey")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            response.close()

            if (!response.isSuccessful || bodyString.isEmpty()) {
                return@withContext emptyList()
            }

            val dataArray = if (bodyString.trim().startsWith("[")) {
                JSONArray(bodyString)
            } else {
                JSONObject(bodyString).optJSONArray("data") ?: JSONArray()
            }

            val list = mutableListOf<QuestionEntity>()
            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                list.add(
                    QuestionEntity(
                        id = 0L,
                        subject = item.optString("subject", "MATH"),
                        unitOrTopic = item.optString("unit_or_topic", item.optString("unitOrTopic", "الوحدة العامة")),
                        questionText = item.optString("question_text", item.optString("questionText", "")),
                        questionType = item.optString("question_type", item.optString("questionType", "MCQ")),
                        optionA = item.optString("option_a", item.optString("optionA", "")),
                        optionB = item.optString("option_b", item.optString("optionB", "")),
                        optionC = item.optString("option_c", item.optString("optionC", "")),
                        optionD = item.optString("option_d", item.optString("optionD", "")),
                        correctAnswer = item.optString("correct_answer", item.optString("correctAnswer", "A")),
                        explanation = item.optString("explanation", ""),
                        difficulty = item.optString("difficulty", "متوسط"),
                        yearOrSource = item.optString("year_or_source", item.optString("yearOrSource", "سحابي من قاعدة Supabase")),
                        isStarred = false,
                        createdTimestamp = System.currentTimeMillis()
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching questions from Supabase: ${e.message}")
            emptyList()
        }
    }

    /**
     * Fetch announcements from Supabase
     */
    suspend fun fetchAnnouncements(): List<CloudAnnouncement> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/rest/v1/announcements?select=*")
                .header("apikey", apiKey)
                .header("Authorization", "Bearer $apiKey")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            response.close()

            if (!response.isSuccessful || bodyString.isEmpty()) {
                return@withContext emptyList()
            }

            val dataArray = if (bodyString.trim().startsWith("[")) {
                JSONArray(bodyString)
            } else {
                JSONObject(bodyString).optJSONArray("data") ?: JSONArray()
            }

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
                        isImportant = item.optBoolean("is_important", item.optBoolean("isImportant", false))
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching announcements from Supabase: ${e.message}")
            emptyList()
        }
    }
}
