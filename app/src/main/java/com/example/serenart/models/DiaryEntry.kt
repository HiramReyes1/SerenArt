package com.example.serenart.models

data class DiaryEntry(
    val id: String,
    val date: String,
    val entryText: String,
    val mood: String, // 😔 😐 😊
    val drawingPath: String? = null, // Path local del dibujo
    val exerciseId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)