package com.unknown.nations.data.models


data class NewsDataResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)