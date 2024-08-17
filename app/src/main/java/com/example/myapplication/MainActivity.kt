package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    private val repository: BuildingMappingRepository by lazy { BuildingMappingRepository(database.buildingMappingDao()) }
    private val buildingVm: BuildingMappingViewModel by viewModels {
        BuildingMappingViewModelFactory(repository)
    }
    private val dynamicFormVm: DynamicFormViewModel by viewModels {
        DynamicFormViewModelFactory(application)
    }

    private lateinit var createDocumentLauncher: ActivityResultLauncher<String>
    private var csvContent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        createDocumentLauncher = registerForActivityResult(CreateDocument("text/csv")) { uri ->
            uri?.let {
                csvContent?.let { content ->
                    saveCsvFileToUri(uri, content)
                }
            }
        }

        setContent {
            MyApplicationTheme {
                val mappings: State<List<BuildingMapping>> = buildingVm.mappings.observeAsState(emptyList())
                buildingVm.getAllMappings()

                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    NavHost(navController, startDestination = "main_screen") {
                        composable("main_screen") { MainScreen(innerPadding, navController, mappings) {
                            val csvContent = AppUtils.convertToCsv(mappings.value)
                            startSaveCsvFile("data.csv", csvContent)
                        }
                        }
                        composable("dynamic_form_screen") {
                            DynamicFormScreen(innerPadding, dynamicFormVm, buildingVm)
                        }
                    }


                }
            }
        }

    }

    // start the activity result launcher
    private fun startSaveCsvFile(fileName: String, content: String) {
        csvContent = content
        createDocumentLauncher.launch(fileName)
    }

    // save CSV content to the provided URI
    private fun saveCsvFileToUri(uri: Uri, csvContent: String) {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(csvContent.toByteArray())
                outputStream.flush()
            }
    }


}

@Composable
fun MainScreen(innerPadding: PaddingValues, navController: NavHostController, mappings: State<List<BuildingMapping>>, onExport: ()->Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { navController.navigate("dynamic_form_screen") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Form Screen")
        }
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            onClick = {
               onExport.invoke()
            } ,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Export Data to CSV")
        }
    }
}

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
