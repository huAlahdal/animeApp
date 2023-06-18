package com.example.blinkanime.topanimes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.blinkanime.R
import com.example.blinkanime.animeRecommendationActivity.AnimeRecommendationAdapter
import com.example.blinkanime.databinding.ActivityAnimeListBinding
import com.example.blinkanime.databinding.ActivityTopAnimeBinding
import com.example.blinkanime.mainFragment.detailsActivity.AnimeDetailsFragment
import com.example.blinkanime.mainFragment.detailsActivity.DetailsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class TopAnimeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTopAnimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTopAnimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pager = binding.viewPager2
        val progressIndicator = binding.progressCircular
        progressIndicator.show()
        pager.visibility = android.view.View.GONE
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up the ViewPager with the sections adapter.
        pager.adapter = TopAnimePagerAdapter(this)

        // Set up the TabLayout with the ViewPager.
        TabLayoutMediator(binding.tabLayout2, pager) { tab, position ->
            // Set the tab's text here
            tab.text = when (position) {
                0 -> "All Anime"
                1 -> "Top Airing"
                2 -> "Top Upcoming"
                3 -> "Top TV Series"
                4 -> "Top Movies"
                5 -> "Top OVAs"
                6 -> "Top ONAs"
                7 -> "Top Specials"
                8 -> "Top Popular"
                9 -> "Most Favorited"
                else -> "All Anime"
            }
        }.attach()

        pager.visibility = android.view.View.VISIBLE
        progressIndicator.hide()

        // go back to main activity when back button is pressed
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    class TopAnimePagerAdapter(fa: FragmentActivity, private val count: Int = 0) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return 10
        }

        override fun createFragment(position: Int): Fragment {
            return TopAnimeFragment.newInstance(position, count)
        }
    }
}