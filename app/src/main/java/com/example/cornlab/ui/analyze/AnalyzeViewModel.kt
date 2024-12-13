package com.example.cornlab.ui.analyze

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cornlab.data.response.Data

class AnalyzeViewModel : ViewModel() {

    // LiveData untuk menyimpan hasil analisis
    private val _analysisData = MutableLiveData<Data>()
    val analysisData: LiveData<Data> get() = _analysisData

    // Fungsi untuk memperbarui hasil analisis
    fun setAnalysisData(data: Data) {
        _analysisData.value = data
    }
}
