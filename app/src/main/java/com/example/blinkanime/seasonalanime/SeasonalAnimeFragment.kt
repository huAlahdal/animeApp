package com.example.blinkanime.seasonalanime

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.databinding.FragmentSeasonalAnimeBinding
import com.example.blinkanime.misc.MALScraper
import com.example.blinkanime.topanimes.TopAnimeAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val Season = "season"
private const val Year = "year"

/**
 * A simple [Fragment] subclass.
 * Use the [SeasonalAnimeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SeasonalAnimeFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var year: Int? = null
    private var season: String? = null

    private var adapterTV: SeasonalAnimeAdapter = SeasonalAnimeAdapter(emptyList(), "TV")
    private var adapterONA: SeasonalAnimeAdapter = SeasonalAnimeAdapter(emptyList(), "ONA")
    private var adapterOVA: SeasonalAnimeAdapter = SeasonalAnimeAdapter(emptyList(), "OVA")
    private var adapterMovie: SeasonalAnimeAdapter = SeasonalAnimeAdapter(emptyList(), "Movie")
    private var adapterSpecial: SeasonalAnimeAdapter = SeasonalAnimeAdapter(emptyList(), "Special")
    // Builder pattern to create RecyclerView
    private fun RecyclerView.setup(
        layoutManager: RecyclerView.LayoutManager,
        adapter: RecyclerView.Adapter<*>
    ) = apply {
        this.layoutManager = layoutManager
        this.adapter = adapter
    }

    private val binding: FragmentSeasonalAnimeBinding by lazy {
        FragmentSeasonalAnimeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            year = it.getInt(Year)
            season = it.getString(Season)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayout = binding.linearLayout

        val recyclerViewTV = binding.recyclerViewTv
        val recyclerViewONA = binding.recyclerViewOna
        val recyclerViewOVA = binding.recyclerViewOva
        val recyclerViewMovie = binding.recyclerViewMovie
        val recyclerViewSpecial = binding.recyclerViewSpecial



        recyclerViewTV.setup(GridLayoutManager(context, 3), adapterTV)
        recyclerViewONA.setup(GridLayoutManager(context, 3), adapterONA)
        recyclerViewOVA.setup(GridLayoutManager(context, 3), adapterOVA)
        recyclerViewMovie.setup(GridLayoutManager(context, 3), adapterMovie)
        recyclerViewSpecial.setup(GridLayoutManager(context, 3), adapterSpecial)


        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.progressCircular.visibility = View.VISIBLE
            }

            val data = MALScraper().getSeasonalAnime(season!!, year.toString())
            val typeTV = data?.filter { it.type == "TV" }
            val typeONA = data?.filter { it.type == "ONA" }
            val typeOVA = data?.filter { it.type == "OVA" }
            val typeMovie = data?.filter { it.type == "Movie" }
            val typeSpecial = data?.filter { it.type == "Special" }


            withContext(Dispatchers.Main) {
                binding.progressCircular.visibility = View.GONE
                if (data != null) {
                    typeTV?.get(0)?.let { adapterTV.updateData(it.list) }
                    typeONA?.get(0)?.let { adapterONA.updateData(it.list) }
                    typeOVA?.get(0)?.let { adapterOVA.updateData(it.list) }
                    typeMovie?.get(0)?.let { adapterMovie.updateData(it.list) }
                    typeSpecial?.get(0)?.let { adapterSpecial.updateData(it.list) }
                }
            }

        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param year Year.
         * @param season Season.
         * @return A new instance of fragment SeasonalAnimeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(year: Int, season: String) =
            SeasonalAnimeFragment().apply {
                arguments = Bundle().apply {
                    putInt(Year, year)
                    putString(Season, season)
                }
            }
    }
}