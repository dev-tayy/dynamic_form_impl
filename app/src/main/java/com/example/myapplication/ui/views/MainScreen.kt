package com.example.myapplication.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.data.model.BuildingMapping


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