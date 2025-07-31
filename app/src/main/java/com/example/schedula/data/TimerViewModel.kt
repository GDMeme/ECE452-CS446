package com.example.schedula.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel(private val repository: TimerRepository) : ViewModel() {

    val timers = getAllTimers()
    val pomodoroTimers = getPomodoros()
    val breakTimers = getBreaks()

    fun runTimer(timer: Timer) {
        viewModelScope.launch {
            while (timer.isRunning && timer.timeRemaining >= 0 ) {
                delay(1000)
                val timeElapsed = (System.currentTimeMillis() - timer.startTime).toInt() / 1000
                timer.timeRemaining -= timeElapsed
                timer.startTime = System.currentTimeMillis()
                updateTimer(timer)
            }
        }
    }

    fun startTimer(timer: Timer) {
        viewModelScope.launch {
            timer.isRunning = true
            timer.startTime = System.currentTimeMillis()
            updateTimer(timer)
            runTimer(timer)
        }
    }

    fun pauseTimer(timer: Timer) {
        viewModelScope.launch {
            timer.isRunning = false
            timer.timeRemaining -= (System.currentTimeMillis() - timer.startTime).toInt() / 1000
            updateTimer(timer)
        }
    }

    fun resetTimer(timer: Timer) {
        viewModelScope.launch {
            val timerType = timer.timerType
            deleteTimer(timer)
            addTimerGivenType(timerType)
        }
    }

// TimerDao Functions
    fun addTimerGivenType(timerType:String) {
        viewModelScope.launch {
            addTimer(
                Timer(
                    id = 0,
                    isRunning = false,
                    startTime = System.currentTimeMillis(),
                    timeRemaining = if (timerType == "Break") 5*60 else 25*60,
                    timerType = timerType
                )
            )
        }
    }
    fun addTimer(timer: Timer) {
        viewModelScope.launch {
            repository.insertTimer(timer)
        }
    }
    fun updateTimer(timer: Timer) {
        viewModelScope.launch {
            repository.updateTimer(timer)
        }
    }
    fun deleteTimer(timer: Timer) {
        viewModelScope.launch {
            repository.deleteTimer(timer)
        }
    }
    fun deleteAllTimers() {
        viewModelScope.launch {
            repository.deleteAllTimers()
        }
    }
    fun getTimerById(id:Int): LiveData<Timer> {
        return repository.getTimerById(id)
    }
    fun getAllTimers(): LiveData<List<Timer>> {
        return repository.getAllTimers()
    }
    fun getPomodoros(): LiveData<List<Timer>> {
        return repository.getPomodoroTimers()
    }
    fun getBreaks(): LiveData<List<Timer>> {
        return repository.getBreakTimers()
    }
}