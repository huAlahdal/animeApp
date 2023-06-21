package com.example.blinkanime.misc

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.blinkanime.MainActivity
import com.example.blinkanime.R
import com.example.blinkanime.mainFragment.AnimeScraper
import com.example.blinkanime.mainFragment.MainPageFragment
import com.example.blinkanime.newsFragment.NewsPageFragment
import com.example.blinkanime.widgets.LatestEpisodesWidget
import com.example.blinkanime.widgets.StackWidgetService
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class AnimeWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        helper = NotificationHelper(applicationContext, "MainFragment", "New episode", "latest episodes")
        newsHelper = NotificationHelper(applicationContext, "NewsFragment", "New News", "latest News")

        MainPageFragment.mainFragmentViewModel.refresh(true, helper)
        NewsPageFragment.newsViewModel.refresh(true, newsHelper)

        // Notify the widget to update by sending a broadcast
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(applicationContext, StackWidgetService::class.java))
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view)

        return Result.success()
    }

    init {
        val workRequest = OneTimeWorkRequestBuilder<AnimeWorker>()
            .setInitialDelay(3, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }

    companion object {
        var helper: NotificationHelper? = null
        var newsHelper: NotificationHelper? = null
    }
}

fun scheduleInitialWork(context: Context) {
    val workRequest = OneTimeWorkRequestBuilder<AnimeWorker>()
        .setInitialDelay(3, TimeUnit.MINUTES)
        .build()
    WorkManager.getInstance(context).enqueue(workRequest)
}