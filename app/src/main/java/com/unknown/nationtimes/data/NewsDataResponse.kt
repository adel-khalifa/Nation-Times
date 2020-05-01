package com.unknown.nationtimes.data


import com.google.gson.annotations.SerializedName

data class NewsDataResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)