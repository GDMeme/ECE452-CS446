package com.example.schedula.data

import androidx.lifecycle.LiveData

class TimerRepository(private val timerDao: TimerDao) {

    val readAllData: LiveData<List<Timer>> = timerDao.readAllData()

    suspend fun addTimer(timer: Timer){
        timerDao.addTimer(timer)
    }
}