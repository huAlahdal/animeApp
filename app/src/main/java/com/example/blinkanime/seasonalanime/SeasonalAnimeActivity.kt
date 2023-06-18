package com.example.blinkanime.seasonalanime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.blinkanime.databinding.ActivitySeasonalAnimeBinding
import com.example.blinkanime.misc.MALScraper
import com.example.blinkanime.misc.Season
import com.example.blinkanime.topanimes.TopAnimeFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeasonalAnimeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeasonalAnimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeasonalAnimeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val pager = binding.viewPager2
        val progressIndicator = binding.progressCircular
        progressIndicator.show()
        pager.visibility = android.view.View.GONE
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        CoroutineScope(Dispatchers.IO).launch {
            val seasons = MALScraper().getSeaons()

            withContext(Dispatchers.Main) {
                pager.adapter = SeasonalAnimePagerAdapter(
                    this@SeasonalAnimeActivity,
                    seasons?.size ?: 0,
                    seasons ?: emptyList())
                // Set up the TabLayout with the ViewPager.
                TabLayoutMediator(binding.tabLayout2, pager) { tab, position ->
                    // Set the tab's text here
                    val tezt = if (seasons?.get(position)?.season == "Later") "Later"
                    else seasons?.get(position)?.season + " " + seasons?.get(position)?.year
                    tab.text = tezt
                }.attach()

                pager.visibility = android.view.View.VISIBLE
                progressIndicator.hide()
            }
        }
        // go back to main activity when back button is pressed
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    class SeasonalAnimePagerAdapter(fa: FragmentActivity, private val tabCount: Int, private val seasonData: List<Season>) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return tabCount
        }

        override fun createFragment(position: Int): Fragment {
            return SeasonalAnimeFragment.newInstance(seasonData[position].year, seasonData[position].season)
        }
    }
}