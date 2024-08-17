package com.example.myapplication

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.data.model.BuildingMapping
import com.example.myapplication.data.model.FormConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DynamicFormViewModel(application: Application) : AndroidViewModel(application),
    ViewModelProvider.Factory {

    private val _formConfig = MutableLiveData<FormConfig>()
    val formConfig: LiveData<FormConfig> get() = _formConfig

    private val _formData = MutableStateFlow<Map<String, String>>(mutableMapOf())
    val formData: StateFlow<Map<String, String>> = _formData.asStateFlow()

    private val _formErrors = MutableStateFlow<Map<String, String>>(mutableMapOf())
    val formErrors: StateFlow<Map<String, String>> = _formErrors.asStateFlow()


    init {
        // Load the form configuration when the ViewModel is initialized
        loadFormConfig()
    }

    private fun loadFormConfig() {
        viewModelScope.launch {
            val json = getApplication<Application>().assets.open("BUILDING_MAPPING.json").bufferedReader().use { it.readText() }
            val config = Gson().fromJson(json, FormConfig::class.java)
            _formConfig.postValue(config)
        }
    }

    private fun validateField(fieldName: String, value: String) {
        val field = formConfig.value?.forms?.get("BUILDING_MAPPING")?.pages?.values?.first()?.fields?.get(fieldName)
        field?.let {
            val error = when {
                (it.required || (it.minLength != null && it.minLength != 0)) && value.isEmpty() -> "This field is required"
                it.minLength != null && value.length < it.minLength -> "Minimum length is ${it.minLength}"
                it.maxLength != null && value.length > it.maxLength -> "Maximum length is ${it.maxLength}"
                else -> null
            }
            _formErrors.value = _formErrors.value.toMutableMap().apply {
                if (error != null) {
                    this[fieldName] = error
                } else {
                    remove(fieldName)
                }
            }
        }
    }

    fun getFieldValue(fieldName: String): String {
        return _formData.value[fieldName] ?: ""
    }

    fun onFieldChange(fieldName: String, value: String) {
        _formData.value = _formData.value.toMutableMap().apply {
            this[fieldName] = value
        }
        validateField(fieldName, value)
    }

    fun submitForm(context: Context, buildingViewModel: BuildingMappingViewModel) {
        val buildingMapping = BuildingMapping(
            name = _formData.value["NAME OF BUILDING"]?.trim() ?: "",
            address = _formData.value["ADDRESSS OF BUILDING"]?.trim(),
            owner = _formData.value["BUILDING OWNERSHIP"]?.trim(),
            status = _formData.value["BUILDING STATUS"]?.trim(),
            ownerName = _formData.value["OWNER'S FULLNAME"]?.trim(),
            ownerPhone = _formData.value["OWNER'S PHONE NUMBER"]?.trim(),
            uses = _formData.value["BUILDING USES"]?.trim()
        )
        buildingViewModel.insertBuildingMapping(buildingMapping);
        buildingViewModel.getAllMappings();
        Toast.makeText(context, "Form submitted!", Toast.LENGTH_SHORT).show()
        _formData.value = mutableMapOf()
        _formErrors.value = mutableMapOf()
    }
}

// Factory class for creating the DynamicFormViewModel
class DynamicFormViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(DynamicFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DynamicFormViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
