package com.example.alinadiplom.parse

import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import com.example.alinadiplom.database.Analysis
import com.example.alinadiplom.math.MyMath
import com.example.alinadiplom.parse.pojo.ParseInfo
import com.example.alinadiplom.pojo.AnalysisInput
import com.example.alinadiplom.parse.pojo.ParseValue
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

class ParseImage(private val input: InputStream?, private val analyzes: ArrayList<Analysis>) {
    val inputArray: MutableLiveData<ArrayList<AnalysisInput>> = MutableLiveData()
    private var allCorrectNames: ArrayList<String> = arrayListOf()
    private var allCorrectValues: ArrayList<ParseValue> = arrayListOf()
    private var minIndex: Int = 0

    fun parse() {
        CoroutineScope(Dispatchers.IO).launch {
            val bitmapImage = BitmapFactory.decodeStream(input)
            val image = InputImage.fromBitmap(bitmapImage, 0)
            val recognizer = TextRecognition.getClient()
            val result = recognizer.process(image).addOnSuccessListener {
                findAllCorrectNames(it)
                findAllCorrectValues(it)

                val filteredNames = filterNames(allCorrectNames)
                if (filteredNames.isNotEmpty() && allCorrectValues.isNotEmpty()) {
                    setValueToNames(filteredNames, allCorrectValues)
                } else{
                    val empty: ArrayList<AnalysisInput> = arrayListOf()
                    inputArray.postValue(empty)
                }
            }
        }
    }

    private fun checkNumOfDot(word: String): Int {
        var num = 0
        for (i in word.indices) {
            if (word[i] == '.' || word[i] == ',') {
                num++
            }
        }
        return num
    }

    private fun findAllCorrectNames(it: Text) {
        val recognizer = TextRecognition.getClient()
        for (blocks in it.textBlocks) {
            for (lines in blocks.lines) {
                for (elements in lines.elements) {
                    val word = elements.text
                    if (isNameCorrect(word)) {
                        allCorrectNames.add(word)
                        println("Correct name = $word")

                    }
                }
            }
        }


        println("Нашел все названия")
    }

    private fun isNameCorrect(word: String): Boolean {
        var size = 0
        for (i in word.indices) {
            if ((word[i] in 'A'..'Z') || word[i] == '-') {
                if (word[i] == '-' && (i + 1) == word.length) {
                    size--
                }
                size++
            }
        }
        return size == word.length
    }

    private fun findAllCorrectValues(it: Text) {
        for (blocks in it.textBlocks) {
            for (lines in blocks.lines) {
                for (elements in lines.elements) {
                    val word = elements.text
                    val parseValue = isValueCorrect(word)
                    if (parseValue != null) {
                        allCorrectValues.add(parseValue)
                        println("Correct value = ${parseValue.value}")
                    }
                }
            }
        }
        println("Нашел все значения")
    }

    private fun isValueCorrect(word: String): ParseValue? {
        var valueString: String = word
        var size = 0
        var question = false
        for (i in word.indices) {
            if (checkNumOfDot(valueString) in 0..1) {
                if (((valueString[i] in '0'..'9') || valueString[i] == '.') && valueString.length > 1) {
                    if (valueString[i] == '.' && (i + 1) == valueString.length) {
                        valueString += '0'
                        size++
                        question = true
                    }
                    size++
                }
            }
        }
        println("")
        return if (size == valueString.length) return ParseValue(valueString.toFloat(), question)
        else null
    }

    private fun filterNames(parseNames: ArrayList<String>): ArrayList<String> {

        val filterParseNames: ArrayList<String> = arrayListOf()
        for (i in parseNames.indices) {
            for (j in analyzes.indices) {
                if (parseNames[i] == analyzes[j].name) {
                    filterParseNames.add(parseNames[i])

                }
            }
        }
        println("Фильтранул названия")
        return filterParseNames
    }

    private fun setValueToNames(names: ArrayList<String>, values: ArrayList<ParseValue>) {
        val math = MyMath()
        val tempValues = values
        val tempArray: ArrayList<AnalysisInput> = arrayListOf()

        for (i in names.indices) {
            val currentAnalysisInfo: ArrayList<ParseInfo> = arrayListOf()

            for (j in tempValues.indices) {

                val coef = MyMath.getCoef(names[i], tempValues[j].value, analyzes)
                val name = names[i]
                val value = tempValues[j].value
                val question = tempValues[j].question
                if(value == 278f){
                    println("name = $name  coef = $coef")
                }
                currentAnalysisInfo.add(ParseInfo(name, value, coef, question))
            }
            tempArray.add(findMinimum(currentAnalysisInfo))
            tempValues.removeAt(minIndex)
        }

        for (obj in tempArray){
            println("name = ${obj.name}  value${obj.value}")
        }
        inputArray.postValue(tempArray)

    }

    private fun findMinimum(parseInfo: ArrayList<ParseInfo>): AnalysisInput {
        val analysisInput = AnalysisInput("", null, false)
        var minCoef = 999999999f

        for (i in parseInfo.indices) {
            if (parseInfo[i].coef < minCoef) {
                analysisInput.name = parseInfo[i].name
                analysisInput.value = parseInfo[i].value
                analysisInput.question = parseInfo[i].question
                minCoef = parseInfo[i].coef
                minIndex = i
            }
        }
        println("Нашел минимум")
        return analysisInput
    }
}