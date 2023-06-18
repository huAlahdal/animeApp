package com.example.blinkanime.scheduleFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.mainFragment.AnimeScraper
import kotlinx.coroutines.launch

data class AnimeSchedule(val dayOfTheWeek: String, val animeData: List<Anime>)
class ScheduleViewModel : ViewModel() {
    private val _scheduleData = MutableLiveData<List<AnimeSchedule>>().apply {
        AnimeScraper().getAnimeSchedule { animeSchedule ->
            if (animeSchedule != null) {
                value = animeSchedule
            }
        }
    }


    val scheduleData: LiveData<List<AnimeSchedule>> = _scheduleData


    fun updateScheduleData(newData: List<AnimeSchedule>) {
        _scheduleData.postValue(newData)
    }

    fun refresh() {
        AnimeScraper().getAnimeSchedule { animeSchedule ->
            if (animeSchedule != null) {
                _scheduleData.postValue(animeSchedule)
            }
        }
    }
}