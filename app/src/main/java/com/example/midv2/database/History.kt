package com.example.midv2.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History (
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    @ColumnInfo val dateTime: String,
    @ColumnInfo val goalTime: Int,
    @ColumnInfo val isSuccess: Boolean,
    @ColumnInfo val timeStamp: Long
    )