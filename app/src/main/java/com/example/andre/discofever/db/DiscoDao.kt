package com.example.andre.discofever.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface DiscoDao {

    @Insert
    fun insert(disco: Disco)

    @Update
    fun update(disco: Disco)

    @Delete
    fun delete(disco: Disco)

    @Query ("DELETE FROM disco_table")
    fun deleteAll()

    @Query ("SELECT * FROM disco_table")
    fun getAll():LiveData<List<Disco>>

}