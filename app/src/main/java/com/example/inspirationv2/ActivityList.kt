package com.example.inspirationv2

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ActivityList(initialName: String, var context: Context) {
    var list_name by mutableStateOf(initialName)
    private var _list = mutableListOf<String>()
    private val list_dir = File(context.filesDir, "lists")
    private var file = File(list_dir, "$initialName.txt")

    init{
        if (!list_dir.exists()){
            list_dir.mkdir()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        read_file()
    }

    fun write_to_file() {
        FileOutputStream(file).use {
            for(option in _list){
                it.write("$option\n".toByteArray())
            }
        }
    }

    fun read_file() {
        if(file.exists()){
            val inputAsString = FileInputStream(file).bufferedReader().use { it.readText() }
            _list = mutableListOf<String>()
            inputAsString.split("\n").forEach {
                if (it.isNotEmpty()) {
                    _list.add(it)
                }
            }
        }
    }

    fun all(): MutableList<String> {
        return _list
    }

    fun get(): String {
        if(_list.isEmpty()){
            return ""
        }
        return _list.random()
    }

    fun size(): Int {
        return _list.size
    }

    fun add(s: String) {
        _list.add(s)
        write_to_file()
    }

    fun remove(s: String) {
        _list.remove(s)
        write_to_file()
    }

    fun rename(newName: String) {
        val newFile = File(list_dir, "$newName.txt")
        if (file.renameTo(newFile)) {
            list_name = newName
            file = newFile
        }
    }

    fun deleteFile() {
        if(file.exists()){
            file.delete()
        }
    }
}