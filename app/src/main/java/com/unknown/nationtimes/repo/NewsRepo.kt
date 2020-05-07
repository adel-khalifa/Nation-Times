package com.unknown.nationtimes.repo

import com.unknown.nationtimes.data.database.DataBaseInstance
import com.unknown.nationtimes.network.RetrofitInstance

class NewsRepo(val mDataBaseInstance: DataBaseInstance) {
    // fetching data from this endpoint ("v2/everything")
    suspend fun getEverythingFromApi(queryHashMapForEverything: HashMap<String, String>) =
        RetrofitInstance.mApi.getEverything(queryHashMapForEverything)

    suspend fun getTopHeadLinesFromApi(queryHashMapForTopHeadLines: HashMap<String, String>) =
        RetrofitInstance.mApi.getTopHeadLines(queryHashMapForTopHeadLines)


}