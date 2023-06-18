package com.example.blinkanime

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.mainFragment.AnimeScraper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {
    private val _animeList = MutableLiveData<List<Anime>>().apply {
        value = emptyList()
    }

    private val _loadingData = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _animeSite = MutableLiveData<String>().apply {
        value = "https://witanime.com/"
    }

    val animeList = _animeList
    val loadingData = _loadingData
    val animeSite = _animeSite

    // update anime list
    fun updateAnimeList(newData: List<Anime>) {
        _animeList.value = newData
    }
    fun updateLoadingData(newData: Boolean) {
        _loadingData.value = newData
    }
    fun updateAnimeSite(newData: String) {
        when (newData) {
            "witanime" -> _animeSite.value = "https://witanime.com/"
            "anime4up" -> _animeSite.value = "https://w1.anime4up.tv/"
            else -> _animeSite.value = "https://witanime.com/"
        }
    }

    // refresh anime list
    fun refresh() {
        if (_loadingData.value == true) return
        _loadingData.value = true
        CoroutineScope(Dispatchers.IO).launch {
            _animeList.postValue(emptyList())
            val animeList = AnimeScraper().getAllAnimes()
            _animeList.postValue(animeList)
            _loadingData.postValue(false)
        }
    }
}