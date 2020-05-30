package com.unknown.nations.network

import com.unknown.nations.data.models.NewsDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface NewsApiInterface {
    // https://newsapi.org/v2/everything?q=bitcoin&apiKey=a0b4079842044971b4eb02d179aaeaa5

    @GET("v2/everything")
    suspend fun getEverything(
        @QueryMap queryMapForEverything :  HashMap<String , String>
//       @Query("q") WantSearchFor: String ,
//        @Query("page") pages: Int = 1 ,
//        @Query("language") language: String? =null
//        @Query("apiKey") key: String = Constants.API_KEY
 ) : Response<NewsDataResponse>

    @GET("v2/top-headlines")
    suspend fun getTopHeadLines(
        @QueryMap queryMapForTopHeadLines :  HashMap<String , String>

//        @Query("country") country : String,
//        @Query("page") pages : Int = 1,
//        @Query("q") WantSearchFor: String? = null,
//        @Query("language") language: String? = null
//        @Query("apiKey") key: String = Constants.API_KEY

    ) : Response<NewsDataResponse>



}