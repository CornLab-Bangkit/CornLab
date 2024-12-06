package com.example.cornlab.ui.home.husk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cornlab.data.response.RecomendationsResponse
import com.example.cornlab.data.response.ListNotesItem
import com.example.cornlab.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Response

class HuskViewModel : ViewModel() {

    private val _noteList = MutableLiveData<List<ListNotesItem>>()
    val noteList: LiveData<List<ListNotesItem>> get() = _noteList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun getHuskRecoms() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response: Response<RecomendationsResponse> = ApiConfig.getApiService().getHuskRecoms()
                _isLoading.value = false
                if (response.isSuccessful) {
                    _noteList.value = response.body()?.listNotes?.filterNotNull() ?: emptyList()

                } else {
                    _errorMessage.value = "Gagal mendapatkan data: ${response.message()}"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            }
        }
    }

}