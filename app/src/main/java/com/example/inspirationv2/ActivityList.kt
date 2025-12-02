package com.example.inspirationv2

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ActivityList(var list_name: String, var context: Context) {
    private var _list = mutableListOf<String>()
    private val filename = "$list_name.txt"
    private val list_dir = File(context.filesDir, "lists")
    private val file = File(list_dir, filename)

    init{
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
        val inputAsString = FileInputStream(file).bufferedReader().use { it.readText() }
        _list = mutableListOf<String>()
        inputAsString.split("\n").forEach {
            if (it != "") {
                _list.add(it)
            }
        }
    }

    fun all(): MutableList<String> {
        return _list
    }

    fun get(): String {
        // TODO: improve to not return the same thing twice?
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
}