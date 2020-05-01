package com.unknown.nationtimes.network

import com.unknown.nationtimes.data.NewsDataResponse
import com.unknown.nationtimes.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiInterface {
    //https://newsapi.org/v2/everything?q=bitcoin&apiKey=a0b4079842044971b4eb02d179aaeaa5

    @GET("v2/everything")
    suspend fun getEverything(
        @Query("q") WantSearchFor: String,
        @Query("page") pages: Int = 1,
        @Query("language") language: String = "en",
        @Query("apiKey") key: String = Constants.API_KEY


    ) : Response<NewsDataResponse>

    @GET("v2/top-headlines")
    suspend fun getTopHeadLines(
        @Query("q") WantSearchFor: String,
        @Query("page") pages: Int = 1,
        @Query("language") language: String = "en",
        @Query("apiKey") key: String = Constants.API_KEY


    ) : Response<NewsDataResponse>



}