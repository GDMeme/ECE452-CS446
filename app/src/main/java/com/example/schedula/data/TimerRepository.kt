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
    fun getTimerById(id: Int): LiveData<Timer> {
        return timerDao.getTimerById(id)
    }
    fun getPomodoroTimers(): LiveData<List<Timer>> {
        return timerDao.getPomodoroTimers()
    }
    fun getBreakTimers(): LiveData<List<Timer>> {
        return timerDao.getBreakTimers()
    }
}
