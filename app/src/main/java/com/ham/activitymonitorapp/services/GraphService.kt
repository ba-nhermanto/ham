package com.ham.activitymonitorapp.services

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.ham.activitymonitorapp.R
import com.ham.activitymonitorapp.databinding.HomeFragmentBinding
import com.ham.activitymonitorapp.viewmodels.HrViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GraphService constructor(
    private var binding: HomeFragmentBinding,
    private var hrViewModel: HrViewModel,
    private var context: Context,
    private var viewLifeCycleOwner: LifecycleOwner
) {

    private lateinit var lineChart: LineChart

    private lateinit var lineDataSet: LineDataSet

    private lateinit var lineData: LineData

    companion object {
        const val TAG = "GRAPH_SERVICE"
    }

    fun initializeHrGraph() {
        Log.d(TAG, "Initializing hr graph")
        lineChart = binding.hrGraph

        // chart settings
        lineChart.setTouchEnabled(true)
        lineChart.setDrawGridBackground(false)
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.isDragXEnabled = true
        lineChart.isDoubleTapToZoomEnabled = false

        // chart xAxis settings
        val xAxis = lineChart.xAxis
        xAxis.isEnabled = false
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        // chart yAxis settings
        lineChart.axisLeft.isEnabled = false
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisRight.textColor = ContextCompat.getColor(context, R.color.red)
        lineChart.axisRight.textSize = 14f

        val entries = hrViewModel.currentHrList.value?.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }
        Log.d(TAG, "setting up graph with entries: $entries")

        setupLineDataSet(entries)

        lineData = LineData(lineDataSet)
        lineChart.data = lineData
    }

    fun updateChart(heartRateData: List<Int>) {
        viewLifeCycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val entries = heartRateData.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            }

            lineData.clearValues()

            setupLineDataSet(entries)

            lineData.addDataSet(lineDataSet)

            lineChart.data = lineData

            lineChart.moveViewToX(lineData.xMax)
            lineChart.setVisibleXRangeMaximum(30f)

            lineChart.invalidate()
        }
    }

    fun observeHrList() {
        hrViewModel.currentHrList.observe(viewLifeCycleOwner) {
            Log.d(TAG, "current hr list changed: $it")
            updateChart(it.toList())
        }
    }

    fun observeHr() {
        hrViewModel.currentHrBpm.observe(viewLifeCycleOwner) {
            Log.d(TAG, "received new HR: $it, updating graph entry")
            addNewHeartEntry(it)
        }
    }

    private fun setupLineDataSet(yVals: List<Entry>?): LineDataSet {
        Log.d(TAG, "setting up linedataset")

        lineDataSet = LineDataSet(yVals, "Heart Rate")
        lineDataSet.color = ContextCompat.getColor(context, R.color.red)
        lineDataSet.setDrawValues(false)
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.setDrawCircles(false)
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.red))
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.lineWidth = 1.5f

        return lineDataSet
    }

    private fun addNewHeartEntry(heartRate: Int) {
        val data: LineData = lineChart.data
        val set = data.getDataSetByIndex(0)
        data.addEntry(Entry(set.entryCount.toFloat(), heartRate.toFloat()), 0)
        data.notifyDataChanged()

        lineChart.data = data
        lineChart.notifyDataSetChanged()
        lineChart.setVisibleXRangeMaximum(30f)
        lineChart.moveViewToX(set.entryCount.toFloat())
        lineChart.invalidate()
    }
}