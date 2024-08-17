package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BuildingMappingDao {

    /**
     * Inserts a new building mapping into the database.
     *
     * @param buildingMapping The BuildingMapping object to insert.
     * @return The ID of the newly inserted building mapping.
     */
    @Insert
    suspend fun insertBuildingMapping(buildingMapping: BuildingMapping): Long

    /**
     * Retrieves all building mappings from the database.
     *
     * @return A list of all BuildingMapping objects.
     */
    @Query("SELECT * FROM building_mapping")
    suspend fun getAllMappings(): List<BuildingMapping>
}
