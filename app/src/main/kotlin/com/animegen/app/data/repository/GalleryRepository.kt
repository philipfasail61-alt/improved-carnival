package com.animegen.app.data.repository

import com.animegen.app.data.db.AppDatabase
import com.animegen.app.data.db.ImageEntity
import com.animegen.app.util.DatabaseException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryRepository @Inject constructor(
    private val database: AppDatabase
) {

    fun getAllImages(): Flow<List<ImageEntity>> {
        return database.imageDao().getAllImages()
    }

    fun getFavoriteImages(): Flow<List<ImageEntity>> {
        return database.imageDao().getFavoriteImages()
    }

    fun getImagesByTag(tag: String): Flow<List<ImageEntity>> {
        return database.imageDao().getImagesByTag(tag)
    }

    fun getImagesPaginated(limit: Int, offset: Int): Flow<List<ImageEntity>> {
        return database.imageDao().getImagesPaginated(limit, offset)
    }

    suspend fun getImageById(imageId: Long): ImageEntity? {
        return try {
            database.imageDao().getImageById(imageId)
        } catch (e: Exception) {
            throw DatabaseException("Failed to fetch image with ID $imageId", e)
        }
    }

    suspend fun toggleFavorite(imageId: Long): Boolean {
        return try {
            val image = database.imageDao().getImageById(imageId)
                ?: throw DatabaseException("Image with ID $imageId not found")

            val updated = image.copy(isFavorite = !image.isFavorite)
            database.imageDao().updateImage(updated)
            updated.isFavorite
        } catch (e: Exception) {
            throw DatabaseException("Failed to toggle favorite for image $imageId", e)
        }
    }

    suspend fun deleteImage(imageId: Long): Boolean {
        return try {
            val image = database.imageDao().getImageById(imageId)
                ?: throw DatabaseException("Image with ID $imageId not found")

            database.imageDao().deleteImage(image)
            true
        } catch (e: Exception) {
            throw DatabaseException("Failed to delete image with ID $imageId", e)
        }
    }

    suspend fun updateImageTags(imageId: Long, tags: String): Boolean {
        return try {
            val image = database.imageDao().getImageById(imageId)
                ?: throw DatabaseException("Image with ID $imageId not found")

            val updated = image.copy(tags = tags)
            database.imageDao().updateImage(updated)
            true
        } catch (e: Exception) {
            throw DatabaseException("Failed to update tags for image $imageId", e)
        }
    }

    fun getImageCount(): Flow<Int> {
        return database.imageDao().getImageCount()
    }

    suspend fun deleteAllImages(): Boolean {
        return try {
            database.imageDao().deleteAllImages()
            true
        } catch (e: Exception) {
            throw DatabaseException("Failed to delete all images", e)
        }
    }

    suspend fun markSynced(imageId: Long, cloudId: String): Boolean {
        return try {
            val image = database.imageDao().getImageById(imageId)
                ?: throw DatabaseException("Image with ID $imageId not found")

            val updated = image.copy(cloudSynced = true, cloudId = cloudId)
            database.imageDao().updateImage(updated)
            true
        } catch (e: Exception) {
            throw DatabaseException("Failed to mark image as synced", e)
        }
    }
}
