package com.unknown.nationtimes.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.unknown.nationtimes.data.models.Article
import com.unknown.nationtimes.util.Constants


@Database(
    entities = [Article::class], version = 1
)
@TypeConverters(SourceConverter::class)
abstract class DataBaseInstance : RoomDatabase() {
    // returns the DAO implemented (by it's Own way ) interface
    abstract fun dataBaseDao(): DaoInterface


    companion object {
        @Volatile
        private var instance: DataBaseInstance? = null
        private val LOCK = Any()
        // invoke method to init db instant once it called and make sure it's initializing on a specific thread "LOCK"
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                DataBaseInstance::class.java,
                Constants.ARTICLE_DATABASE_NAME
            ).build()
        }


    }
}