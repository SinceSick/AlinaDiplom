package com.example.alinadiplom.chart

import com.example.alinadiplom.database.Analysis
import com.example.alinadiplom.database.AnalysisResult
import com.github.mikephil.charting.charts.CombinedChart

data class MyChart(val chart: CombinedChart,val results: ArrayList<AnalysisResult>, val analyzes: ArrayList<Analysis>) {

}