package com.example.blinkanime.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.Toast
import com.example.blinkanime.R

class LatestEpisodesWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        val intent = Intent(context, StackWidgetService::class.java)
        views.setRemoteAdapter(R.id.list_view, intent)
        views.setEmptyView(R.id.list_view, R.id.list_view)

        val appIntent = Intent(context, LatestEpisodesWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_IDS,
                appWidgetManager.getAppWidgetIds(ComponentName(context, LatestEpisodesWidget::class.java))
            )
        }

        val appPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            appIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.refresh_button, appPendingIntent)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            // Update widget view
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context!!, LatestEpisodesWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view)
        }
    }
}
