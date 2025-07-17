package com.example.schedula.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer_table")
data class Timer (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val timerRunning: Boolean,
    val minutes: Int,
    val seconds: Int,
    val timerType: String,
    )