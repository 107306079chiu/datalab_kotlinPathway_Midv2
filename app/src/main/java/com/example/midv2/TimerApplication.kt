package com.example.midv2

import android.app.Application
import com.example.midv2.database.AppDatabase

class TimerApplication: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}