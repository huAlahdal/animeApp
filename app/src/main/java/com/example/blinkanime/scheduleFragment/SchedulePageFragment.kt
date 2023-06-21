package com.example.blinkanime.scheduleFragment

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.blinkanime.databinding.SchedulePageBinding
import com.google.android.material.tabs.TabLayoutMediator

class SchedulePageFragment : Fragment() {
    private lateinit var binding: SchedulePageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SchedulePageBinding.inflate(inflater, container, false)

        // Set up the ViewPager with the sections adapter.
        binding.viewPager.adapter = SectionsPagerAdapter(requireActivity())

        // Set up the TabLayout with the ViewPager.
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // Set the tab's text here
            tab.text = when (position) {
                0 -> "الأثنين"
                1 -> "الثلاثاء"
                2 -> "الأربعاء"
                3 -> "الخميس"
                4 -> "الجمعة"
                5 -> "السبت"
                else -> "الأحد"
            }
        }.attach()

        // Get the current day of the week
        val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        // Set the current item of the ViewPager2 to the appropriate position
        binding.viewPager.currentItem = when (currentDayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            else -> 6 // Sunday
        }

        return binding.root
    }
}

class SectionsPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        // Return the number of tabs you have
        return 7
    }

    override fun createFragment(position: Int): Fragment {
        // Return the correct Fragment for the given tab position
        return DayFragment.newInstance(position)
    }
}