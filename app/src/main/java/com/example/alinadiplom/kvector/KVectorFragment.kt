package com.example.alinadiplom.kvector

import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alinadiplom.R
import com.example.alinadiplom.database.Analysis
import com.example.alinadiplom.databinding.FragmentKvectorBinding
import com.example.alinadiplom.databinding.InfoDialogBinding
import com.example.alinadiplom.pojo.MyEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class KVectorFragment : Fragment() {

    lateinit var binding: FragmentKvectorBinding
    private lateinit var graphData: ArrayList<MyEntry>
    private lateinit var analyzes: ArrayList<Analysis>

    companion object {
        fun instance(myEntries: ArrayList<MyEntry>, analyzes: ArrayList<Analysis>): KVectorFragment {
            val fragment = KVectorFragment()
            fragment.setGraphData(myEntries)
            fragment.setAnalyzes(analyzes)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentKvectorBinding.inflate(layoutInflater)
        setChart()

        return binding.root
    }

    private fun setChart() {
        val (sets, kVectorGraphData) = KVectorHelper.getDataSets(graphData)
        sets.forEach {
            (it as LineDataSet).setColors(ContextCompat.getColor(requireContext(), R.color.purple_500))
        }

        val lineData = LineData(sets)
        lineData.setDrawValues(false)
        binding.lineChart.data = lineData
        binding.lineChart.xAxis.axisMinimum = -1f
        binding.lineChart.xAxis.axisMaximum = 1f
        binding.lineChart.axisLeft.axisMaximum = 1f
        binding.lineChart.axisLeft.axisMinimum = -0f

        binding.lineChart.layoutParams.width = requireActivity().resources.displayMetrics.widthPixels
        binding.lineChart.layoutParams.height = requireActivity().resources.displayMetrics.widthPixels / 2

        val startWidth = resources.displayMetrics.widthPixels
        println("StartWidth = $startWidth")
        val deltaInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f, resources.displayMetrics).toInt()
        binding.backgroundImg.layoutParams.width = startWidth - deltaInPx
        println("NewWidth = ${startWidth - deltaInPx}")
        binding.backgroundImg.layoutParams.height = (startWidth - deltaInPx) / 2
        println("NewHeight = ${(startWidth - deltaInPx) / 2}")

        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.axisLeft.isEnabled = false
        binding.lineChart.xAxis.isEnabled = false
        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        binding.lineChart.description.isEnabled = false
        binding.lineChart.legend.isEnabled = false
        binding.lineChart.xAxis.setDrawGridLines(false)
        binding.lineChart.axisLeft.setDrawGridLines(false)
        binding.lineChart.axisRight.setDrawGridLines(false)
        binding.lineChart.setScaleEnabled(false)

        binding.lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {

                h?.let {

                    println("wewewe  ${it.x}  in  $graphData")
                    val curData = kVectorGraphData.filter { graphData ->
                        graphData.x == it.x && graphData.y == it.y
                    }
                    val name = curData[0].name

                    val curGraphData = graphData.filter { graphData ->
                        graphData.name == name
                    }
                    val value = curGraphData[0].value

                    analyzes.forEach { analyse ->
                        if (analyse.name == name)
                            showResultInfoDialog(analyse.info, analyse.greenStart, analyse.greenEnd, value)
                    }
                }
            }

            override fun onNothingSelected() {}
        })

        binding.lineChart.invalidate()

    }

    fun setGraphData(myEntries: ArrayList<MyEntry>) {
        this.graphData = myEntries
        this.graphData.add(0, MyEntry(0f, 0f, "", 0f))
    }

    fun setAnalyzes(analyzes: ArrayList<Analysis>) {
        this.analyzes = analyzes
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
}