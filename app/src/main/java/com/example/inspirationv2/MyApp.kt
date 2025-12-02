package com.example.inspirationv2

import android.app.Application
import java.io.File

class MyApp: Application() {
    companion object {
        var lists = mutableListOf<ActivityList>()
    }

    override fun onCreate() {
        super.onCreate()

        // Read list directory
        val listDir = File(filesDir, "lists")
        if (!listDir.exists()) {
            listDir.mkdir()
        } else {
            listDir.listFiles()?.forEach {
                lists.add(ActivityList(it.nameWithoutExtension, this))
            }
        }
    }
}