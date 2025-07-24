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
    fun deleteAllTimers()

    @Query("SELECT * FROM timer_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Timer>>
}