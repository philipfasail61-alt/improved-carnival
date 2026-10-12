package com.animegen.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.animegen.app.data.db.AppDatabase
import com.animegen.app.data.db.ImageEntity
import com.animegen.app.util.ContentFilter
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class IntegrationTests {

    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun testImageDatabaseOperations() = runTest {
        val imageEntity = ImageEntity(
            prompt = "A beautiful anime character",
            imagePath = "/path/to/image.png",
            timestamp = System.currentTimeMillis()
        )

        // Insert
        val insertedId = database.imageDao().insertImage(imageEntity)
        assertTrue(insertedId > 0)

        // Retrieve
        val retrieved = database.imageDao().getImageById(insertedId)
        assertNotNull(retrieved)
        assertEquals(imageEntity.prompt, retrieved.prompt)

        // Update
        val updated = retrieved.copy(isFavorite = true)
        database.imageDao().updateImage(updated)

        val retrievedUpdated = database.imageDao().getImageById(insertedId)
        assertTrue(retrievedUpdated?.isFavorite ?: false)

        // Delete
        database.imageDao().deleteImage(updated)
        val deleted = database.imageDao().getImageById(insertedId)
        assertEquals(null, deleted)
    }

    @Test
    fun testGenerationHistoryDatabase() = runTest {
        val historyEntity = com.animegen.app.data.db.GenerationHistoryEntity(
            prompt = "Test prompt",
            status = "pending"
        )

        val insertedId = database.generationHistoryDao().insertHistory(historyEntity)
        assertTrue(insertedId > 0)

        val retrieved = database.generationHistoryDao().getHistoryByStatus("pending")

        retrieved.collect { histories ->
            assertTrue(histories.isNotEmpty())
            assertEquals("pending", histories.first().status)
        }
    }

    @Test
    fun testContentFilterIntegration() {
        val contentFilter = ContentFilter()

        // Test appropriate content
        assertTrue(contentFilter.isContentAppropriate("Anime girl with blue eyes"))

        // Test inappropriate content
        assertTrue(!contentFilter.isContentAppropriate("Violence and gore"))

        // Test violation reason
        val reason = contentFilter.getViolationReason("Kill someone")
        assertNotNull(reason)
        assertTrue(reason!!.isNotEmpty())
    }

    @Test
    fun testMultipleImageOperations() = runTest {
        val images = listOf(
            ImageEntity(prompt = "Image 1", imagePath = "/path/1.png"),
            ImageEntity(prompt = "Image 2", imagePath = "/path/2.png"),
            ImageEntity(prompt = "Image 3", imagePath = "/path/3.png")
        )

        // Insert multiple
        images.forEach { database.imageDao().insertImage(it) }

        // Retrieve all
        val allImages = mutableListOf<ImageEntity>()
        database.imageDao().getAllImages().collect { images ->
            allImages.addAll(images)
        }

        assertEquals(3, allImages.size)
    }

    @Test
    fun testImagePagination() = runTest {
        // Insert 10 images
        repeat(10) { i ->
            database.imageDao().insertImage(
                ImageEntity(
                    prompt = "Image $i",
                    imagePath = "/path/$i.png"
                )
            )
        }

        // Test pagination
        val paginated = mutableListOf<ImageEntity>()
        database.imageDao().getImagesPaginated(5, 0).collect { images ->
            paginated.addAll(images)
        }

        assertEquals(5, paginated.size)

        paginated.clear()
        database.imageDao().getImagesPaginated(5, 5).collect { images ->
            paginated.addAll(images)
        }

        assertEquals(5, paginated.size)
    }

    @Test
    fun testFavoriteFiltering() = runTest {
        // Insert images with favorites
        database.imageDao().insertImage(
            ImageEntity(prompt = "Favorite", imagePath = "/path/1.png", isFavorite = true)
        )
        database.imageDao().insertImage(
            ImageEntity(prompt = "Not favorite", imagePath = "/path/2.png", isFavorite = false)
        )

        // Get favorites
        val favorites = mutableListOf<ImageEntity>()
        database.imageDao().getFavoriteImages().collect { images ->
            favorites.addAll(images)
        }

        assertEquals(1, favorites.size)
        assertEquals("Favorite", favorites.first().prompt)
    }
}
