package com.animegen.app.util

import org.junit.Test
import org.junit.Before
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContentFilterTest {

    private lateinit var contentFilter: ContentFilter

    @Before
    fun setup() {
        contentFilter = ContentFilter()
    }

    @Test
    fun testAppropriatePrompt() {
        val prompt = "A beautiful anime girl with blue eyes and long silver hair, fantasy setting"
        assertTrue(contentFilter.isContentAppropriate(prompt))
    }

    @Test
    fun testViolentContent() {
        val prompt = "Anime character killing someone with blood and gore"
        assertFalse(contentFilter.isContentAppropriate(prompt))
    }

    @Test
    fun testSexualContent() {
        val prompt = "Nude anime girl in sexual pose"
        assertFalse(contentFilter.isContentAppropriate(prompt))
    }

    @Test
    fun testMinorWithSexualContent() {
        val prompt = "Child in adult situations"
        assertFalse(contentFilter.isContentAppropriate(prompt))
    }

    @Test
    fun testGetViolationReason() {
        val prompt = "Murder anime scene"
        val reason = contentFilter.getViolationReason(prompt)
        assertTrue(reason != null)
        assertTrue(reason?.contains("violent", ignoreCase = true) ?: false)
    }

    @Test
    fun testCaseSensitivity() {
        val prompt = "VIOLENCE is bad"
        assertFalse(contentFilter.isContentAppropriate(prompt))
    }

    @Test
    fun testSanitizePrompt() {
        val prompt = "Violence and hate speech"
        val sanitized = contentFilter.sanitizePrompt(prompt)
        assertTrue(sanitized.contains("***"))
        assertFalse(sanitized.contains("Violence", ignoreCase = true))
    }
}
