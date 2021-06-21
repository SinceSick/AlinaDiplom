package com.example.alinadiplom


import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.alinadiplom.adapter.AnalyzesListAdapter
import com.example.alinadiplom.adapter.FragmentsAdapter
import com.example.alinadiplom.adapter.ResultsListAdapter
import com.example.alinadiplom.database.Analysis
import com.example.alinadiplom.database.AnalysisResult
import com.example.alinadiplom.database.MyDao
import com.example.alinadiplom.database.MyDataBase
import com.example.alinadiplom.databinding.ActivityMainBinding
import com.example.alinadiplom.databinding.InputDialogBinding
import com.example.alinadiplom.math.MyMath
import com.example.alinadiplom.parse.ParseExcel
import com.example.alinadiplom.parse.ParseImage
import com.example.alinadiplom.pojo.AnalysisInput
import com.example.alinadiplom.pojo.MyEntry
import com.github.mikephil.charting.data.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet

class MainActivity : AppCompatActivity(), ResultsListAdapter.ResultListener {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var analyzes: ArrayList<Analysis>
    private lateinit var dao: MyDao
    private lateinit var results: ArrayList<AnalysisResult>
    private lateinit var names: ArrayList<String>

    private var adapterRV: ResultsListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.resultsRV.layoutManager = LinearLayoutManager(this)

        setMyProperties()
        initNavigationView()
        getAnalyzes()
        getResults()

        viewModel.analyzes.observe(this) {
            if (it.size == 0) {
                analyzes = MainAnalyzes.list
                viewModel.insertAnalyzes(dao, analyzes)
            } else {
                analyzes = it
            }
        }

        viewModel.results.observe(this) {
            results = it
            updateResultsList()
        }

