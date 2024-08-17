package com.example.myapplication

import android.util.Log
import com.google.gson.Gson

object AppUtils {

    /**
     * Function to convert BuildingMapping list to CSV string
     * @return A CSV formatted string.
     */
    fun convertToCsv(buildingMappings: List<BuildingMapping>): String {
        val csvHeader = "ID,NAME_BLD,ADDRESS,OWNER,BLD_STAT,NAM_OWN,NUM_OWN,BLD_USER\n"
        val csvBody = buildingMappings.joinToString(separator = "\n") { mapping ->
            "${mapping.id},${mapping.name},${mapping.address ?: ""},${mapping.owner ?: ""},${mapping.status ?: ""},${mapping.ownerName ?: ""},${mapping.ownerPhone ?: ""},${mapping.uses ?: ""}"
        }
        return csvHeader + csvBody
    }


    /**
     * Loads and parses the JSON configuration file from the assets folder.
     * @return A FormConfig object if successful, or null if an error occurred.
     */
}
