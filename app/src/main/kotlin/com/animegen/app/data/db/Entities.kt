package com.animegen.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prompt: String,
    val imagePath: String,
    val timestamp: Long = System.currentTimeMillis(),
    val tags: String = "", // Comma-separated tags
    val isFavorite: Boolean = false,
    val cloudSynced: Boolean = false,
    val cloudId: String? = null
) : Serializable

@Entity(tableName = "generation_history")
data class GenerationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prompt: String,
    val status: String, // "pending", "success", "failed"
    val createdAt: Long = System.currentTimeMillis(),
    val errorMessage: String? = null,
    val imageId: Long? = null // FK to ImageEntity
) : Serializable
