package com.example.alinadiplom.parse

import androidx.lifecycle.MutableLiveData
import com.example.alinadiplom.database.AnalysisResult
import com.example.alinadiplom.pojo.AnalysisInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream



class ParseExcel(private val input: InputStream?) {


    val parseResults: MutableLiveData<ArrayList<AnalysisInput>> = MutableLiveData()

    fun parseXls() {
        CoroutineScope(Dispatchers.IO).launch {
            val saveResults: ArrayList<AnalysisInput> = arrayListOf()
            val workbook = HSSFWorkbook(POIFSFileSystem(input))
            val sheet: Sheet = workbook.getSheetAt(0)
            for (i in 0 until sheet.lastRowNum) {
                sheet.getRow(i).getCell(0).cellType = 1
                sheet.getRow(i).getCell(1).cellType = 1
                val value = sheet.getRow(i).getCell(1).stringCellValue.toFloat()
                val analysisName = sheet.getRow(i).getCell(0).stringCellValue
                saveResults.add(AnalysisInput(analysisName, value, false))
            }
            parseResults.postValue(saveResults)
        }

    }

    fun parseXlsx() {
        CoroutineScope(Dispatchers.IO).launch {
            val saveResults: ArrayList<AnalysisInput> = arrayListOf()
            val workbook = XSSFWorkbook(input)
            val sheet: XSSFSheet = workbook.getSheetAt(0)
            for (i in 0 until sheet.lastRowNum) {
                sheet.getRow(i).getCell(0).cellType = 1
                sheet.getRow(i).getCell(1).cellType = 1
                val value = sheet.getRow(i).getCell(1).stringCellValue.toFloat()
                val analysisName = sheet.getRow(i).getCell(0).stringCellValue
                saveResults.add(AnalysisInput(analysisName, value, false))
            }
            parseResults.postValue(saveResults)
        }
    }
}