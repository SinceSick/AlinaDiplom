package com.example.alinadiplom.kvector

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alinadiplom.R
import com.example.alinadiplom.database.Analysis
import com.example.alinadiplom.databinding.FragmentGraphBinding
import com.example.alinadiplom.databinding.InfoDialogBinding
import com.example.alinadiplom.pojo.MyEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class KVectorGraphFragment : Fragment() {

    lateinit var binding: FragmentGraphBinding
    private lateinit var graphData: ArrayList<MyEntry>
    private lateinit var analyzes: ArrayList<Analysis>
    private val combinedData: CombinedData = CombinedData()


    companion object {
        fun instance(myEntries: ArrayList<MyEntry>, analyzes: ArrayList<Analysis>): KVectorGraphFragment {
            val fragment = KVectorGraphFragment()
            fragment.setGraphData(myEntries)
            fragment.setAnalyzes(analyzes)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGraphBinding.inflate(inflater, container, false)
        setChart()
        return binding.root
    }

    private fun setChart() {
        val sets: ArrayList<ILineDataSet> = arrayListOf()
        val dataValues: ArrayList<Entry> = arrayListOf()

        dataValues.add(Entry(0f, 0f))
        for (i in graphData.indices) {
            if (graphData[i].x > 1.5f) {
                val numerator = graphData[i].x - 1.5f
                val max = (graphData.maxOf { it.x } /*- 1.5f*/) * 2f
                val x: Float = 1.5f + (numerator / max)
                dataValues.add(Entry(x, i + 1f))
            } else {
                dataValues.add(Entry(graphData[i].x, i + 1f))
            }
        }

        val lineDataSet = LineDataSet(dataValues, "check")
        lineDataSet.setColors(ContextCompat.getColor(requireContext(), R.color.purple_500))
        lineDataSet.lineWidth = 3f
        sets.add(lineDataSet)

        val lineData = LineData(sets)
        lineData.setDrawValues(false)
        combinedData.setData(lineData)
        drawBackground(lineDataSet.xMax)
        binding.chart.data = combinedData
        binding.chart.axisLeft.axisMaximum = lineData.yMax + 1f

        binding.chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {

                val y = e?.y!!.toInt()
                if (y != 0) {
                    val currentAnalysisName = graphData[y - 1].name
                    var info = ""
                    var start = 0f
                    var end = 0f
                    var value = 0f
                    for (obj in analyzes) {
                        if (obj.name == currentAnalysisName) {
                            info = obj.info
                            start = obj.greenStart
                            end = obj.greenEnd
                            value = graphData[y - 1].value
                        }
                    }

                    showResultInfoDialog(info, start, end, value)
                }
            }

            override fun onNothingSelected() {}
        })
        binding.chart.invalidate()
    }

    private fun drawBackground(max: Float) {
        val maxInt = if ((max + 1).toInt() < 4) {
            4
        } else {
            (max + 1).toInt() * 2
        }
        val barSets: ArrayList<IBarDataSet> = arrayListOf()

        for (i in 0 until maxInt) {
            val color = when (i) {
                0 -> ContextCompat.getColor(requireContext(), R.color.green)
                1 -> ContextCompat.getColor(requireContext(), R.color.yellow)
                2 -> ContextCompat.getColor(requireContext(), R.color.orange)
                3 -> ContextCompat.getColor(requireContext(), R.color.red)
                else -> ContextCompat.getColor(requireContext(), R.color.red)
            }
            val barDataValues: ArrayList<BarEntry> = arrayListOf(
                BarEntry(i.toFloat(), 100f)
            )
            val barSet = BarDataSet(barDataValues, "$i")
            barSet.setColors(color)
            barSets.add(barSet)
        }

        val barData = BarData(barSets)
        barData.barWidth = 0.5f

        combinedData.setData(barData)
        combinedData.barData.groupBars(0f, 0f, 0f)
        binding.chart.data = combinedData
        binding.chart.xAxis.axisMaximum = 2f
        binding.chart.description.isEnabled = false
        binding.chart.legend.isEnabled = false
        binding.chart.xAxis.setDrawGridLines(false)
        binding.chart.axisLeft.setDrawGridLines(false)
        binding.chart.axisRight.setDrawGridLines(false)
        binding.chart.axisLeft.gridLineWidth = 0f
        binding.chart.axisRight.gridLineWidth = 0f
        binding.chart.axisLeft.axisMinimum = 0f
        binding.chart.axisLeft.axisMaximum = binding.chart.lineData.yMax + 1f
        binding.chart.axisRight.isEnabled = false
        binding.chart.axisLeft.isEnabled = false
        binding.chart.xAxis.isEnabled = false
    }

    private fun showResultInfoDialog(info: String, start: Float, end: Float, value: Float) {
        val dialog = Dialog(requireContext())
        val dialogBinding = InfoDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        val width: Int = requireContext().resources.displayMetrics.widthPixels
        val height: Int = (requireContext().resources.displayMetrics.heightPixels * 0.4).toInt()
        dialog.window?.setLayout(width, height)
        dialog.show()

        var startString = start.toString()
        startString += " "
        dialogBinding.infoTv.text = info
        dialogBinding.normStartTv.text = startString
        dialogBinding.normEndTv.text = end.toString()
        dialogBinding.valueTv.text = value.toString()
    }

    fun setGraphData(myEntries: ArrayList<MyEntry>) {
        this.graphData = myEntries
    }

    fun setAnalyzes(analyzes: ArrayList<Analysis>) {
        this.analyzes = analyzes
    }

}