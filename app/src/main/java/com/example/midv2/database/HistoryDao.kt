package com.example.midv2.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY dateTime DESC")
    fun getAll(): Flow<List<History>>

    @Insert
    fun insert(item: History)

    @Query("SELECT goalTime FROM history WHERE ID = (SELECT MAX(ID) FROM history)")
    fun getLatestGoal(): Int

    @Query("SELECT timeStamp FROM history WHERE ID = (SELECT MAX(ID) FROM history)")
    fun getTimestamp(): Long

    @Query("UPDATE history SET isSuccess = 1 WHERE ID = (SELECT MAX(ID) FROM history)")
    fun updateSuccess()

    @Query("SELECT isSuccess FROM history WHERE ID = (SELECT MAX(ID) FROM history)")
    fun getIsSuccess(): Boolean
}