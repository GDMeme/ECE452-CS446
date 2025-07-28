package com.example.schedula.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface TimerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTimer(timer: Timer)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTimer(timer: Timer)

    @Delete
    suspend fun deleteTimer(timer: Timer)

    @Query("DELETE FROM timer_table")
    suspend fun deleteAllTimers()

    @Query("SELECT * FROM timer_table ORDER BY id ASC")
    fun getAllTimers(): LiveData<List<Timer>>

    @Query("SELECT * FROM timer_table WHERE timerType == 'Pomodoro' ORDER BY id ASC")
    fun getPomodoros(): LiveData<List<Timer>>

    @Query("SELECT * FROM timer_table WHERE timerType == 'Break' ORDER BY id ASC")
    fun getBreaks(): LiveData<List<Timer>>

    @Query("SELECT * FROM timer_table WHERE id = :id")
    fun getTimerById(id: Int): LiveData<Timer>

}