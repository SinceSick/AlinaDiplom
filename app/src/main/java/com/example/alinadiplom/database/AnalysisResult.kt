package com.example.alinadiplom.database

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Entity(tableName = "analyzes_results")
data class AnalysisResult(
    var name: String,
    val value: Float?,
    @ColumnInfo(name = "analysis_name")
    val analysisName: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

