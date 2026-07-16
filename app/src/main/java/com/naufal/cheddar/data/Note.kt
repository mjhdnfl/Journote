package com.naufal.cheddar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val isArchived: Boolean = false,
    val isTrashed: Boolean = false,
    val trashedTimestamp: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)