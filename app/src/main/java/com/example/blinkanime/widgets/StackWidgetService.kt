package com.example.blinkanime.widgets

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.example.blinkanime.R
import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.mainFragment.AnimeScraper
import com.squareup.picasso.Picasso


// StackWidgetService.kt
class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext)
    }
}

class StackRemoteViewsFactory(
    private val context: Context
) : RemoteViewsFactory {
    private val items = mutableListOf<Anime>()

    override fun onCreate() {
        updateData()
    }

    private fun updateData() {
        AnimeScraper().getLatestEpisodes {
            if (it != null) {
                items.clear()
                items.addAll(it)
            }
        }
    }

    override fun onDataSetChanged() {
        updateData()
    }

    override fun onDestroy() {}

    override fun getCount(): Int = items.size

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_view_item)
        if (position in 0 until items.size) {
            views.setTextViewText(R.id.episode_title, items[position].name)
            views.setTextViewText(R.id.episode_number, items[position].episodeNumber)
            views.setImageViewBitmap(R.id.anime_image, Picasso.get().load(items[position].image).get())
        }
        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}

