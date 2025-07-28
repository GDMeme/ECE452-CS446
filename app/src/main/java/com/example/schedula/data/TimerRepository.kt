package com.example.schedula.data

import androidx.lifecycle.LiveData

class TimerRepository(private val timerDao: TimerDao) {

    suspend fun insertTimer(timer: Timer) {
        timerDao.insertTimer(timer)
    }

    suspend fun updateTimer(timer: Timer) {
        timerDao.updateTimer(timer)
    }

    suspend fun deleteTimer(timer: Timer) {
        timerDao.deleteTimer(timer)
    }

    suspend fun deleteAllTimers() {
        timerDao.deleteAllTimers()
    }

    fun getAllTimers(): LiveData<List<Timer>> {
        return timerDao.getAllTimers()
    }

    fun getPomodoros(): LiveData<List<Timer>> {
        return timerDao.getPomodoros()
    }

    fun getBreaks(): LiveData<List<Timer>> {
        return timerDao.getBreaks()
    }

    fun getTimerById(id: Int): LiveData<Timer> {
        return timerDao.getTimerById(id)
    }

}
