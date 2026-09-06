package com.example.data.cloud

import android.util.Log
import com.example.data.db.QuestionEntity
import com.example.data.models.Subject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class CloudAnnouncement(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val date: String = "",
    val timestamp: Long = 0L,
    val isImportant: Boolean = false
)

object FirebaseCloudSync {
    private const val TAG = "FirebaseCloudSync"
    private const val COLLECTION_QUESTIONS = "questions_bank"
    private const val COLLECTION_ANNOUNCEMENTS = "announcements"
    private const val COLLECTION_APP_CONFIG = "app_config"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Firestore not yet configured with google-services.json", e)
            null
        }
    }

    val isCloudAvailable: Boolean
        get() = firestore != null

    /**
     * Upload a single question to Cloud Firestore
     */
    suspend fun uploadQuestion(question: QuestionEntity): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            val docId = if (question.id > 0) "q_${question.id}" else "q_${System.currentTimeMillis()}"
            val map = hashMapOf(
                "subject" to question.subject,
                "unitOrTopic" to question.unitOrTopic,
                "questionText" to question.questionText,
                "questionType" to question.questionType,
                "optionA" to question.optionA,
                "optionB" to question.optionB,
                "optionC" to question.optionC,
                "optionD" to question.optionD,
                "correctAnswer" to question.correctAnswer,
                "explanation" to question.explanation,
                "difficulty" to question.difficulty,
                "yearOrSource" to question.yearOrSource,
                "timestamp" to question.createdTimestamp
            )
            db.collection(COLLECTION_QUESTIONS).document(docId).set(map, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading question to cloud", e)
            false
        }
    }

    /**
     * Batch upload multiple questions extracted from PDF or created by Admin
     */
    suspend fun uploadQuestionsBatch(questions: List<QuestionEntity>): Int = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext 0
        try {
            var count = 0
            val batch = db.batch()
            for (q in questions) {
                val docId = "q_${System.currentTimeMillis()}_${count}"
                val docRef = db.collection(COLLECTION_QUESTIONS).document(docId)
                val map = hashMapOf(
                    "subject" to q.subject,
                    "unitOrTopic" to q.unitOrTopic,
                    "questionText" to q.questionText,
                    "questionType" to q.questionType,
                    "optionA" to q.optionA,
                    "optionB" to q.optionB,
                    "optionC" to q.optionC,
                    "optionD" to q.optionD,
                    "correctAnswer" to q.correctAnswer,
                    "explanation" to q.explanation,
                    "difficulty" to q.difficulty,
                    "yearOrSource" to q.yearOrSource,
                    "timestamp" to System.currentTimeMillis()
                )
                batch.set(docRef, map, SetOptions.merge())
                count++
            }
            batch.commit().await()
            count
        } catch (e: Exception) {
            Log.e(TAG, "Error in batch upload questions", e)
            0
        }
    }

    /**
     * Fetch all questions from Cloud Firestore
     */
    suspend fun fetchCloudQuestions(): List<QuestionEntity> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext emptyList()
        try {
            val snapshot = db.collection(COLLECTION_QUESTIONS).get().await()
            val list = mutableListOf<QuestionEntity>()
            for (doc in snapshot.documents) {
                val data = doc.data ?: continue
                list.add(
                    QuestionEntity(
                        id = 0L,
                        subject = data["subject"] as? String ?: Subject.MATH.name,
                        unitOrTopic = data["unitOrTopic"] as? String ?: "الوحدة العامة",
                        questionText = data["questionText"] as? String ?: "",
                        questionType = data["questionType"] as? String ?: "MCQ",
                        optionA = data["optionA"] as? String ?: "",
                        optionB = data["optionB"] as? String ?: "",
                        optionC = data["optionC"] as? String ?: "",
                        optionD = data["optionD"] as? String ?: "",
                        correctAnswer = data["correctAnswer"] as? String ?: "A",
                        explanation = data["explanation"] as? String ?: "",
                        difficulty = data["difficulty"] as? String ?: "متوسط",
                        yearOrSource = data["yearOrSource"] as? String ?: "بنك السحابة الوزاري",
                        isStarred = false,
                        createdTimestamp = (data["timestamp"] as? Long) ?: System.currentTimeMillis()
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching questions from cloud", e)
            emptyList()
        }
    }

    /**
     * Real-time listener for cloud questions updates
     */
    fun observeCloudQuestions(): Flow<List<QuestionEntity>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        var listener: ListenerRegistration? = null
        try {
            listener = db.collection(COLLECTION_QUESTIONS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Listen failed for questions", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { doc ->
                            val data = doc.data ?: return@mapNotNull null
                            QuestionEntity(
                                id = 0L,
                                subject = data["subject"] as? String ?: Subject.MATH.name,
                                unitOrTopic = data["unitOrTopic"] as? String ?: "الوحدة العامة",
                                questionText = data["questionText"] as? String ?: "",
                                questionType = data["questionType"] as? String ?: "MCQ",
                                optionA = data["optionA"] as? String ?: "",
                                optionB = data["optionB"] as? String ?: "",
                                optionC = data["optionC"] as? String ?: "",
                                optionD = data["optionD"] as? String ?: "",
                                correctAnswer = data["correctAnswer"] as? String ?: "A",
                                explanation = data["explanation"] as? String ?: "",
                                difficulty = data["difficulty"] as? String ?: "متوسط",
                                yearOrSource = data["yearOrSource"] as? String ?: "سحابي أونلاين",
                                isStarred = false,
                                createdTimestamp = (data["timestamp"] as? Long) ?: System.currentTimeMillis()
                            )
                        }
                        trySend(list)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up snapshot listener", e)
        }

        awaitClose {
            listener?.remove()
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Fetch Announcements broadcasted from Web Admin Dashboard
     */
    suspend fun fetchAnnouncements(): List<CloudAnnouncement> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext emptyList()
        try {
            val snapshot = db.collection(COLLECTION_ANNOUNCEMENTS).get().await()
            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                CloudAnnouncement(
                    id = doc.id,
                    title = data["title"] as? String ?: "",
                    message = data["message"] as? String ?: "",
                    date = data["date"] as? String ?: "",
                    timestamp = (data["timestamp"] as? Long) ?: 0L,
                    isImportant = (data["isImportant"] as? Boolean) ?: false
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching announcements", e)
            emptyList()
        }
    }
}
