package com.example.andre.discofever.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "disco_table")
data class Disco (
    @ColumnInfo(name = "etAlbum")
    var etAlbum: String,

    @ColumnInfo(name = "etArtista")
    var etArtista: String

    ):Serializable{
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}