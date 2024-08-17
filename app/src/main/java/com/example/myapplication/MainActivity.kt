package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.io.IOException

class MainActivity : ComponentActivity() {
    private val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    private val repository: BuildingMappingRepository by lazy { BuildingMappingRepository(database.buildingMappingDao()) }
    private val viewModel: BuildingMappingViewModel by viewModels {
        BuildingMappingViewModelFactory(repository)
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
                val mappings: State<List<BuildingMapping>> = viewModel.mappings.observeAsState(emptyList())
                viewModel.getAllMappings()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { /* Navigate to Form Screen */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Go to Form Screen")
                        }
                        Spacer(modifier = Modifier.height(50.dp))
                        Button(
                            onClick = {
                                val csvContent = CsvUtils.convertToCsv(mappings.value)
                                startSaveCsvFile("data.csv", csvContent)
                            } ,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Export Data to CSV")
                        }
                    }
                }
            }
        }

    }

    // Function to start the activity result launcher
    private fun startSaveCsvFile(fileName: String, content: String) {
        csvContent = content
        createDocumentLauncher.launch(fileName)
    }

    // Function to save CSV content to the provided URI
    private fun saveCsvFileToUri(uri: Uri, csvContent: String) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(csvContent.toByteArray())
                outputStream.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle error
        }
    }


}
