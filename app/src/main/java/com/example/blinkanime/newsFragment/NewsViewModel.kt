package com.example.blinkanime.newsFragment

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blinkanime.MainActivity
import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.misc.NotificationHelper
import com.example.blinkanime.scheduleFragment.AnimeSchedule
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val _NewsData = MutableLiveData<List<Tweet>>().apply {
        AnimeNewsAPI().getTweets { tweets ->
            if (tweets != null) {
                value = tweets
            }
        }
    }


    val newsData: LiveData<List<Tweet>> = _NewsData


    private fun updateScheduleData(newData: List<Tweet>) {
        _NewsData.postValue(newData)
    }

        fun refresh(sendNotify: Boolean = false, helper: NotificationHelper? = null) {
            AnimeNewsAPI().getTweets { tweets ->
                if (tweets != null) {
                    val latestName = tweets.firstOrNull()?.id ?: 0
                    if (latestName !in newsData.value!!.map { it.id }) {
                    updateScheduleData(tweets)
                        if (sendNotify) {
                            sendNotificationForLatestEpisode(helper!!, tweets[0])
                        }
                    }
                }
            }
        }

        private fun sendNotificationForLatestEpisode(notificationHelper: NotificationHelper, news: Tweet) {
            val image = news.entities.media?.firstOrNull()?.media_url_https?.let { "$it?format=jpg&name=small" }
            notificationHelper.sendNotification("Anime News", news.full_text, image)
        }


}