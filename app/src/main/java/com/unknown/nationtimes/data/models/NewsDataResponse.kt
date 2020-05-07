package com.unknown.nationtimes.data.models


import com.unknown.nationtimes.data.models.Article

data class NewsDataResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)