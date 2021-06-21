package com.example.alinadiplom.kvector

import com.example.alinadiplom.pojo.MyEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

class KVectorHelper {
    companion object {
        fun initDataSet(dot1: MyEntry, dot2: MyEntry, graphData: ArrayList<MyEntry>): LineDataSet {
            println("DOTS ${graphData.indexOf(dot1)} -- ${graphData.indexOf(dot2)}  ")
            val dataValues: ArrayList<Entry> = arrayListOf()

            dataValues.add(getEntry(dot1,graphData))
            dataValues.add(getEntry(dot2,graphData))

            return LineDataSet(dataValues, "check")
        }

        fun getEntry(dot: MyEntry, graphData: ArrayList<MyEntry>): Entry{

            val radius: Double = (graphData.indexOf(dot) + 1.toDouble()) / (graphData.size.toDouble() + 1)
            val angle: Float

            if (dot.x > 1.5f || dot.x < -1.5f) {

                val numerator = if (dot.x > 0) {
                    dot.x - 1.5f
                } else {
                    -1 * dot.x - 1.5f
                }

                val max = graphData.maxOf {
                    it.x.absoluteValue
                }

                val denominator = (max) * 2f
                val x: Float = 1.5f + (numerator / denominator)
                angle = (x * (Math.PI / 2f)).toFloat()
            } else {
                angle = (dot.x * Math.PI / 2).toFloat()
            }

            val x = radius * cos(angle)
            var y = radius * sin(angle)
            if (y < 0) y *= -1
            println("$x, $y")

            return Entry(x.toFloat(),y.toFloat())
        }
    }
}