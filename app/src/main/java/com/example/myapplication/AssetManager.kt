package com.example.myapplication

import android.content.Context
import android.util.Log
import com.google.gson.Gson

class AssetManager(private val context: Context) {
    private val gson = Gson()
    private val fileName = "BUILDING_MAPPING.json";

    /**
     * Loads and parses the JSON configuration file from the assets folder.
     * @return A FormConfig object if successful, or null if an error occurred.
     */
    fun loadFormConfig(): FormConfig? {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, FormConfig::class.java)
        } catch (exception: Exception) {
            Log.e("AssetManager", "Unexpected error", exception)
            null
        }
    }
}