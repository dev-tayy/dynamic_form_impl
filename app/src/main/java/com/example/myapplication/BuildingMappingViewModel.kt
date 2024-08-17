package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch

class BuildingMappingViewModel(private val repository: BuildingMappingRepository) : ViewModel() {

    private val _mappings = MutableLiveData<List<BuildingMapping>>()
    val mappings: LiveData<List<BuildingMapping>> get() = _mappings


    fun insertBuildingMapping(buildingMapping: BuildingMapping) {
        viewModelScope.launch {
            repository.insert(buildingMapping)
        }
    }


    fun getAllMappings() {
        viewModelScope.launch {
            val allMappings = repository.getAll()
            _mappings.postValue(allMappings)
        }
    }
}

class BuildingMappingViewModelFactory(private val repository: BuildingMappingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(BuildingMappingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BuildingMappingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}