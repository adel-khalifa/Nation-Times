package com.unknown.nationtimes.data.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.unknown.nationtimes.util.Constants

@Entity(
    tableName = Constants.ARTICLE_TABLE_NAME
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String
)