package com.example.ronitawidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.RemoteViews

class MyWidget : AppWidgetProvider() {

    companion object {
        const val PREFS_NAME = "com.example.ronitawidget.MyWidget"
        const val PREF_PREFIX_KEY = "value_"
        const val ADD_ACTION = "com.example.ronitawidget.ADD_ACTION"
        const val SUB_ACTION = "com.example.ronitawidget.SUB_ACTION"

        private fun getValue(context: Context, appWidgetId: Int): Int {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val value = prefs.getInt(PREF_PREFIX_KEY, 0)
            //val value = prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 0)
            println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHH getValue $value $appWidgetId")
            return value
        }

        private fun saveValue(context: Context, appWidgetId: Int, value: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            with(prefs.edit()) {
                putInt(PREF_PREFIX_KEY , value)
               // putInt(PREF_PREFIX_KEY + appWidgetId, value)
                apply()
            }
            println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHH saveValue $value  $appWidgetId")
        }

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val value = getValue(context, appWidgetId)
            val views = RemoteViews(context.packageName, R.layout.widget_layout).apply {
                setTextViewText(R.id.valueText, value.toString())
                setOnClickPendingIntent(R.id.Add, createPendingIntent(context, appWidgetId, ADD_ACTION, 0))
                setOnClickPendingIntent(R.id.sub, createPendingIntent(context, appWidgetId, SUB_ACTION, 1000))
                setOnClickPendingIntent(R.id.openButton, PendingIntent.getActivity(context, appWidgetId + 2000,
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")), PendingIntent.FLAG_IMMUTABLE))
                println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHH  updateAppWidget $value $appWidgetId")
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun createPendingIntent(context: Context, appWidgetId: Int, action: String, requestCodeOffset: Int): PendingIntent {
            val intent = Intent(context, MyWidget::class.java).apply {
                this.action = action
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHH createPendingIntent $appWidgetId")
            }
            return PendingIntent.getBroadcast(context, appWidgetId + requestCodeOffset, intent, PendingIntent.FLAG_IMMUTABLE)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHH onUpdate  ")
        appWidgetIds.forEach{ appWidgetId ->
            // Save the widget ID
            saveAppWidgetId(context, appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    private fun saveAppWidgetId(context: Context, appWidgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        with(prefs.edit()) {
            putInt("app_widget_id", appWidgetId)
            apply()
        }
    }
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action in listOf(ADD_ACTION, SUB_ACTION)) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val value = getValue(context, appWidgetId)
                val newValue = if (intent.action == ADD_ACTION) value + 1 else value - 1
               println ("HHHHHHHHHHHHHHHHHHHHHHHHHHHHH onReceive $value $newValue ")
                saveValue(context, appWidgetId, newValue)
                updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId)
            }
        }
    }
}