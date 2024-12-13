package com.example.cornlab.ui.detail_recom

import androidx.lifecycle.*
import com.example.cornlab.data.response.DetailResponse
import com.example.cornlab.data.response.Recommendation
import com.example.cornlab.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Response

class DetailViewModel : ViewModel() {

    private val _recomDetails = MutableLiveData<Recommendation?>()
    val recomDetails: LiveData<Recommendation?> get() = _recomDetails

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error
    fun resetError() {
        _error.value = null
    }

    fun getRecomDetails(recomId: Int) {
        viewModelScope.launch {
            try {
                // Mengambil data dari API
                val response: Response<DetailResponse> = ApiConfig.getApiService().getRecomDetails(recomId)

                if (response.isSuccessful) {
                    _recomDetails.value = response.body()?.recommendation
                } else {
                    _error.value = "Gagal memuat data: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Gagal memuat data: ${e.message}"
                _recomDetails.value = null
            }
        }
    }
}
