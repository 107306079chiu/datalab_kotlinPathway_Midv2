package com.example.midv2.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.midv2.database.History
import com.example.midv2.database.HistoryDao
import kotlinx.coroutines.flow.Flow

class HomeViewModel(private val historyDao: HistoryDao) : ViewModel() {

    val goalTime = MutableLiveData<Int>()

    val isSuccess = MutableLiveData<Boolean>().apply {
        //value = false
        postValue(false)
    }

    fun fullHistory(): Flow<List<History>> = historyDao.getAll()

    fun insertNew(history: History) {
        historyDao.insert(history)
    }

    fun getLatestGoal(): Int = historyDao.getLatestGoal()

    fun getTimestamp(): Long = historyDao.getTimestamp()

    fun updateSuccess() = historyDao.updateSuccess()

    fun getIsSuccess() = historyDao.getIsSuccess()

}

/*
Boiler plate code enabling DAO manipulation from ui by calling this.
 */
class HomeViewModelFactory(private val historyDao: HistoryDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(historyDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}