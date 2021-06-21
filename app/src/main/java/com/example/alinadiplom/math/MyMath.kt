package com.example.alinadiplom.math

import com.example.alinadiplom.database.Analysis

class MyMath {
    companion object {
        fun getCoefWithModule(analysisName: String, value: Float, analyzes: ArrayList<Analysis>): Float {

            var start = 0f
            var end = 0f
            for (obj in analyzes) {
                if (obj.name == analysisName) {
                    start = obj.greenStart
                    end = obj.greenEnd

                }
            }

            val average = (end + start) / 2f
            val module = if (value - average > 0) {
                value - average
            } else {
                (value - average) * -1
            }
            return module / (end - start)
        }

        fun getCoef(analysisName: String, value: Float, analyzes: ArrayList<Analysis>): Float {

            var start = 0f
            var end = 0f
            for (obj in analyzes) {
                if (obj.name == analysisName) {
                    start = obj.greenStart
                    end = obj.greenEnd

                }
            }

            val average = (end + start) / 2f
            val module = value - average
            return module / (end - start)
        }
    }
}