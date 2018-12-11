package com.example.andre.discofever.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.launch

@Database(entities = [Disco::class], version = 4)
abstract class DiscoDatabase : RoomDatabase(){

    abstract fun discoDAO():DiscoDao

    companion object {

        @Volatile
        private var INSTANCE: DiscoDatabase? = null
        fun getDatabase(context: Context, scope: CoroutineScope):DiscoDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiscoDatabase::class.java,
                    "disco-database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DiscoDatabaseCallback(scope))
                    .build()
                    INSTANCE = instance
                instance
            }
        }

    }

    private class DiscoDatabaseCallback(
        private val scope:CoroutineScope
    ): RoomDatabase.Callback(){

        override fun onOpen(db: SupportSQLiteDatabase){
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch (Dispatchers.IO){
                    populaTabela(database.discoDAO())
                }
            }
        }

        fun populaTabela(discoDao: DiscoDao){

        }
    }
}