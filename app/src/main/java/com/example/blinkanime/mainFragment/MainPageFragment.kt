package com.example.blinkanime.mainFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.MainActivity
import com.example.blinkanime.databinding.MainPageBinding
import com.example.blinkanime.newsFragment.NewsPageFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class MainPageFragment : Fragment() {
    // setup fragment
    private lateinit var _binding: MainPageBinding
    private val binding get() = _binding
//    private val seasonAnimeAdapter = SeasonAnimeAdapter(emptyList())
    private val latestEpisodesAdapter = LatestEpisodesAdapter(emptyList())
//    private val mostWatchedAdapter = MostWatchedAnimeAdapter(emptyList())

    // Builder pattern to create RecyclerView
    private fun RecyclerView.setup(
        layoutManager: RecyclerView.LayoutManager,
        adapter: RecyclerView.Adapter<*>
    ) = apply {
        this.layoutManager = layoutManager
        this.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = MainPageBinding.inflate(inflater, container, false)


//        binding.seasonAnimesRecyclerView.setup(
//            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false),
//            seasonAnimeAdapter
//        )

        binding.latestEpisodesRecyclerView.setup(
            LinearLayoutManager(context),
            latestEpisodesAdapter
        )

//        binding.mostWatchedRecyclerView.setup(
//            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false),
//            mostWatchedAdapter
//        )
//
//        mainFragmentViewModel.seasonAnime.observe(viewLifecycleOwner) { data ->
//            seasonAnimeAdapter.updateData(data)
//        }

        mainFragmentViewModel.latestEpisodes.observe(viewLifecycleOwner) { data ->
            latestEpisodesAdapter.updateData(data)
        }

//        mainFragmentViewModel.mostWatched.observe(viewLifecycleOwner) { data ->
//            mostWatchedAdapter.updateData(data)
//        }




        val swipeRefreshLayout = binding.mainRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
             mainFragmentViewModel.refresh()
            swipeRefreshLayout.isRefreshing = false
        }

        return binding.root
    }

    companion object {
        val mainFragmentViewModel = ViewModelProvider.NewInstanceFactory().create(MainViewModel::class.java)
    }
}