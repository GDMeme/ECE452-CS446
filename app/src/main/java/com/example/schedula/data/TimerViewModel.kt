package com.example.schedula.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.timer

class TimerViewModel(private val repository: TimerRepository) : ViewModel() {
    init {
        initializeTimers()
    }

    private fun initializeTimers() {
        viewModelScope.launch {
            val existingTimers = repository.getAllTimers().value
            if (existingTimers.isNullOrEmpty()) {
                insertInitialTimers()
            }
        }
    }

    private suspend fun insertInitialTimers() {
        val pomodoroTimer = Timer(
            id = 1,
            isRunning = false,
            isSelected = true,
            startTime = System.currentTimeMillis(),
            timerType = "Pomodoro",
            timeRemaining = 25 * 60
        )
        val breakTimer = Timer(
            id = 2,
            isRunning = false,
            isSelected = false,
            startTime = System.currentTimeMillis(),
            timerType = "Break",
            timeRemaining = 5 * 60
        )
        repository.insertTimer(pomodoroTimer)
        repository.insertTimer(breakTimer)
    }

    val timers: LiveData<List<Timer>> = repository.getAllTimers()
    val pomodoroTimer: LiveData<Timer> = repository.getTimerById(1)
    val breakTimer: LiveData<Timer> = repository.getTimerById(2)

    fun startTimer(timer: Timer) {
        viewModelScope.launch {
            var timeRemaining = timer.timeRemaining
            timer.startTime = System.currentTimeMillis()
            timer.isRunning = true

            while (timeRemaining > 0 && timer.isRunning) {
                delay(1000)
                val elapsedTime = (System.currentTimeMillis() - timer.startTime) / 1000
                timeRemaining = (timer.timeRemaining - elapsedTime).coerceAtLeast(0).toInt()
                repository.updateTimer(timer.copy(timeRemaining = timeRemaining))
            }
            repository.updateTimer(timer)
        }
    }
    fun pauseTimer(timer: Timer) {
        viewModelScope.launch {
            val timeElapsed = (System.currentTimeMillis() - timer.startTime) / 1000 // Time elapsed in seconds
            val remainingTime = timer.timeRemaining - timeElapsed.toInt()
            timer.isRunning = false
            timer.timeRemaining = remainingTime
            repository.updateTimer(timer)
        }
    }
    fun resetTimer(timer: Timer) {
        viewModelScope.launch {
            val initialTime = if (timer.timerType == "Pomodoro") 25 * 60 else 5 * 60 // 25 minutes for Pomodoro, 5 minutes for Break
            timer.isRunning = false
            timer.timeRemaining = initialTime
            timer.startTime = System.currentTimeMillis()
            repository.updateTimer(timer)
        }
    }


// TimerDao Functions
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
}