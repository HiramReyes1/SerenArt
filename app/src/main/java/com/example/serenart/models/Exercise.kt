package com.example.serenart.models

data class Exercise(
    val id: String,
    val title: String,
    val description: String,
    val duration: String,
    val category: String,
    val imageRes: Int, // Recurso de imagen local
    val isCompleted: Boolean = false
)
