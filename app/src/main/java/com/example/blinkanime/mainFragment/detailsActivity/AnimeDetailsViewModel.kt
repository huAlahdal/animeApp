package com.example.blinkanime.mainFragment.detailsActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blinkanime.mainFragment.AnimeDetails
import com.example.blinkanime.mainFragment.AnimeScraper
import com.example.blinkanime.misc.MALScraper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnimeDetailsViewModel(private val animeLink: String, private val isMal: Boolean = false) : ViewModel() {
    private val _animeDetails = MutableLiveData<List<AnimeDetails>>().apply {
        CoroutineScope(Dispatchers.IO).launch {
            if (isMal) {
                val data = MALScraper().getAnime(animeLink.toInt())
                withContext(Dispatchers.Main) {
                    postValue(data?: emptyList())
                }
            } else
            AnimeScraper().getAnimeDetalisPage(animeLink) {
                postValue(it?: emptyList())
            }
        }
    }

    val animeDetails: LiveData<List<AnimeDetails>> = _animeDetails

}