package com.example.blinkanime.scheduleFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blinkanime.MainActivity
import com.example.blinkanime.databinding.ScheduleContentBinding
import com.example.blinkanime.mainFragment.AnimeScraper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DayFragment : Fragment() {
    private var dayOfWeek: Int? = null

    private var _binding: ScheduleContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dayOfWeek = it.getInt(ARG_DAY_OF_WEEK)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = ScheduleContentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ScheduleAdapter(emptyList())
        recyclerView.adapter = adapter

//        val animeScraper = AnimeScraper()
//        lifecycleScope.launch {
//            animeScraper.updateScheduleData()
//        }

        scheduleViewModel.scheduleData.observe(viewLifecycleOwner) { animeSchedule ->
            val animesch = animeSchedule
                .filter { it.dayOfTheWeek == toArabic(dayOfWeek) }
                .flatMap { it.animeData }

            adapter.updateData(animesch)
        }

        val swipeRefreshLayout = binding.sechduleRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            scheduleViewModel.refresh()
            swipeRefreshLayout.isRefreshing = false
        }


        return root
    }

    private fun toArabic(dayOfWeek: Int?) : String {
        return when (dayOfWeek) {
            0 -> "الإثنين"
            1 -> "الثلاثاء"
            2 -> "الأربعاء"
            3 -> "الخميس"
            4 -> "الجمعة"
            5 -> "السبت"
            6 -> "الأحد"
            // Pass data for other days of the week here
            else -> ""
        }
    }

    companion object {
        private const val ARG_DAY_OF_WEEK = "dayOfWeek"
        val scheduleViewModel = ViewModelProvider.NewInstanceFactory().create(ScheduleViewModel::class.java)
        @JvmStatic
        fun newInstance(dayOfWeek: Int) =
            DayFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_DAY_OF_WEEK, dayOfWeek)
                }
            }
    }
}