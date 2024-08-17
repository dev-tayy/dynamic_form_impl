package com.example.myapplication.ui.views

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.BuildingMappingViewModel
import com.example.myapplication.DynamicFormViewModel
import com.example.myapplication.data.model.Field


@Composable
fun DynamicFormScreen(
    innerPadding: PaddingValues,
    viewModel: DynamicFormViewModel,
    buildingVm: BuildingMappingViewModel,
) {
    val context = LocalContext.current
    val formConfig by viewModel.formConfig.observeAsState()
    val formData by viewModel.formData.collectAsState()
    val formErrors by viewModel.formErrors.collectAsState()

    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        formConfig?.let { config ->
            config.forms["BUILDING_MAPPING"]?.pages?.values?.forEach { page ->
                page.fields.forEach { (fieldName, field) ->
                    item {
                        when (field.uiType) {
                            "text_field" -> {
                                TextField(
                                    value = formData[fieldName] ?: "",
                                    onValueChange = { viewModel.onFieldChange(fieldName, it) },
                                    label = { Text(fieldName) },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Next
                                    ),
                                    isError = formErrors[fieldName] != null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                )
                                formErrors[fieldName]?.let { error ->
                                    Text(
                                        text = error,
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                            "drop_down" -> {
                                DropDownField(fieldName, field, viewModel)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            item {
                Button(
                    onClick = { viewModel.submitForm(context, buildingVm) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formErrors.isEmpty() && formData.isNotEmpty() && !formData["OWNER'S PHONE NUMBER"].isNullOrEmpty()) {
                    Text("Submit")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        } ?: item {
            Text("Loading form...", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
        }
    }
}



@Composable
fun DropDownField(fieldName: String, field: Field, viewModel: DynamicFormViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = viewModel.getFieldValue(fieldName)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(fieldName, style = MaterialTheme.typography.labelMedium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .clickable { expanded = true }
        ) {
            Text(
                text = selectedOption.ifEmpty { "Select an option" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                field.values?.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            viewModel.onFieldChange(fieldName, option)
                            expanded = false
                        })
                }
            }
        }
    }
}
