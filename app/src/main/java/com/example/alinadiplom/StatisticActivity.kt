package com.example.alinadiplom

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.alinadiplom.database.Analysis
import com.example.alinadiplom.database.AnalysisResult
import com.example.alinadiplom.database.MyDao
import com.example.alinadiplom.database.MyDataBase
import com.example.alinadiplom.databinding.ActivityMainBinding
import com.example.alinadiplom.databinding.ActivityStatisticBinding
import com.example.alinadiplom.databinding.InfoDialogBinding
import com.example.alinadiplom.pojo.MyEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class StatisticActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticBinding
    private lateinit var analyzes: ArrayList<Analysis>
    private lateinit var dao: MyDao
    private lateinit var results: ArrayList<AnalysisResult>
    private lateinit var combinedData: CombinedData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationView()
        getAnalyzesFromDataBase()
        setExampleChart()
        initLoadSpinner()
    }

    private fun initNavigationView() {
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.what_is -> showInfoDialog(R.layout.what_is_dialog)
                R.id.how_build_norms -> showInfoDialog(R.layout.how_build_norms_dialog)
                R.id.what_color_means -> showInfoDialog(R.layout.what_color_means_dialog)
                R.id.how_often -> showInfoDialog(R.layout.how_often_dialog)
                R.id.red_zone -> showInfoDialog(R.layout.red_zone_dialog)
                R.id.statistic -> binding.drawerLayout.closeDrawers()
                R.id.k_vector -> startActivity(Intent(this, MainActivity::class.java))
            }

            return@setNavigationItemSelectedListener true
        }
    }

    private fun showInfoDialog(id: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(id)
        val width: Int = applicationContext.resources.displayMetrics.widthPixels
        val height: Int = (applicationContext.resources.displayMetrics.heightPixels * 0.85).toInt()
        dialog.window?.setLayout(width, height)
        dialog.show()
    }

    private fun getAnalyzesFromDataBase() {
        val db = Room.databaseBuilder(this, MyDataBase::class.java, "my_database")
            .allowMainThreadQueries()
            .build()
        dao = db.myDao()
        analyzes = dao.getAnalyzes() as ArrayList<Analysis>

        if (analyzes.size == 0) {
            dao.insertAnalyzes(MainAnalyzes.list)
            analyzes = dao.getAnalyzes() as ArrayList<Analysis>
        }
    }

    private fun setExampleChart() {
        combinedData = CombinedData()

        val barSets: ArrayList<IBarDataSet> = arrayListOf()
        for (i in 0 until 4) {
            val color = when (i) {
                0 -> ContextCompat.getColor(this, R.color.green)
                1 -> ContextCompat.getColor(this, R.color.yellow)
                2 -> ContextCompat.getColor(this, R.color.orange)
                3 -> ContextCompat.getColor(this, R.color.red)
                else -> ContextCompat.getColor(this, R.color.red)
            }
            val barDataValues: ArrayList<BarEntry> = arrayListOf(
                BarEntry(i.toFloat(), 100f)
            )
            val barSet = BarDataSet(barDataValues, "$i")
            barSet.setColors(color)
            barSet.barBorderColor = ContextCompat.getColor(this, R.color.red)
            barSets.add(barSet)
        }
        val barData = BarData(barSets)
        barData.barWidth = 0.5f

        combinedData.setData(barData)

        combinedData.barData.groupBars(0f, 0f, 0f)

        binding.chart.data = combinedData
        binding.chart.axisLeft.axisMinimum = 0f
        binding.chart.axisRight.isEnabled = false
        binding.chart.axisLeft.isEnabled = false
        binding.chart.xAxis.isEnabled = false
        binding.chart.legend.isEnabled = false
        binding.chart.setDrawGridBackground(false)

        val xAxis = binding.chart.xAxis
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 2f
        xAxis.isGranularityEnabled = true
        xAxis.granularity = barData.barWidth
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        binding.chart.description.isEnabled = false
    }

    private fun initLoadSpinner() {
        results = dao.getAnalyzesResults() as ArrayList<AnalysisResult>
        val set = LinkedHashSet<String>()
        for (i in 0 until results.size) {
            set.add(results[i].analysisName)
        }
        val analyzesNames = ArrayList<String>(set)
        binding.loadSpinner.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, analyzesNames)

        binding.loadSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                setChartData(analyzesNames[position])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.loadSpinner.setSelection(analyzesNames.size - 1)
    }

    private fun setChartData(name: String) {
        val resultForChart: ArrayList<AnalysisResult> = arrayListOf()
        for (obj in results) {
            if (obj.analysisName == name) {
                resultForChart.add(obj)
            }
        }

        val sets: ArrayList<ILineDataSet> = arrayListOf()

        val dataValues: ArrayList<Entry> = arrayListOf()
        val myEntries: ArrayList<MyEntry> = arrayListOf()
        for (i in 0 until resultForChart.size) {
            val value = resultForChart[i].value
            if (value != null) {
                myEntries.add(MyEntry(calculateX(resultForChart[i]), i + 1f, resultForChart[i].analysisName, value))
            }
        }

        myEntries.sortBy { it.x }
        val names: ArrayList<String> = arrayListOf()
        names.add("")
        dataValues.add(Entry(0f, 0f))
        for (i in myEntries.indices) {
            dataValues.add(Entry(myEntries[i].x, i + 1f))
            names.add(myEntries[i].name)
        }

        val lineDataSet = LineDataSet(dataValues, "check")
        lineDataSet.setColors(ContextCompat.getColor(this, R.color.purple_500))
        lineDataSet.lineWidth = 3f
        sets.add(lineDataSet)

        val lineData = LineData(sets)
        lineData.setDrawValues(false)
        combinedData.setData(lineData)
        drawBackground(lineDataSet.xMax)
        binding.chart.data = combinedData
        binding.chart.axisLeft.axisMaximum = 2f

        binding.chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {

                val y = e?.y!!.toInt()
                if (y != 0) {
                    val currentAnalysisName = names[y]
                    var info = ""
                    var start = 0f
                    var end = 0f
                    var value = 0f
                    for (obj in analyzes) {
                        if (obj.name == currentAnalysisName) {
                            info = obj.info
                            start = obj.greenStart
                            end = obj.greenEnd
                            value = myEntries[y - 1].value
                        }
                    }

                    showResultInfoDialog(info, start, end, value)
                }
            }

            override fun onNothingSelected() {
            }
        })

        binding.chart.invalidate()
    }

    private fun calculateX(result: AnalysisResult): Float {
        val value = result.value
        val analysisName = result.analysisName
        var start = 0f
        var end = 0f
        for (obj in analyzes) {
            if (obj.name == analysisName) {
                start = obj.greenStart
                end = obj.greenEnd
            }
        }
        val average = (end + start) / 2f
        val module = if (value!! - average > 0) {
            value - average
        } else {
            (value - average) * -1
        }
        return module / (end - start)
    }

    private fun drawBackground(max: Float) {
        val maxInt = if ((max + 1).toInt() < 2) {
            5
        } else {
            (max + 1).toInt() * 2
        }
        val barSets: ArrayList<IBarDataSet> = arrayListOf()

        for (i in 0 until maxInt) {
            val color = when (i) {
                0 -> ContextCompat.getColor(this, R.color.green)
                1 -> ContextCompat.getColor(this, R.color.yellow)
                2 -> ContextCompat.getColor(this, R.color.orange)
                3 -> ContextCompat.getColor(this, R.color.red)
                else -> ContextCompat.getColor(this, R.color.red)
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

        binding.chart.invalidate()

    }

    private fun showResultInfoDialog(info: String, start: Float, end: Float, value: Float) {
        val dialog = Dialog(this)
        val dialogBinding = InfoDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        val width: Int = applicationContext.resources.displayMetrics.widthPixels
        val height: Int = (applicationContext.resources.displayMetrics.heightPixels * 0.4).toInt()
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