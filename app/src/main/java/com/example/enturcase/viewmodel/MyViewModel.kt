package com.example.enturcase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enturcase.data.model.Location
import com.example.enturcase.data.model.StopPlace
import com.example.enturcase.data.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MyRepository) : ViewModel() {

    private val _data = MutableLiveData<List<StopPlace>>()
    val data: LiveData<List<StopPlace>> = _data

    fun fetchData(location: Location) {
        viewModelScope.launch {
            _data.value = repository.loadStopPlacesForLocation(location)
        }
    }
}