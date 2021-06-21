package com.example.alinadiplom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alinadiplom.database.Analysis
import com.example.alinadiplom.database.AnalysisResult
import com.example.alinadiplom.database.MyDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    val analyzes: MutableLiveData<ArrayList<Analysis>> = MutableLiveData()
    val results: MutableLiveData<ArrayList<AnalysisResult>> = MutableLiveData()

    fun getAnalyzes(dao: MyDao){
        CoroutineScope(Dispatchers.IO).launch{
            analyzes.postValue(dao.getAnalyzes() as ArrayList<Analysis>)
        }
    }

    fun insertAnalyzes(dao: MyDao,analyzes: ArrayList<Analysis>){
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertAnalyzes(analyzes)
        }
    }

    fun getResults(dao: MyDao){
        CoroutineScope(Dispatchers.IO).launch {
            results.postValue(dao.getAnalyzesResults() as ArrayList<AnalysisResult>)
        }
    }

    fun insertResults(dao: MyDao, results: ArrayList<AnalysisResult>){
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertResults(results)

        }
    }

    fun getFile(){

    }
}