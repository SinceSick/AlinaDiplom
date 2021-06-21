package com.example.alinadiplom.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analyzes")
data class Analysis(

    val name: String,
    @ColumnInfo(name = "green_start")
    val greenStart: Float,
    @ColumnInfo(name = "green_end")
    val greenEnd: Float,

    val info: String

){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
