package com.example.blinkanime.animeRecommendationActivity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.databinding.ActivityAnimeRecommendationBinding
import com.example.blinkanime.misc.MALScraper
import com.example.blinkanime.misc.RecommendationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnimeRecommendation : AppCompatActivity() {

    private lateinit var binding: ActivityAnimeRecommendationBinding
    private val adapter = AnimeRecommendationAdapter(null)

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
        // setup binding
        binding = ActivityAnimeRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get page number from intent
        val pervButton = binding.previousPage
        val nextButton = binding.nextPage

        val pageNumber = intent.getIntExtra("page_number", 0)

        // setup recycler view
        val progressIndicator = binding.progressCircular
        val recyclerView = binding.recomListView

        recyclerView.setup(
            LinearLayoutManager(this),
            adapter
        )

//        MainActivity.mainActivityViewModel.loadingData.observe(this) { loading ->
//            if (loading) {
//                progressIndicator.visibility = android.view.View.VISIBLE
//            } else {
//                progressIndicator.visibility = android.view.View.GONE

                // get anime Recommendation
                CoroutineScope(Dispatchers.IO).launch {
                    progressIndicator.visibility = View.VISIBLE
                    val animeData = MALScraper().getAnimeRecommendations(pageNumber)
                    withContext(Dispatchers.Main) {
                        progressIndicator.visibility = View.GONE
                        if (animeData != null) {
                            adapter.updateData(animeData)
                            setupButton(nextButton, animeData.nextPage)
                            setupButton(pervButton, animeData.prevPage)
                        }
                    }
                }
//            }
//        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // return back
        toolbar.setNavigationOnClickListener {
            finish()
        }


    }

    private fun setupButton(button: Button, pageNumber: Int?) {
        if (pageNumber != null) {
            button.visibility = View.VISIBLE
            button.setOnClickListener {
                val updatedIntent = intent.putExtra("page_number", pageNumber)
                finish()
                startActivity(updatedIntent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.comments_settings, menu)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        menu.findItem(R.id.translate_comments).isChecked = sharedPref.getBoolean("translate_reasons", false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuDataMap = mapOf(
            R.id.translate_comments to Pair("translate_reasons") { isChecked: Boolean ->
                adapter.reasonSettings.copy(translate = isChecked)
            }
        )

        menuDataMap[item.itemId]?.let { pair ->
            val prefKey = pair.first
            val adapterUpdate = pair.second
            val isChecked = !item.isChecked
            val updatedSettings = adapterUpdate(isChecked)
            updateAdapterSettings(updatedSettings)
            savePreference(prefKey, isChecked)
            item.isChecked = !item.isChecked
        }


        return false
    }

    private fun updateAdapterSettings(settings: ReasonSettings?) {
        settings?.let {
            adapter.reasonSettings = it
            adapter?.notifyDataSetChanged()
        }
    }

    private fun savePreference(key: String, value: Boolean) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean(key, value).apply()
    }
}