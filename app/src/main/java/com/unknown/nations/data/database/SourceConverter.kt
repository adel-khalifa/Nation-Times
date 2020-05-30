package com.unknown.nations.data.database

import androidx.room.TypeConverter
import com.unknown.nations.data.models.Source


class SourceConverter {
    /* takes an string and return a source object contains that string
    and we can pass a string to id field (because its type is any ,,, therefore we don't care about it
    because sometimes is equals to null from our response
    */
    @TypeConverter
    fun stringToSource(name: String) = Source(name, name)

    // takes source object and return it's own name as String
    @TypeConverter
    fun sourceToString(source: Source) = source.name
}