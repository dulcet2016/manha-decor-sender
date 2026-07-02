package com.manha.decorsender.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SentHistoryDao {
    @Query("SELECT * FROM sent_history ORDER BY sentAt DESC")
    fun getAllLive(): LiveData<List<SentHistory>>

    @Insert
    suspend fun insert(history: SentHistory): Long
}
