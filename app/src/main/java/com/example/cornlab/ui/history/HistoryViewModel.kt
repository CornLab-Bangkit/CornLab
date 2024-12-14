package com.example.cornlab.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cornlab.data.local.HistoryDatabase
import com.example.cornlab.data.local.HistoryEntity
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val historyDao = HistoryDatabase.getInstance(application).historyDao()
    val historyList: LiveData<List<HistoryEntity>> = historyDao.getAllHistories()

    fun deleteHistoryById(id: Int) {
        viewModelScope.launch {
            historyDao.deleteById(id)
        }
    }
}
