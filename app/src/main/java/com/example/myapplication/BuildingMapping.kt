package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "building_mapping")
data class BuildingMapping(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name_bld") val name: String,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "owner") val owner: String?,
    @ColumnInfo(name = "bld_stat") val status: String?,
    @ColumnInfo(name = "nam_own") val ownerName: String?,
    @ColumnInfo(name = "num_own") val ownerPhone: String?,
    @ColumnInfo(name = "bld_use") val uses: String?
)