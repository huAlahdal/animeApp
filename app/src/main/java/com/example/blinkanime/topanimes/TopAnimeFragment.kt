package com.example.blinkanime.topanimes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.databinding.TopAnimeFragmentBinding
import com.example.blinkanime.misc.MALScraper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TopAnimeFragment : Fragment() {

    private var adapter: TopAnimeAdapter = TopAnimeAdapter(emptyList())
    // Builder pattern to create RecyclerView
    private fun RecyclerView.setup(
        layoutManager: RecyclerView.LayoutManager,
        adapter: RecyclerView.Adapter<*>
    ) = apply {
        this.layoutManager = layoutManager
        this.adapter = adapter
    }

    private val binding: TopAnimeFragmentBinding by lazy {
        TopAnimeFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = getTypeFromArguments()

        val count = arguments?.getInt("count") ?: 0

        val recyclerView = binding.recyclerView
        recyclerView.setup(
            LinearLayoutManager(requireContext()),
            adapter
        )

        getTopAnime(type, count)

    }

    private fun getTopAnime(type: String, count: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.progressCircular.visibility = View.VISIBLE
            }

            val topAnime = MALScraper().getTopAnime(type, count)

            withContext(Dispatchers.Main) {
                binding.progressCircular.visibility = View.GONE
                adapter.updateData(topAnime ?: emptyList())

                if (topAnime?.get(0)?.source?.isNotEmpty() == true) {
                    binding.nextPage.visibility = View.VISIBLE
                    binding.nextPage.setOnClickListener {
                        binding.previousPage.visibility = View.GONE
                        binding.nextPage.visibility = View.GONE
                        adapter.updateData(emptyList())
                        getTopAnime(type, count + 50)
                    }
                }

                if (topAnime?.get(0)?.malDetails?.MALName?.isNotEmpty() == true) {
                    binding.previousPage.visibility = View.VISIBLE
                    binding.previousPage.setOnClickListener {
                        binding.previousPage.visibility = View.GONE
                        binding.nextPage.visibility = View.GONE
                        adapter.updateData(emptyList())
                        getTopAnime(type, count - 50)
                    }
                }
            }
        }

    }



    private fun getTypeFromArguments(): String {
        val position = arguments?.getInt("position") ?: 0
        val typeMap = mapOf(
            0 to "all",
            1 to "airing",
            2 to "upcoming",
            3 to "tv",
            4 to "movie",
            5 to "ova",
            6 to "ona",
            7 to "special",
            8 to "bypopularity",
            9 to "favorite"
        )
        return typeMap[position] ?: "all"
    }

    companion object {
        fun newInstance(position: Int, count: Int = 0): TopAnimeFragment {
            val fragment = TopAnimeFragment()
            val args = Bundle().apply {
                putInt("position", position)
                putInt("count", count)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
