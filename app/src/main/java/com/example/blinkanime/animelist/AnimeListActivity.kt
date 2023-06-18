package com.example.blinkanime.animelist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.MainActivity
import com.example.blinkanime.R
import com.example.blinkanime.databinding.ActivityAnimeListBinding
import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.mainFragment.MainPageFragment

class AnimeListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnimeListBinding

    // Builder pattern to create RecyclerView
    private fun RecyclerView.setup(
        layoutManager: RecyclerView.LayoutManager,
        adapter: RecyclerView.Adapter<*>
    ) = apply {
        this.layoutManager = layoutManager
        this.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAnimeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // progress indicator
        val progressIndicator = binding.progressCircular

        // setup recycler view
        val animeList = MainActivity.mainActivityViewModel.animeList
        val recyclerView = binding.recyclerView
        val adapter = AnimeListAdapter(animeList.value?: emptyList())
        recyclerView.setup(
            GridLayoutManager(this, 3),
            adapter)

        animeList.observe(this) {
            adapter.updateData(it)
        }

        // show progress indicator when loading data
        MainActivity.mainActivityViewModel.loadingData.observe(this) {
            if (it) {
                progressIndicator.visibility = android.view.View.VISIBLE
            } else {
                progressIndicator.visibility = android.view.View.GONE
            }
        }

        // setup search bar with id search_bar_edit_text
        val searchBar = binding.searchBarEditText
        searchBar.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                // do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // update anime list
                val query = s.toString()
                val filteredList = mutableListOf<Anime>()
                for (item in animeList.value!!) {
                    if (item.name.contains(query, true)) {
                        filteredList.add(item)
                    }
                }
                adapter.updateData(filteredList)
            }
        })

        val swipeRefreshLayout = binding.animeListRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            MainActivity.mainActivityViewModel.refresh()
            swipeRefreshLayout.isRefreshing = false
        }


        // go back to main activity when back button is pressed
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}