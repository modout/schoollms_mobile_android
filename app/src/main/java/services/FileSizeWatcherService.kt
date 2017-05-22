package services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import net.schoollms.schoollmsregistration.InstallerActivity
import java.io.File
import java.util.*
import kotlin.NullPointerException


 class FileSizeWatcherService : IntentService("FileSizeWatcherService") {



    val TAG: String = "FileSizeWatcherService"

    override fun onCreate() {
        super.onCreate()
        val interval = 1000
        var time = 1 // time is seconds
        val timer = Timer()
        val dirPath1: File = File(cacheDir.parent).parentFile
        val dirPathGNU: File = File(dirPath1.absolutePath + "/" + InstallerActivity.GNU_PACKAGE)

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val byteGNUSize = folderSize(dirPathGNU)
                Log.d(TAG, "run: on Installer activity GNU: " + byteGNUSize / 1024 / 1024 )
                time += 1
                if (time == 120) timer.cancel() // stop timer after 10 seconds
            }
        }, 0, interval.toLong())


    }

    override fun onHandleIntent(intent: Intent) {

        for(i: Int in 1..10) {
            Log.d(TAG, "Hello there $i")
        }
    }

    fun folderSize(directory: File): Long {
        var length = 0L
        if(directory.exists()) {
           try {
               for (file in directory.listFiles()) {
                   Log.d(TAG, "files in here ${file.absolutePath}")
                   if (file.isFile)
                       length += file.length()
                   else
                       length += folderSize(file)
               }
           }catch (e: NullPointerException) {
               Log.e(TAG, "oops null pointer watch out ")
               Log.e(TAG, e.toString())
           }

        } else {
            Log.e(TAG, "Error whats the directory does not exist ")
            return 1
        }
        return length
    }
}
