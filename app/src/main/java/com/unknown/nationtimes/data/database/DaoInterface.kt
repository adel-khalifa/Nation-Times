package com.unknown.nationtimes.data.database


import androidx.lifecycle.LiveData
import androidx.room.*
import com.unknown.nationtimes.data.models.Article
import com.unknown.nationtimes.util.Constants

@Dao
interface DaoInterface {

// insert or update(if it Conflict) Article
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOrInsert(article: Article)

    @Query("SELECT * FROM " + Constants.ARTICLE_TABLE_NAME )
    fun getArticlesFromDataBase() : LiveData<List<Article>>

    @Delete
    suspend fun deleteThisArticle(article: Article)
}