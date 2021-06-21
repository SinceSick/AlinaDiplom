package com.example.alinadiplom.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface MyDao {
    @Query("select * from analyzes")
    fun getAnalyzes(): List<Analysis>

    @Insert
    fun insertAnalyzes(analyzes: List<Analysis>)

    @Query("select * from analyzes_results")
    fun getAnalyzesResults(): List<AnalysisResult>

    @Insert
    fun insertResults(analyzesResults: List<AnalysisResult>)
    //@Query("select * from results")
}