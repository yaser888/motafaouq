package com.example.data.cloud

import android.content.Context
import android.util.Log
import com.example.data.StudyRepository
import com.example.data.db.QuestionEntity
import com.example.data.db.QuestionFeedbackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Unified Cloud Synchronization Manager:
 * Seamlessly connects the Android app with Supabase Database API and/or Firebase Firestore.
 */
object CloudSyncManager {
    private const val TAG = "CloudSyncManager"

    fun init(context: Context) {
        SupabaseCloudSync.init(context)
    }

    fun getSupabaseUrl(): String = SupabaseCloudSync.baseUrl

    fun setSupabaseConfig(context: Context, url: String, key: String = "") {
        SupabaseCloudSync.setCustomSupabaseConfig(context, url, key)
    }

    /**
     * Upload student's question feedback to the cloud.
     * Tries Supabase first, then Firebase Firestore as fallback.
     */
    suspend fun submitFeedback(feedback: QuestionFeedbackEntity): Boolean = withContext(Dispatchers.IO) {
        var supabaseSuccess = false
        var firebaseSuccess = false

        try {
            supabaseSuccess = SupabaseCloudSync.uploadFeedback(feedback)
        } catch (e: Exception) {
            Log.w(TAG, "Supabase upload error: ${e.message}")
        }

        try {
            if (FirebaseCloudSync.isCloudAvailable) {
                firebaseSuccess = FirebaseCloudSync.uploadStudentFeedback(feedback)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase upload error: ${e.message}")
        }

        Log.d(TAG, "Feedback submitted to cloud: supabase=$supabaseSuccess, firebase=$firebaseSuccess")
        supabaseSuccess || firebaseSuccess
    }

    /**
     * Sync latest questions from the cloud into the local Room database.
     */
    suspend fun syncQuestions(repository: StudyRepository): Int = withContext(Dispatchers.IO) {
        var newQuestionsCount = 0

        // 1. Try Supabase Cloud API
        try {
            val supabaseQuestions = SupabaseCloudSync.fetchQuestions()
            if (supabaseQuestions.isNotEmpty()) {
                repository.addQuestions(supabaseQuestions)
                newQuestionsCount += supabaseQuestions.size
                Log.d(TAG, "Synced ${supabaseQuestions.size} questions from Supabase")
                return@withContext newQuestionsCount
            }
        } catch (e: Exception) {
            Log.w(TAG, "Supabase question sync error: ${e.message}")
        }

        // 2. Try Firebase Firestore
        try {
            if (FirebaseCloudSync.isCloudAvailable) {
                val fbQuestions = FirebaseCloudSync.fetchCloudQuestions()
                if (fbQuestions.isNotEmpty()) {
                    repository.addQuestions(fbQuestions)
                    newQuestionsCount += fbQuestions.size
                    Log.d(TAG, "Synced ${fbQuestions.size} questions from Firebase")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase question sync error: ${e.message}")
        }

        newQuestionsCount
    }

    /**
     * Fetch announcements from the cloud.
     */
    suspend fun fetchAnnouncements(): List<CloudAnnouncement> = withContext(Dispatchers.IO) {
        // Try Supabase first
        try {
            val sAnn = SupabaseCloudSync.fetchAnnouncements()
            if (sAnn.isNotEmpty()) return@withContext sAnn
        } catch (e: Exception) {
            Log.w(TAG, "Supabase announcement error: ${e.message}")
        }

        // Try Firebase
        try {
            if (FirebaseCloudSync.isCloudAvailable) {
                val fbAnn = FirebaseCloudSync.fetchAnnouncements()
                if (fbAnn.isNotEmpty()) return@withContext fbAnn
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase announcement error: ${e.message}")
        }

        emptyList()
    }
}
