package com.unknown.nations.repo

import com.unknown.nations.data.database.DataBaseInstance
import com.unknown.nations.data.models.Article
import com.unknown.nations.network.RetrofitInstance

class NewsRepo(private val mDataBaseInstance: DataBaseInstance) {
    // fetching data from this endpoint ("v2/everything")
    suspend fun getEverythingFromApi(queryHashMapForEverything: HashMap<String, String>) =
        RetrofitInstance.mApi.getEverything(queryHashMapForEverything)


    // fetching data from this endpoint ("v2/Top-HeadLines")
    suspend fun getTopHeadLinesFromApi(queryHashMapForTopHeadLines: HashMap<String, String>) =
        RetrofitInstance.mApi.getTopHeadLines(queryHashMapForTopHeadLines)

    //manage insertion or updating exist article in database
    suspend fun updateOrInsertArticle(article: Article) =
        mDataBaseInstance.dataBaseDao().updateOrInsertToDataBase(article)


    //retrieve all articles from database
    fun getAllArticles() = mDataBaseInstance.dataBaseDao().getAllArticlesFromDataBase()

    // delete desire article from database
    suspend fun deleteArticle(article: Article) =
        mDataBaseInstance.dataBaseDao().deleteThisArticleFromDataBase(article)

    fun lookForThisArticle(articleUrl : String ) = mDataBaseInstance.dataBaseDao().lookForThisArticle( articleUrl )

}