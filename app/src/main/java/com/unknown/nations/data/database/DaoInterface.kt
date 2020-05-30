package com.unknown.nations.data.database


import androidx.lifecycle.LiveData
import androidx.room.*
import com.unknown.nations.data.models.Article
import com.unknown.nations.util.Constants

@Dao
interface DaoInterface {

// insert or update(if it Conflict) Article
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOrInsertToDataBase(article: Article)

    @Query("SELECT * FROM " + Constants.ARTICLE_TABLE_NAME )
    fun getAllArticlesFromDataBase() : LiveData<List<Article>>

    @Delete
    suspend fun deleteThisArticleFromDataBase(article: Article)

    @Query("SELECT * FROM ${Constants.ARTICLE_TABLE_NAME} WHERE url = :articleUrl " )
     fun lookForThisArticle(articleUrl: String) : LiveData<List<Article>>
}