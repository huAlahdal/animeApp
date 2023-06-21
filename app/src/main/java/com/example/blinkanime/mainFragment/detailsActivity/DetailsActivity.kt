package com.example.blinkanime.mainFragment.detailsActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.blinkanime.R
import com.example.blinkanime.databinding.ActivityDetailsBinding
import com.example.blinkanime.mainFragment.AnimeDetails
import com.example.blinkanime.mainFragment.MainViewModel
import com.example.blinkanime.scheduleFragment.DayFragment
import com.example.blinkanime.scheduleFragment.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import java.net.URLDecoder

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pager = binding.viewPager2
        val progressIndicator = binding.progressCircular
        progressIndicator.show()
        pager.visibility = android.view.View.GONE
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val animeLink = intent.extras?.getString("animeLink")
        val malLink = intent.extras?.getString("MALLink")
        val isMAL: Boolean
        val getLink = if (animeLink != null) {
            isMAL = false
            val url = URLDecoder.decode(animeLink, "UTF-8")
            url?.replace("https://witanime.com", "")?.replace("https://w1.anime4up.tv", "")
        } else {
            isMAL = true
            val url = malLink?:""
            val regex = """.*/anime/(\d+).*""".toRegex()
            val matchResult = regex.find(url)
            val id = matchResult?.groups?.get(1)?.value
            id
        }


        animeDetailsViewModel = AnimeDetailsViewModel(getLink!!, isMAL)

        animeDetailsViewModel.animeDetails.observe(this) {
            if (it.isNotEmpty()) {
                progressIndicator.hide()
                pager.visibility = android.view.View.VISIBLE
            }
        }

        val tabID = intent.extras?.getInt("tabID")?: 0

        // Set up the ViewPager with the sections adapter.
        pager.adapter = DetailsPagerAdapter(this)

        // Set up the TabLayout with the ViewPager.
        TabLayoutMediator(binding.tabLayout2, pager) { tab, position ->
            // Set the tab's text here
            tab.text = when (position) {
                0 -> "عن الانمي"
                1 -> "الحلقات"
                2 -> "اجزاء اخرى"
                3 -> "التقييم"
                4 -> "انميات مشابهة"
                else -> "عن الانمي"
            }
        }.attach()

        // go back to main activity when back button is pressed
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.tabLayout2.getTabAt(tabID)?.select()
    }

    companion object {
        var animeDetailsViewModel: AnimeDetailsViewModel = AnimeDetailsViewModel("")
    }
}

class DetailsPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        // Return the number of tabs you have
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return AnimeDetailsFragment.newInstance(position)
    }
}