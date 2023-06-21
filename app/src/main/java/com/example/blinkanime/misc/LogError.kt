package com.example.blinkanime.misc

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat.getExternalFilesDirs
import androidx.core.content.ContextCompat.startActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LogError {

    fun logError(context: Context, errorMessage: String) {
        val date = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(Date())
        // path in the internal storage directory
        val downloadFolder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        Log.e("ErrorLogger", downloadFolder.toString())
        val logFile = File(downloadFolder, "error_$date.txt")

        // create the log file if it doesn't exist
        if (!logFile.exists()) {
            logFile.createNewFile()
        }

        // get the current date and time
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // write the error message and timestamp to the log file
        logFile.appendText("[$timestamp] ERROR: $errorMessage\n")

        // log the error to the system log as well
        Log.e("ErrorLogger", errorMessage)
    }

}