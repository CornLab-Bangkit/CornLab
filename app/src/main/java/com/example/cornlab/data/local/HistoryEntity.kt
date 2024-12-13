package com.example.cornlab.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Entity(tableName = "history_analyze")
@Parcelize
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "imageUri")
    var imageUri: String,

    @ColumnInfo(name = "result")
    var result: String? = null,

    @ColumnInfo(name = "suggestion")
    var suggestion: String? = null,

    @ColumnInfo(name = "createdAt")
    val createdAt: String? = null
) : Parcelable