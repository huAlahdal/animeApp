package com.example.blinkanime.mainFragment.detailsActivity

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.MainActivity
import com.example.blinkanime.R
import com.example.blinkanime.animelist.AnimeListAdapter
import com.example.blinkanime.databinding.AnimedDetailsPageBinding
import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.mainFragment.AnimeDetails
import com.example.blinkanime.misc.MALScraper
import com.example.blinkanime.misc.MiscFunc
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnimeDetailsFragment : Fragment() {
    // setup fragment
    private lateinit var _binding: AnimedDetailsPageBinding
    private val binding get() = _binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AnimedDetailsPageBinding.inflate(inflater, container, false)

        val position = arguments?.getInt("position") ?: 0
        val contentContainer: FrameLayout = binding.contentContainer

        DetailsActivity.animeDetailsViewModel.animeDetails.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                activity?.title = it?.get(0)?.name
                when (position) {
                    0 -> {
                        setupInfoTab(contentContainer, container, it)
                    }
                    1 -> {
                        setupListView(contentContainer, container, it)
                    }
                    2 -> {
                        setupPartsListView(contentContainer, container, it)
                    }
                    3 -> {
                        setupReviewsListView(contentContainer, container, it)
                    }
                    4 -> {
                        setupRecommendationsListView(contentContainer, container, it)
                    }
                }
            }
        }

        return binding.root
    }

    private fun setupInfoTab(contentContainer: FrameLayout, container: ViewGroup?, animeDetails: List<AnimeDetails>) {
        val data = animeDetails.firstOrNull()
        val view = layoutInflater.inflate(R.layout.anime_about_detalis, container, false)
        contentContainer.addView(view)

        val imageView = view.findViewById<ImageView>(R.id.anime_image)

        val genresContainer = view.findViewById<LinearLayout>(R.id.genres_container)

        contentContainer.removeAllViews()

        view.findViewById<TextView>(R.id.anime_score).apply {
            text = data?.malDetails?.score
        }

        view.findViewById<TextView>(R.id.anime_rank).apply {
            text = data?.malDetails?.ranked
        }

        view.findViewById<TextView>(R.id.anime_popularity).apply {
            text = "Popularity " + data?.malDetails?.popularity
        }

        view.findViewById<TextView>(R.id.anime_studio).apply {
            text = "الاستديو:" + data?.malDetails?.studio
        }

        view.findViewById<TextView>(R.id.anime_name).apply {
            text = data?.name
        }
        view.findViewById<TextView>(R.id.anime_details).apply {
            text = if (data?.description.isNullOrEmpty())
                data?.malDetails?.description
            else
                data?.description
        }
        view.findViewById<TextView>(R.id.anime_year).apply {
            text = data?.year
        }
        view.findViewById<TextView>(R.id.anime_episodesNum).apply {
            text = data?.totalEp
        }
        view.findViewById<TextView>(R.id.anime_season).apply {
            text = "الموسم: " + data?.season
        }
        view.findViewById<TextView>(R.id.anime_type).apply {
            text = "النوع: " + data?.type
        }
        view.findViewById<TextView>(R.id.anime_status).apply {
            text = "حالة الأنمي: " + data?.status
        }
        view.findViewById<TextView>(R.id.anime_length).apply {
            text = data?.length
        }
        view.findViewById<TextView>(R.id.anime_source).apply {
            text = data?.source
        }

        MiscFunc().loadAndTransformImage(data?.image?:"", imageView, "noRound", 600, 1000)

//                view.findViewById<Button>(R.id.anime_mal).setOnClickListener {
//                    val url = data?.MAL_Link
//                    if (url != "") {
//                        MiscFunc().openWebPage(view.context, url?:"")
//                    } else {
//                        Snackbar.make(view, "MAL link not found", Snackbar.LENGTH_SHORT).show()
//                    }
//                }
//
//                view.findViewById<Button>(R.id.anime_trailer).setOnClickListener {
//                    val url = data?.trailer
//                    if (url != "") {
//                        MiscFunc().openWebPage(view.context, url?:"")
//                    } else {
//                        Snackbar.make(view, "Trailer link not found", Snackbar.LENGTH_SHORT).show()
//                    }
//                }

        data?.genres?.forEach {
            val genre = cardCreate(it)
            genresContainer.addView(genre)
        }

        contentContainer.addView(view)


    }

    private fun cardCreate(genText: String): View {
        val cardView = CardView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radius = dpToPx(8f, context)
            cardElevation = dpToPx(8f, context)
            useCompatPadding = true
        }

        val textView = TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            text = genText
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setPadding(dpToPx(8f, context).toInt())
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        cardView.addView(textView)


        return cardView
    }

    private fun dpToPx(dp: Float, context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }

    private fun setupListView(contentContainer: FrameLayout, container: ViewGroup?, animeDetails: List<AnimeDetails>) {
        // inflate the layout for this fragment
        val view = layoutInflater.inflate(R.layout.anime_details_ep, container, false)
        val progressIndicator = view.findViewById<CircularProgressIndicator>(R.id.progress_circular)


        val episodesList = animeDetails[0].episodes

        // CoroutineScope with the context of the main thread
        progressIndicator.visibility = View.VISIBLE
        contentContainer.addView(view)
        CoroutineScope(Dispatchers.Main).launch {
            val malEpisodes = animeDetails[0].malDetails?.let {
                MALScraper().getEpisodes(
                    it.MALId,
                    it.MALName)
            }

            withContext(Dispatchers.Main) {
                if (malEpisodes != null) {
                    if (malEpisodes.isNotEmpty()) {
                        episodesList?.forEach { episode ->
                            val epNumber = episode.name.filter { it.isDigit() }.toInt()
                            val malEpisode = malEpisodes.filter { it.epNumber.toInt() == epNumber}
                            episode.extraData = malEpisode.firstOrNull()
                        }
                    }
                }


                val adapter = EpisodesListAdapter(episodesList?: emptyList())

                view.findViewById<RecyclerView>(R.id.episodes_list_view).setup(
                    LinearLayoutManager(requireContext()),
                    adapter
                )

                progressIndicator.visibility = View.GONE
                contentContainer.removeAllViews()
                contentContainer.addView(view)

            }
        }


    }

    private fun setupPartsListView(contentContainer: FrameLayout, container: ViewGroup?, animeDetails: List<AnimeDetails>) {
        // inflate the layout for this fragment
        val view = layoutInflater.inflate(R.layout.anime_details_ep, container, false)
        val progressIndicator = view.findViewById<CircularProgressIndicator>(R.id.progress_circular)


        // show progress indicator when loading data
        MainActivity.mainActivityViewModel.loadingData.observe(viewLifecycleOwner) {
            if (it) {
                progressIndicator.visibility = View.VISIBLE
            } else {
                progressIndicator.visibility = View.GONE
            }
        }

        var animelistClean = emptyList<Anime>()
        val adapter = AnimeListAdapter(animelistClean)

        MainActivity.mainActivityViewModel.animeList.observe(viewLifecycleOwner) { anime ->
            if (anime.isNotEmpty()) {

                val animeName = animeDetails[0].name
                val animeNameClean = if (animeName?.contains(":") == true) {
                    animeName.substringBefore(":")
                } else if (animeName?.contains(Regex("\\d+(nd|rd|th)\\b")) == true)  {
                    animeName.replace(Regex("\\d+(nd|rd|th)\\b"), "")
                        .trim().substringBefore("Season").trim()
                } else {
                    animeName?.substringBefore("Season")?.trim()
                }

                animelistClean = anime.filter {
                    it.name.contains(animeNameClean?:"", ignoreCase = true) &&
                            !it.name.equals(animeName, ignoreCase = true)
                }

                adapter.updateData(animelistClean)
            }
        }

        view.findViewById<RecyclerView>(R.id.episodes_list_view).setup(
            GridLayoutManager(requireContext(), 2),
            adapter
        )

        contentContainer.addView(view)
    }

    private fun setupRecommendationsListView(contentContainer: FrameLayout, container: ViewGroup?, animeDetails: List<AnimeDetails>) {
        // inflate the layout for this fragment
        val view = layoutInflater.inflate(R.layout.anime_details_ep, container, false)
        val progressIndicator = view.findViewById<CircularProgressIndicator>(R.id.progress_circular)


        // CoroutineScope with the context of the main thread
        progressIndicator.visibility = View.VISIBLE
        contentContainer.addView(view)
        CoroutineScope(Dispatchers.Main).launch {
            MALScraper().getAnimeReviews(animeDetails[0].malDetails?.MALId?: "", animeDetails[0].malDetails?.MALName?: "")
            val malRecommendation = animeDetails[0].malDetails?.let {
                MALScraper().getSimilarAnimes(
                    it.MALId,
                    it.MALName)
            }

            withContext(Dispatchers.Main) {
                val adapter = AnimeRecommendationAdapter(malRecommendation?: emptyList())

                view.findViewById<RecyclerView>(R.id.episodes_list_view).setup(
                    LinearLayoutManager(requireContext()),
                    adapter
                )

                progressIndicator.visibility = View.GONE
                contentContainer.removeAllViews()
                contentContainer.addView(view)

            }
        }
    }

    private fun setupReviewsListView(contentContainer: FrameLayout, container: ViewGroup?, animeDetails: List<AnimeDetails>) {
        // inflate the layout for this fragment
        val view = layoutInflater.inflate(R.layout.anime_details_ep, container, false)
        val progressIndicator = view.findViewById<CircularProgressIndicator>(R.id.progress_circular)


        // CoroutineScope with the context of the main thread
        progressIndicator.visibility = View.VISIBLE
        contentContainer.addView(view)
        CoroutineScope(Dispatchers.Main).launch {
            MALScraper().getAnimeReviews(animeDetails[0].malDetails?.MALId?: "", animeDetails[0].malDetails?.MALName?: "")
            val malRecommendation = animeDetails[0].malDetails?.let {
                MALScraper().getAnimeReviews(
                    it.MALId,
                    it.MALName)
            }

            withContext(Dispatchers.Main) {
                val adapter = malRecommendation?.let { AnimeReviewAdapter(it) }

                if (adapter != null) {
                    view.findViewById<RecyclerView>(R.id.episodes_list_view).setup(
                        LinearLayoutManager(requireContext()),
                        adapter
                    )
                }

                progressIndicator.visibility = View.GONE
                contentContainer.removeAllViews()
                contentContainer.addView(view)

            }
        }


    }

    // Builder pattern to create RecyclerView
    private fun RecyclerView.setup(
        layoutManager: RecyclerView.LayoutManager,
        adapter: RecyclerView.Adapter<*>
    ) = apply {
        this.layoutManager = layoutManager
        this.adapter = adapter
    }


    companion object {
        // New instance pattern to create fragment with parameters
        fun newInstance(position: Int): AnimeDetailsFragment {
            val fragment = AnimeDetailsFragment()
            val args = Bundle()
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }

}