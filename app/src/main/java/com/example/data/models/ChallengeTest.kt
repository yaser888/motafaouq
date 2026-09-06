package com.example.data.models

data class ChallengeTest(
    val title: String,
    val description: String,
    val questions: List<MistakeItem>
)
