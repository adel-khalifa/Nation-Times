package com.unknown.nations.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.unknown.nations.data.models.Article
import com.unknown.nations.util.Constants


@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(SourceConverter::class)
abstract class DataBaseInstance() : RoomDatabase() {
    // Room will implement this interface by its own way
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