
package com.example.ronitawidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println(" HHHHHHHHHHHHHHHHHHHHHHHHHHHHH onCreate ")
        setContentView(R.layout.activity_main)



        // Retrieve the appWidgetId dynamically
        val appWidgetId = getAppWidgetId()
        var value = getValue(this, appWidgetId)

        val textView: TextView = findViewById(R.id.textView)
       // textView.text = value.toString()
        println("MainActivity: Retrieved value from SharedPreferences: $value")

        val add: Button = findViewById(R.id.waadd);
        val sub: Button = findViewById(R.id.wasub);

        sub.setOnClickListener {
            value--
            saveValue(this, appWidgetId, value)
            textView.text = value.toString()
            sendBroadcastToUpdateWidget(appWidgetId)
        }
        add.setOnClickListener {
            value++
            saveValue(this, appWidgetId, value)
            textView.text = value.toString()
            sendBroadcastToUpdateWidget(appWidgetId)

        }
        val refreshButton: Button = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            value = getValue(this, appWidgetId)
            textView.text = value.toString()
        }




        // Youtube Feature
        val openButton: Button = findViewById(R.id.openButton)
        val editText: EditText = findViewById(R.id.editTextText)

        openButton.setOnClickListener {
            val searchText = editText.text.toString().trim()

            // Construct the YouTube search URL
            val searchQuery = Uri.encode(searchText)
            val youtubeSearchUrl = "https://www.youtube.com/results?search_query=$searchQuery"

            // Open the URL in a web browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeSearchUrl))
            startActivity(intent)
        }
        // Set padding to handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
       val value = getValue(this, -1 )
        println(" HHHHHHHHHHHHHHHHHHHHHHHHHHHHH onResume ")
        val textView: TextView = findViewById(R.id.textView)
        textView.text = value.toString()
    }
    private fun getValue(context: Context, appWidgetId: Int): Int {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(MyWidget.PREFS_NAME, 0)
        val value = sharedPreferences.getInt(MyWidget.PREF_PREFIX_KEY , 0)
        //val value = sharedPreferences.getInt(MyWidget.PREF_PREFIX_KEY + appWidgetId, 0)
        println("MainActivity: getValue() - Retrieved value: $value for appWidgetId: $appWidgetId")
        return value
    }
    private fun saveValue(context: Context, appWidgetId: Int, value: Int) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(MyWidget.PREFS_NAME, 0)
        with(sharedPreferences.edit()) {
            putInt(MyWidget.PREF_PREFIX_KEY , value)
           // putInt(MyWidget.PREF_PREFIX_KEY + appWidgetId, value)
            apply()
        }
    }

    private fun getAppWidgetId(): Int {
        val sharedPreferences: SharedPreferences = getSharedPreferences(MyWidget.PREFS_NAME, 0)
        return sharedPreferences.getInt("app_widget_id", 0) // Default value can be handled as needed
    }
    private fun sendBroadcastToUpdateWidget(appWidgetId: Int) {
        val intent = Intent(this, MyWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId)) // Use actual appWidgetIds here
        }
        sendBroadcast(intent)
    }

}