package com.example.cornlab.ui.detail

import androidx.lifecycle.*
import com.example.cornlab.data.response.DetailResponse
import com.example.cornlab.data.response.Recom
import com.example.cornlab.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Response

class DetailViewModel : ViewModel() {

    private val _recomDetails = MutableLiveData<Recom?>()
    val recomDetails: LiveData<Recom?> get() = _recomDetails

    fun getRecomDetails(recomId: Int) {
        viewModelScope.launch {
            try {
                // Mengambil data dari API
                val response: Response<DetailResponse> = ApiConfig.getApiService().getRecomDetails(recomId)

                if (response.isSuccessful) {
                    // Akses 'event' yang berisi data 'Recom'
                    _recomDetails.value = response.body()?.event
                }
            } catch (e: Exception) {
                // Handle error, log if needed
                _recomDetails.value = null
            }
        }
    }
}