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
 * Seamlessly connects the Android app with Vercel Serverless API and/or Firebase Firestore.
 */
object CloudSyncManager {
    private const val TAG = "CloudSyncManager"

    fun init(context: Context) {
        VercelCloudSync.init(context)
    }

    fun getVercelUrl(): String = VercelCloudSync.baseUrl

    fun setVercelUrl(context: Context, url: String) {
        VercelCloudSync.setCustomVercelUrl(context, url)
    }

    /**
     * Upload student's question feedback to the cloud.
     * Tries Vercel first, then Firebase Firestore as fallback.
     */
    suspend fun submitFeedback(feedback: QuestionFeedbackEntity): Boolean = withContext(Dispatchers.IO) {
        var vercelSuccess = false
        var firebaseSuccess = false

        try {
            vercelSuccess = VercelCloudSync.uploadFeedback(feedback)
        } catch (e: Exception) {
            Log.w(TAG, "Vercel upload error: ${e.message}")
        }

        try {
            if (FirebaseCloudSync.isCloudAvailable) {
                firebaseSuccess = FirebaseCloudSync.uploadStudentFeedback(feedback)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase upload error: ${e.message}")
        }

        Log.d(TAG, "Feedback submitted to cloud: vercel=$vercelSuccess, firebase=$firebaseSuccess")
        vercelSuccess || firebaseSuccess
    }

    /**
     * Sync latest questions from the cloud into the local Room database.
     */
    suspend fun syncQuestions(repository: StudyRepository): Int = withContext(Dispatchers.IO) {
        var newQuestionsCount = 0

        // 1. Try Vercel Cloud API
        try {
            val vercelQuestions = VercelCloudSync.fetchQuestions()
            if (vercelQuestions.isNotEmpty()) {
                repository.addQuestions(vercelQuestions)
                newQuestionsCount += vercelQuestions.size
                Log.d(TAG, "Synced ${vercelQuestions.size} questions from Vercel")
                return@withContext newQuestionsCount
            }
        } catch (e: Exception) {
            Log.w(TAG, "Vercel question sync error: ${e.message}")
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
        // Try Vercel first
        try {
            val vAnn = VercelCloudSync.fetchAnnouncements()
            if (vAnn.isNotEmpty()) return@withContext vAnn
        } catch (e: Exception) {
            Log.w(TAG, "Vercel announcement error: ${e.message}")
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
