package com.example.myapplication

object CsvUtils {
    // Function to convert BuildingMapping list to CSV string
    fun convertToCsv(buildingMappings: List<BuildingMapping>): String {
        val csvHeader = "ID,NAME_BLD,ADDRESS,OWNER,BLD_STAT,NAM_OWN,NUM_OWN,BLD_USER\n"
        val csvBody = buildingMappings.joinToString(separator = "\n") { mapping ->
            "${mapping.id},${mapping.name},${mapping.address ?: ""},${mapping.owner ?: ""},${mapping.status ?: ""},${mapping.ownerName ?: ""},${mapping.ownerPhone ?: ""},${mapping.uses ?: ""}"
        }
        return csvHeader + csvBody
    }
}
