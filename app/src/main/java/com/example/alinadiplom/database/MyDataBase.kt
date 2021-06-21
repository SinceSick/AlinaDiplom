package com.example.alinadiplom.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Analysis::class, AnalysisResult::class],
    version = 2,
    exportSchema = false
)
abstract class MyDataBase : RoomDatabase() {
    abstract fun myDao(): MyDao
}