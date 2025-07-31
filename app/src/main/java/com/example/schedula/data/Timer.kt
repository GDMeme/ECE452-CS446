package com.example.schedula.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "timer_table")
data class Timer(
    @PrimaryKey val id: Int,
    var isRunning: Boolean,
    var isSelected: Boolean,
    var startTime: Long,
    var timeRemaining: Int,
    val timerType: String,
    )