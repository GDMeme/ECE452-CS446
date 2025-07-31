package com.example.schedula.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel(private val repository: TimerRepository) : ViewModel() {

    private val defaultTimer = Timer(
        id = 0,
        isRunning = false,
        startTime = System.currentTimeMillis(),
        timeRemaining = 25 * 60,
        timerType = "Pomodoro"
    )

    val timers = getAllTimers()
    val pomodoroTimers = getPomodoros()
    val breakTimers = getBreaks()

    private var _currentTimer = MutableLiveData<Timer>(timers.value?.get(0) ?: defaultTimer)
    val currentTimer: LiveData<Timer> = _currentTimer
    fun updateCurrentTimer(timer:Timer) {
        _currentTimer.value = timer
    }

//    private val _timerType = MutableLiveData<String>("Pomodoro")
//    val timerType: LiveData<String> = _timerType
//    fun updateType(newType:String) {
//        _timerType.value = newType
//    }
//
//    private val _timeRemaining = MutableLiveData<Int>(25*60)
//    val timeRemaining: LiveData<Int> = _timeRemaining
//    fun updateTimeRemaining(newTimeRemaining:Int) {
//        _timeRemaining.value = newTimeRemaining
//    }
//
//    private val _isRunning = MutableLiveData<Boolean>(false)
//    val isRunning: LiveData<Boolean> = _isRunning
//    fun updateIsRunning(newIsRunning:Boolean) {
//        _isRunning.value = newIsRunning
//    }

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