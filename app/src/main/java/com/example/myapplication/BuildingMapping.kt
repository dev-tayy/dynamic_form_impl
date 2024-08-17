package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "building_mapping")
data class BuildingMapping(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "NAME_BLD") val name: String,
    @ColumnInfo(name = "ADDRESS") val address: String?,
    @ColumnInfo(name = "OWNER") val owner: String?,
    @ColumnInfo(name = "BLD_STAT") val status: String?,
    @ColumnInfo(name = "NAM_OWN") val ownerName: String?,
    @ColumnInfo(name = "NUM_OWN") val ownerPhone: String?,
    @ColumnInfo(name = "BLD_USER") val uses: String?
)