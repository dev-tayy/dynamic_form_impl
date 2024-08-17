package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.model.BuildingMapping
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.views.DynamicFormScreen
import com.example.myapplication.ui.views.MainScreen

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
                            startSaveCsvFile(csvContent)
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
    private fun startSaveCsvFile(content: String) {
        csvContent = content
        createDocumentLauncher.launch("data.csv")
    }

    // save CSV content to the provided URI
    private fun saveCsvFileToUri(uri: Uri, csvContent: String) {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(csvContent.toByteArray())
                outputStream.flush()
            }
    }

}

