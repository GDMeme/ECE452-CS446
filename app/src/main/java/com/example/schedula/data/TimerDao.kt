package com.example.schedula.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.google.firebase.firestore.util.AsyncQueue.TimerId

@Dao
interface TimerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTimer(timer: Timer)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTimer(timer: Timer)

    @Delete
    suspend fun deleteTimer(timer: Timer)

    @Query("SELECT * FROM timer_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Timer>>
}