package com.animegen.app.util

import org.junit.Test
import org.junit.Before
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResultTest {

    @Test
    fun testSuccessResult() {
        val result = Result.Success("data")
        assertTrue(result.isSuccess())
        assertFalse(result.isError())
        assertFalse(result.isLoading())
        assertEquals("data", result.getOrNull())
    }

    @Test
    fun testErrorResult() {
        val exception = Exception("Test error")
        val result = Result.Error(exception, "Test message")
        assertFalse(result.isSuccess())
        assertTrue(result.isError())
        assertFalse(result.isLoading())
        assertEquals(null, result.getOrNull())
    }

    @Test
    fun testLoadingResult() {
        val result = Result.Loading
        assertFalse(result.isSuccess())
        assertFalse(result.isError())
        assertTrue(result.isLoading())
    }

    @Test
    fun testGetOrThrow() {
        val successResult = Result.Success("data")
        assertEquals("data", successResult.getOrThrow())

        val errorResult = Result.Error(Exception("error"), "message")
        try {
            errorResult.getOrThrow()
            assertTrue(false, "Should have thrown")
        } catch (e: Exception) {
            assertTrue(true)
        }
    }
}
