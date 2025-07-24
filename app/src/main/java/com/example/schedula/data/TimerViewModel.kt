package com.example.schedula.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimerViewModel(application: Application): AndroidViewModel(application){

    private val readAllData: LiveData<List<Timer>>
    private val repository: TimerRepository

    init {
        val timerDao = AppDatabase.getDatabase(application).timerDao()
        repository = TimerRepository(timerDao)
        readAllData = repository.readAllData
    }

    fun addTimer(timer: Timer){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertTimer(timer)
        }
    }
}