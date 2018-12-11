package com.example.andre.discofever.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.example.andre.discofever.db.Disco
import com.example.andre.discofever.db.DiscoDatabase
import com.example.andre.discofever.repository.DiscoRepository
import kotlinx.coroutines.experimental.Job
import kotlin.coroutines.experimental.CoroutineContext
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main

class DiscoViewModel(application: Application) : AndroidViewModel(application) {
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    private val repository: DiscoRepository
    val allDiscos: LiveData<List<Disco>>

    init {
        val discoDao = DiscoDatabase.getDatabase(application, scope).discoDAO()

        repository = DiscoRepository(discoDao)
        allDiscos = repository.allDiscos
    }

    fun insert(disco: Disco) = scope.launch (Dispatchers.IO){
        repository.insert(disco)
    }

    fun update(disco: Disco) = scope.launch (Dispatchers.IO) {
        repository.update(disco)
    }

    fun delete(disco: Disco) = scope.launch (Dispatchers.IO){
        repository.delete(disco)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

}