package com.example.enturcase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enturcase.utils.Logger
import com.example.enturcase.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MyRepository) : ViewModel() {

    private val _data = MutableLiveData<String>()
    val data: LiveData<String> = _data

    fun fetchData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _data.value = repository.fetchDataFromPosition(latitude, longitude)
                // Logger.debug("response: ${data.value}")
        }
    }
}