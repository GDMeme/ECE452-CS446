package com.example.schedula.ui
import com.google.firebase.firestore.DocumentId

data class DailyGoals(
    @DocumentId // This annotation is useful if your Firestore document ID is the goalId
    val goalId: String = "",
    val description: String = "",
    val xp: Int = 0,
    var completed: Boolean = false, // Make it mutable for UI state representation
    val date: String = "" // Add date to link goals to a specific day
)