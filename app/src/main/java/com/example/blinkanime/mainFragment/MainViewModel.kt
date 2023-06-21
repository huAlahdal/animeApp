package com.example.blinkanime.mainFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blinkanime.misc.MALAnimeData
import com.example.blinkanime.misc.NotificationHelper

data class MALEpisode(
    val epNumber: String,
    val epTitle: String,
    val dateAired: String,
    val epScore: String,
    val epCommentsCount: String = "",
    val epCommentsLink: String = ""
)

data class Episode(val name: String, val link:String, val image: String, var extraData: MALEpisode?)
data class Anime(
    val name: String,
    val image: String,
    val link: String,
    val episodeNumber: String = "",
    val description: String = "",
    val animeLink: String = "")

data class AnimeDetails(
    val name: String?,
    val image: String? = "",
    val episodes: List<Episode>? = emptyList(),
    val description: String? = "",
    val genres: List<String>? = emptyList(),
    val type: String? = "",
    val status: String? = "",
    val length: String? = "",
    val year: String? = "",
    val totalEp: String? = "",
    val season: String? = "",
    val source: String? = "",
    val trailer: String? = "",
    val MAL_Link: String? = "",
    val malDetails: MalDetails? = null,
)

data class MalDetails(
    val MALId: String,
    val MALName: String,
    val description: String,
    val score: String,
    val ranked: String,
    val popularity: String,
    val studio: String,
    val episodesLink: String,
)



class MainViewModel : ViewModel() {
    private val _latestEpisodes = MutableLiveData<List<Anime>>().apply {
        AnimeScraper().getLatestEpisodes { episodes ->
            if (episodes != null) {
                postValue(episodes)
            }
        }
    }

    private val _seasonAnime = MutableLiveData<List<Anime>>().apply {
        AnimeScraper().getSeasonalAnimes { seasonAnime ->
            if (seasonAnime != null) {
                postValue(seasonAnime)
            }
        }
    }

    private val _mostWatched = MutableLiveData<List<Anime>>().apply {
        AnimeScraper().getMostWatched { mostWachedAnimes ->
            if (mostWachedAnimes != null) {
                postValue(mostWachedAnimes)
            }
        }
    }

    val seasonAnime: LiveData<List<Anime>> get() = _seasonAnime
    val latestEpisodes: LiveData<List<Anime>> get() = _latestEpisodes
    val mostWatched: LiveData<List<Anime>> get() = _mostWatched

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState


    fun loadData(callback: () -> Unit) {
        // Make network requests and update LiveData here
        AnimeScraper().getLatestEpisodes { episodes ->
            if (episodes != null) {
                _latestEpisodes.postValue(episodes)
            }
        }
        AnimeScraper().getSeasonalAnimes { seasonAnime ->
            if (seasonAnime != null) {
                _seasonAnime.postValue(seasonAnime)
            }
        }
        AnimeScraper().getMostWatched { mostWachedAnimes ->
            if (mostWachedAnimes != null) {
                _mostWatched.postValue(mostWachedAnimes)
            }
        }

        // When data is loaded, call callback function
        // Here we assume that all data is loaded at the same time
        _latestEpisodes.observeForever {
            if (_seasonAnime.value != null && _mostWatched.value != null) {
                callback()
                _latestEpisodes.removeObserver {}
                _seasonAnime.removeObserver {}
                _mostWatched.removeObserver {}
            }
        }
    }


    fun refresh(sendNotify: Boolean = false, helper: NotificationHelper? = null) {
        AnimeScraper().getLatestEpisodes { latestEp ->
            if (sendNotify && latestEpisodes.value != null) {
                val latestId = latestEp?.firstOrNull()?.name ?: return@getLatestEpisodes
                val newsIds = latestEpisodes.value!!.map { it.name }
                if (latestId !in newsIds) {
                    sendNotificationForLatestEpisode(helper!!, latestEp[0])
                }
            }
            _latestEpisodes.postValue(latestEp)
        }

        AnimeScraper().getSeasonalAnimes { seasonAnime ->
            _seasonAnime.postValue(seasonAnime)
        }
        AnimeScraper().getMostWatched { mostWachedAnimes ->
            _mostWatched.postValue(mostWachedAnimes)
        }
    }

    private fun sendNotificationForLatestEpisode(
        notificationHelper: NotificationHelper,
        latestEp: Anime,
    ) {
        notificationHelper.sendNotification(latestEp.name, latestEp.episodeNumber, latestEp.image)
    }


}