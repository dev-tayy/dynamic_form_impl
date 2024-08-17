package com.example.myapplication

import com.example.myapplication.data.model.BuildingMapping

class BuildingMappingRepository(private val buildingMappingDao: BuildingMappingDao) {

    /**
     * Inserts a new building mapping into the database.
     *
     * @param buildingMapping The BuildingMapping object to insert.
     * @return The ID of the newly inserted building mapping.
     */
    suspend fun insert(buildingMapping: BuildingMapping): Long {
        return buildingMappingDao.insertBuildingMapping(buildingMapping)
    }

    /**
     * Retrieves all building mappings from the database.
     *
     * @return A list of all BuildingMapping objects.
     */
    suspend fun getAll(): List<BuildingMapping> {
        return buildingMappingDao.getAllMappings()
    }
}