        binding.resultsLyt.setOnClickListener {
            if (binding.viewPager.visibility == View.VISIBLE) {
                binding.viewPager.visibility = View.GONE
                binding.resultsRV.visibility = View.VISIBLE
                binding.resultNameLabel.visibility = View.GONE
            } else {
                binding.viewPager.visibility = View.VISIBLE
                binding.resultsRV.visibility = View.GONE
                binding.resultNameLabel.visibility = View.VISIBLE

            }

        }
    }

    private fun setMyProperties() {
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLInputFactory",
            "com.fasterxml.aalto.stax.InputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        )
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
                R.id.k_vector -> binding.drawerLayout.closeDrawers()
                R.id.statistic -> startActivity(Intent(this, StatisticActivity::class.java))
            }

            return@setNavigationItemSelectedListener true
        }
    }

    private fun getAnalyzes() {
        val db = Room.databaseBuilder(this, MyDataBase::class.java, "my_database").build()
        dao = db.myDao()

        viewModel.getAnalyzes(dao)
    }

    private fun getResults() {
        viewModel.getResults(dao)
    }

    private fun updateResultsList() {

        val set = LinkedHashSet<String>()
        for (i in results.indices) {
            set.add(results[i].name)
        }
        names = ArrayList(set)
        println(names)

        if (adapterRV != null) {
            adapterRV?.results = names
            adapterRV?.notifyDataSetChanged()
        } else {
            adapterRV = ResultsListAdapter(this, names)
            binding.resultsRV.adapter = adapterRV
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

    fun showInputDialog(view: View) {
        binding.inputByUserFab.visibility = View.GONE
        binding.loadFromFileFab.visibility = View.GONE
        binding.addFab.setImageResource(R.drawable.add_image)

        val dialog = Dialog(this)
        val dialogBinding = InputDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        val width: Int = applicationContext.resources.displayMetrics.widthPixels
        val height: Int = (applicationContext.resources.displayMetrics.heightPixels * 0.75).toInt()
        dialog.window?.setLayout(width, height)

        val inputAnalyzes: ArrayList<AnalysisInput> = arrayListOf()

        for (i in 0 until analyzes.size) {
            inputAnalyzes.add(AnalysisInput(analyzes[i].name, null, false))
        }

        val adapter = AnalyzesListAdapter(inputAnalyzes)
        dialogBinding.inputListRv.layoutManager = LinearLayoutManager(this)
        dialogBinding.inputListRv.adapter = adapter
        dialogBinding.inputListRv.setItemViewCacheSize(inputAnalyzes.size)
        dialogBinding.inputListRv.setHasFixedSize(true)

        dialog.show()

        dialogBinding.saveBtn.setOnClickListener {

            val array = adapter.analyzesInput
            val name = dialogBinding.resultNameEt.text.toString()

            if (checkName(results, name)) {
                saveInDataBase(array, name)
                dialog.hide()
            } else {
                if (results.isNotEmpty() && name.isNotEmpty()) {
                    dialogBinding.resultNameEt.error = "Данное имя уже используется"
                } else if (name.isEmpty()) {
                    dialogBinding.resultNameEt.error = "Введите название результата"
                }
                dialogBinding.resultNameEt.requestFocus()
            }
        }
    }

    private fun showInputDialog(inputArray: ArrayList<AnalysisInput>) {
        Toast.makeText(this, "Обязательно проверьте результаты перед сохранением!", Toast.LENGTH_LONG).show()
        val dialog = Dialog(this)
        val dialogBinding = InputDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        val width: Int = applicationContext.resources.displayMetrics.widthPixels
        val height: Int = (applicationContext.resources.displayMetrics.heightPixels * 0.75).toInt()
        dialog.window?.setLayout(width, height)
        dialog.show()

        if (inputArray.isEmpty()) {
            Toast.makeText(this, "Невозможно прочитать информацию, выберите другой файл или заполните вручную", Toast.LENGTH_LONG).show()
        }


        val arrayForAdapter: ArrayList<AnalysisInput> = arrayListOf()
        for (i in analyzes.indices) {
            arrayForAdapter.add(AnalysisInput(analyzes[i].name, null, false))
        }

        for (i in inputArray.indices) {
            for (j in arrayForAdapter.indices) {
                if (inputArray[i].name == arrayForAdapter[j].name) {
                    arrayForAdapter[j].name = inputArray[i].name
                    arrayForAdapter[j].value = inputArray[i].value
                    arrayForAdapter[j].question = inputArray[i].question
                }
            }
        }

        println("Input size = ${inputArray.size}  For adapter = ${arrayForAdapter.size}")

        val adapter = AnalyzesListAdapter(arrayForAdapter)
        dialogBinding.inputListRv.layoutManager = LinearLayoutManager(this)
        dialogBinding.inputListRv.adapter = adapter
        dialogBinding.inputListRv.setItemViewCacheSize(arrayForAdapter.size)
        dialogBinding.inputListRv.setHasFixedSize(true)

        dialogBinding.saveBtn.setOnClickListener {

            val array = adapter.analyzesInput
            val name = dialogBinding.resultNameEt.text.toString()

            if (checkName(results, name)) {
                saveInDataBase(array, name)
                dialog.hide()
            } else {
                if (results.isNotEmpty() && name.isNotEmpty()) {
                    dialogBinding.resultNameEt.error = "Данное имя уже используется"
                } else if (name.isEmpty()) {
                    dialogBinding.resultNameEt.error = "Введите название результата"
                }
                dialogBinding.resultNameEt.requestFocus()
            }
        }

    }

    private fun saveInDataBase(inputAnalyzes: ArrayList<AnalysisInput>, name: String) {
        val saveResults: ArrayList<AnalysisResult> = arrayListOf()

        val now = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val date = formatter.format(now)
        for (i in inputAnalyzes.indices) {
            if (inputAnalyzes[i].value != null) {
                saveResults.add(AnalysisResult("$name $date", inputAnalyzes[i].value, inputAnalyzes[i].name))
            }
        }
        viewModel.insertResults(dao, saveResults)
        results.addAll(saveResults)
        viewModel.results.value = results
    }

    private fun checkName(results: List<AnalysisResult>, name: String): Boolean {
        if (name.isEmpty()) {
            return false
        }

        if (results.isNotEmpty()) {
            for (element in results) {
                if (element.name == name) {
                    return false
                }
            }
        }

        return true
    }

    fun loadFromFile(view: View) {
        binding.inputByUserFab.visibility = View.GONE
        binding.loadFromFileFab.visibility = View.GONE
        binding.addFab.setImageResource(R.drawable.add_image)

        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123)
    }

    private fun loadFromImage(uri: Uri) {
        val input = contentResolver.openInputStream(uri)
        val parseImage = ParseImage(input, analyzes)
        parseImage.inputArray.observe(this, {
            binding.progressBar.visibility = View.GONE
            showInputDialog(it)
        })

        parseImage.parse()
    }

    fun showExtraButtons(view: View) {
        if (binding.inputByUserFab.visibility == View.GONE) {
            binding.inputByUserFab.visibility = View.VISIBLE
            binding.loadFromFileFab.visibility = View.VISIBLE
            binding.addFab.setImageResource(R.drawable.done_image)
        } else {
            binding.inputByUserFab.visibility = View.GONE
            binding.loadFromFileFab.visibility = View.GONE
            binding.addFab.setImageResource(R.drawable.add_image)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            binding.progressBar.visibility = View.VISIBLE

            val selectedFile = data.data //The uri with the location of the file

            if (selectedFile != null) {
                val input = contentResolver.openInputStream(selectedFile)

                val type = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(selectedFile))
                println("type = $type")
                println("input")

                if (type == "xls") {
                    val parseExcel = ParseExcel(input)
                    parseExcel.parseResults.observe(this, {
                        binding.progressBar.visibility = View.GONE
                        showInputDialog(it)
                    })
                    parseExcel.parseXls()
                } else if (type == "xlsx") {
                    val parseExcel = ParseExcel(input)
                    parseExcel.parseResults.observe(this, {
                        binding.progressBar.visibility = View.GONE
                        showInputDialog(it)
                    })
                    parseExcel.parseXlsx()
                } else if (type == "png" || type == "jpg" || type == "jpeg" || type == "JPEG") {
                    loadFromImage(selectedFile)
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Данный тип файла не поддерживается", Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    override fun onResultClick(position: Int) {
        val name = names[position]
        val resultForChart: ArrayList<AnalysisResult> = arrayListOf()

        for (obj in results) {
            if (obj.name == name) {
                resultForChart.add(obj)
            }
        }

        val myEntries1: ArrayList<MyEntry> = arrayListOf()
        val myEntries2: ArrayList<MyEntry> = arrayListOf()
        for (i in resultForChart.indices) {
            val value = resultForChart[i].value
            if (value != null) {
                myEntries1.add(MyEntry(MyMath.getCoef(resultForChart[i].analysisName, value, analyzes), i + 1f, resultForChart[i].analysisName, value))
                myEntries2.add(MyEntry(MyMath.getCoefWithModule(resultForChart[i].analysisName, value, analyzes), i + 1f, resultForChart[i].analysisName, value))
            }
        }

        myEntries1.sortBy {
            if (it.x < 0) {
                it.x * -1
            } else {
                it.x
            }
        }
        myEntries2.sortBy { it.x }

        updateViewPager(myEntries1, myEntries2)
        binding.resultsRV.visibility = View.GONE
        binding.viewPager.visibility = View.VISIBLE
        binding.resultNameTV.text = name
        binding.resultNameLabel.visibility = View.VISIBLE
    }

    private fun updateViewPager(myEntries1: ArrayList<MyEntry>, myEntries2: ArrayList<MyEntry>) {
        val viewPagerAdapter = FragmentsAdapter(this, myEntries1, myEntries2, analyzes)
        binding.viewPager.adapter = viewPagerAdapter
    }
}