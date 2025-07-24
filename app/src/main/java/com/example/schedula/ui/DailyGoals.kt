package com.example.schedula.ui
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

data class DailyGoals (
    val id: String,
    val description: String,
    val xp: Int
) {
    var completed by mutableStateOf(false)
}