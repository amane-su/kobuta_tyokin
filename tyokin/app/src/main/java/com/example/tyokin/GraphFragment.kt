package com.example.tyokin

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.tyokin.Bank.Goalmoney
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GraphFragment : Fragment() {

    private lateinit var lineChart: LineChart

    // 現在の日付から日付リストを生成
    private val today = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dates: List<String> = List(12 * 31) { index ->
        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -index) }.let { dateFormat.format(it.time) }
    }.reversed()

    // 初期データ: すべての値を0に設定
    private val data: MutableList<Float> = MutableList(dates.size) { 0f }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lineChart = view.findViewById(R.id.lineChart)

        //戻るボタンを押したとき
        val returnButton = view.findViewById<ImageButton>(R.id.backButton)
        returnButton.setOnClickListener { onReturnButtonTapped() }

        //グラフの期間を設定するボタンを押したとき
        val selectButton = view.findViewById<Button>(R.id.selectPeriodButton)
        selectButton.setOnClickListener { onSelectButtonTapped() }

        // データを読み込み
        val loadedData = loadSavingsData()
        data.clear()
        data.addAll(loadedData.first)

        // 初期(デフォルト)表示: 最新7日間のデータを表示
        updateChart(getRecentData(7))
    }

    private fun updateChart(filteredData: Pair<List<Float>, List<String>>) {
        val (filteredValues, filteredDates) = filteredData

        if (dates.isEmpty() || data.isEmpty() || filteredValues.isEmpty() || filteredDates.isEmpty()) {
            lineChart.clear()
            lineChart.setNoDataText("データがありません")
            lineChart.setNoDataTextColor(Color.BLACK) // メッセージの文字色を設定
            lineChart.setBackgroundColor(Color.WHITE) // 背景色を設定
            lineChart.invalidate() // 再描画
            return
        }

        // データをEntry型に変換
        val dataEntries = mutableListOf<Entry>()  //mutableListOf：変更可能
        filteredValues.forEachIndexed { index, value ->
            dataEntries.add(Entry(index.toFloat(), value))
        }

        // メインのデータセット
        val lineDataSet = LineDataSet(dataEntries, "貯金額の遷移")
        lineDataSet.lineWidth = 3.0f
        lineDataSet.color = Color.rgb(0, 255, 128)

        // 目標ラインのデータセット
        val goal = Goalmoney.toFloat()  //共有プリファレンスの目標貯金額を代入
        val goalEntries = listOf(
            Entry(0.0f, goal),
            Entry(filteredValues.size.toFloat() - 1, goal)
        )

        val goalLineDataSet = LineDataSet(goalEntries, "目標貯金額")
        goalLineDataSet.color = resources.getColor(R.color.black, null)
        goalLineDataSet.lineWidth = 3.0f
        goalLineDataSet.setDrawCircles(true)
        goalLineDataSet.enableDashedLine(8f, 3f, 1f)

        // グラフのデータにセット
        val lineData = LineData(lineDataSet, goalLineDataSet)
        lineChart.data = lineData

        // グラフの設定
        lineChart.description.isEnabled = false

        // x軸の設定
        lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : ValueFormatter() {
                private val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index in filteredDates.indices) {
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(filteredDates[index])
                        dateFormat.format(date)
                    } else ""
                }
            }
            granularity = 1f
            labelCount = filteredDates.size.coerceAtMost(31)
        }

        // 左Y軸の設定
        lineChart.axisLeft.apply {
            axisMinimum = 0f
            granularity = 5000f
            setGranularityEnabled(true)
        }

        // 右Y軸の非表示設定
        lineChart.axisRight.isEnabled = false

        lineChart.invalidate() // グラフを更新
    }

    private fun getRecentData(days: Int): Pair<List<Float>, List<String>> {
        val recentDates = dates.takeLast(days)
        val recentData = data.takeLast(days)
        return Pair(recentData, recentDates)
    }

    fun onReturnButtonTapped() {
        activity?.supportFragmentManager?.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        // OpenGL表示
        val fragmentManager = activity?.supportFragmentManager
        if(fragmentManager != null){
            val transaction = fragmentManager.beginTransaction()
            if(activity != null){
                val glfragment = requireActivity().supportFragmentManager.findFragmentById(R.id.kobuta) as? OpenGLFragment
                if (glfragment != null) {
                    // フラグメントにアクセスできた場合の処理
                    transaction.show(glfragment) // フラグメントを非表示にする
                    transaction.commit()
                }
            }
        }
    }

    fun onSelectButtonTapped() {
        showRangePicker()
    }

    private fun filterDataByDateRange(startDate: String, endDate: String): Pair<List<Float>, List<String>> {

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start = format.parse(startDate)
        val end = format.parse(endDate)

        val filteredDates = dates.filter { date ->
            val parsedDate = format.parse(date)
            parsedDate in start..end
        }

        if (filteredDates.isEmpty()) {
            return Pair(emptyList(), emptyList()) // 空のリストを返す
        }

        val startIndex = dates.indexOf(filteredDates.first())
        val endIndex = dates.indexOf(filteredDates.last())

        return if (startIndex in data.indices && endIndex in data.indices) {
            Pair(data.subList(startIndex, endIndex + 1), filteredDates)
        } else {
            Pair(emptyList(), emptyList())
        }
    }

    private fun showRangePicker() {
        // 範囲選択のためのMaterialDatePickerを作成
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("期間を選択してください")
            .build()

        // Pickerの結果を処理
        dateRangePicker.addOnPositiveButtonClickListener { dateRange ->
            // 範囲の開始日と終了日を取得
            val startDateMillis = dateRange.first
            val endDateMillis = dateRange.second

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDate = dateFormat.format(startDateMillis)
            val endDate = dateFormat.format(endDateMillis)

            // デバッグ用ログ
            Log.d("DateRangePicker", "Start Date: $startDate, End Date: $endDate")

            // 範囲を使用してデータをフィルタリング
            val filteredData = filterDataByDateRange(startDate, endDate)
            updateChart(filteredData)
        }

        // Pickerを表示
        dateRangePicker.show(parentFragmentManager, "DateRangePicker")
    }

    private fun loadSavingsData(): Pair<List<Float>, List<String>> {
        val context = context ?: return Pair(emptyList(), emptyList()) // Contextがnullの場合は空リストを返す
        val savingsData = MainActivity.SavingsManager.getAllSavings(context)

        val values = mutableListOf<Float>()
        var previousAmount = 0f

        dates.forEach { date ->
            val amount = savingsData[date]?.toFloat() ?: previousAmount
            values.add(amount)
            previousAmount = amount // 貯金がない日でも前日分を引き継ぐ
        }
        return Pair(values, dates)
    }
}