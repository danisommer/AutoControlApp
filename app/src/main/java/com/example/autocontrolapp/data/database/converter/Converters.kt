package com.example.autocontrolapp.data.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {

    // Converter List<String> <-> JSON
    @TypeConverter
    fun fromListToJson(value: List<String>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromJsonToList(value: String?): List<String>? {
        if (value == null) return null
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    // Converter Date <-> Long (timestamp)
    @TypeConverter
    fun fromDateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromTimestampToDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }
}
