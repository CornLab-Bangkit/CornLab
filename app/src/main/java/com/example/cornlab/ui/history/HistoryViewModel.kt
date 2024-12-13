package com.example.cornlab.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.cornlab.data.local.HistoryDatabase
import com.example.cornlab.data.local.HistoryEntity

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val historyDao = HistoryDatabase.getInstance(application).historyDao()
    val historyList: LiveData<List<HistoryEntity>> = historyDao.getAllHistories()
}
