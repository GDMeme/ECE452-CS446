package com.example.schedula.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "timer_table")
data class Timer(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var isRunning: Boolean,
    var startTime: Long,
    var timeRemaining: Int,
    val timerType: String,
    )