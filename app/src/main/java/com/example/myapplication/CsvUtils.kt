package com.example.myapplication

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

object CsvUtils {
    fun convertToCsv(buildingMappings: List<BuildingMapping>): String {
        val csvHeader = "ID,NAME_BLD,ADDRESS,OWNER,BLD_STAT,NAM_OWN,NUM_OWN,BLD_USER\n"
        val csvBody = buildingMappings.joinToString(separator = "\n") { mapping ->
            "${mapping.id},${mapping.name},${mapping.address ?: ""},${mapping.owner ?: ""},${mapping.status ?: ""},${mapping.ownerName ?: ""},${mapping.ownerPhone ?: ""},${mapping.uses ?: ""}"
        }
        return csvHeader + csvBody
    }

    @Throws(IOException::class)
    fun saveCsvFile(csvContent: String, fileName: String, context: Context): File {
        val directory = context.getExternalFilesDir(null)
            ?: throw IOException("Failed to get external files directory")

        val file = File(directory, fileName)

        try {
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos).use { osw ->
                    osw.write(csvContent)
                }
            }
        } catch (e: IOException) {
            throw IOException("Error writing CSV file", e)
        }

        return file
    }
}
