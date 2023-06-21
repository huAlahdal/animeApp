package com.example.blinkanime.episodecomments

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.databinding.ActivityEpisodecommentsBinding
import com.example.blinkanime.misc.MALScraper
import com.example.blinkanime.misc.ThreadData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EpisodeCommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEpisodecommentsBinding
    // adapter
    private val adapter = EpisodeCommentsAdapter(ThreadData(emptyList(), null, emptyList()))

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
        binding = ActivityEpisodecommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val episodeComments = intent.extras?.getString("episodeComments")
        val id = episodeComments?.substringAfter("topicid=")
        var page = 0
        if (episodeComments?.contains("show=") == true)
            page = episodeComments.substringAfter("show=").toInt()


        val progressIndicator = binding.progressCircular
        val preButton = binding.previousPage
        val nextButton = binding.nextPage
        // adapter
        val recyclerView = binding.recyclerView
        recyclerView.setup(
            LinearLayoutManager(this),
            adapter
        )

        progressIndicator.visibility = RecyclerView.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val threadData = MALScraper().getEpisodeComments(id!!, page)
            withContext(Dispatchers.Main) {
                progressIndicator.visibility = RecyclerView.GONE
                adapter.updateData(threadData?: ThreadData(emptyList(), null, emptyList()))

                threadData?.pages?.forEach {
                    if (it.page == "previous") {
                        preButton.visibility = View.VISIBLE
                        preButton.setOnClickListener {_ ->
                            // call this activity again with the previous page
                            val intent = Intent(this@EpisodeCommentsActivity, EpisodeCommentsActivity::class.java)
                            intent.putExtra("episodeComments", it.url)
                            startActivity(intent)
                        }
                    }
                    else if (it.page == "next") {
                        nextButton.visibility = View.VISIBLE
                        nextButton.setOnClickListener {_ ->
                            // call this activity again with the next page
                            val intent = Intent(this@EpisodeCommentsActivity, EpisodeCommentsActivity::class.java)
                            intent.putExtra("episodeComments", it.url)
                            startActivity(intent)
                        }
                    }
                }
            }
        }

        // go back to main activity when back button is pressed
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.comments_settings, menu)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        menu.findItem(R.id.translate_comments).isChecked = sharedPref.getBoolean("translate_comments", false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuDataMap = mapOf(
            R.id.translate_comments to Pair("translate_comments") { isChecked: Boolean ->
                adapter.commentSettings.copy(translate = isChecked)
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

    private fun updateAdapterSettings(settings: CommentSettings?) {
        settings?.let {
            adapter.commentSettings = it
            adapter?.notifyDataSetChanged()
        }
    }

    private fun savePreference(key: String, value: Boolean) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean(key, value).apply()
    }




    // new instance of the activity
}