package com.example.inspirationv2

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import java.io.File

class MyApp: Application() {
    companion object {
        var lists = mutableStateListOf<ActivityList>()
    }

    override fun onCreate() {
        super.onCreate()
        lists.clear()
        // Read list directory
        val listDir = File(filesDir, "lists")
        if (!listDir.exists()) {
            listDir.mkdir()
        } else {
            listDir.listFiles()?.forEach { 
                if(it.isFile && it.extension == "txt") {
                    lists.add(ActivityList(it.nameWithoutExtension, this))
                }
            }
        }
    }
}