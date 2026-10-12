package com.animegen.app.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity): Long

    @Query("SELECT * FROM images ORDER BY timestamp DESC")
    fun getAllImages(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE id = :imageId")
    suspend fun getImageById(imageId: Long): ImageEntity?

    @Query("SELECT * FROM images WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteImages(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE tags LIKE '%' || :tag || '%'")
    fun getImagesByTag(tag: String): Flow<List<ImageEntity>>

    @Update
    suspend fun updateImage(image: ImageEntity)

    @Delete
    suspend fun deleteImage(image: ImageEntity)

    @Query("DELETE FROM images")
    suspend fun deleteAllImages()

    @Query("SELECT COUNT(*) FROM images")
    fun getImageCount(): Flow<Int>

    @Query("SELECT * FROM images LIMIT :limit OFFSET :offset ORDER BY timestamp DESC")
    fun getImagesPaginated(limit: Int, offset: Int): Flow<List<ImageEntity>>
}

@Dao
interface GenerationHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: GenerationHistoryEntity): Long

    @Query("SELECT * FROM generation_history ORDER BY createdAt DESC")
    fun getAllHistory(): Flow<List<GenerationHistoryEntity>>

    @Query("SELECT * FROM generation_history WHERE status = :status ORDER BY createdAt DESC")
    fun getHistoryByStatus(status: String): Flow<List<GenerationHistoryEntity>>

    @Update
    suspend fun updateHistory(history: GenerationHistoryEntity)

    @Delete
    suspend fun deleteHistory(history: GenerationHistoryEntity)

    @Query("DELETE FROM generation_history")
    suspend fun deleteAllHistory()

    @Query("SELECT COUNT(*) FROM generation_history")
    fun getHistoryCount(): Flow<Int>
}
