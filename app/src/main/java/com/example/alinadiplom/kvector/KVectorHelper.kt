package com.example.alinadiplom.kvector

import com.example.alinadiplom.pojo.MyEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

class KVectorHelper {
    companion object {
        fun getDataSets(graphData: ArrayList<MyEntry>): Pair<ArrayList<ILineDataSet>, ArrayList<KVectorGraphData>> {
            val sets: ArrayList<ILineDataSet> = arrayListOf()
            val dataForClick: ArrayList<KVectorGraphData> = arrayListOf()
            for (i in graphData.indices) {
                if (i == graphData.size - 1) {
                    val lineDataSet = initDataSet(graphData[i], graphData[i], graphData)
                    lineDataSet.lineWidth = 2f
                    sets.add(lineDataSet)
                } else {
                    val lineDataSet = initDataSet(graphData[i], graphData[i + 1], graphData)
                    lineDataSet.lineWidth = 2f
                    sets.add(lineDataSet)
                }
                val entry = getEntry(graphData[i], graphData)
                dataForClick.add(KVectorGraphData(entry.x, entry.y, graphData[i].name))
            }
            return Pair(sets, dataForClick)
        }

        private fun initDataSet(dot1: MyEntry, dot2: MyEntry, graphData: ArrayList<MyEntry>): LineDataSet {
            println("DOTS ${graphData.indexOf(dot1)} -- ${graphData.indexOf(dot2)}  ")
            val dataValues: ArrayList<Entry> = arrayListOf()

            dataValues.add(getEntry(dot1, graphData))
            dataValues.add(getEntry(dot2, graphData))

            return LineDataSet(dataValues, "check")
        }

        private fun getEntry(dot: MyEntry, graphData: ArrayList<MyEntry>): Entry {

            val radius = if (graphData.indexOf(dot) == 0) {
                0.0
            } else {
                graphData.indexOf(dot) / (graphData.size.toDouble())
            }
            val angle: Float

            println("wewewe,  ${graphData.indexOf(dot)}")

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

            return Entry(x.toFloat(), y.toFloat())
        }
    }
}

data class KVectorGraphData(val x: Float, val y: Float, val name: String)