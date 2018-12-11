package com.example.andre.discofever.repository

import android.arch.lifecycle.LiveData
import com.example.andre.discofever.db.Disco
import com.example.andre.discofever.db.DiscoDao

class DiscoRepository(private val discoDao: DiscoDao) {

    val allDiscos : LiveData<List<Disco>> = discoDao.getAll()

    fun insert(disco: Disco){
        discoDao.insert(disco)
    }

    fun update(disco: Disco){
        discoDao.update(disco)
    }

    fun delete(disco: Disco){
        discoDao.delete(disco)
    }
}