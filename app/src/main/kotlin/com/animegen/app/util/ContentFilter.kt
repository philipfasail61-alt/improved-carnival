package com.animegen.app.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentFilter @Inject constructor() {

    private val blockedKeywords = setOf(
        // Violence and harm
        "violence", "gore", "death", "kill", "murder", "torture", "abuse", "harm",
        "blood", "dismember", "stab", "shoot", "gun", "weapon", "attack", "rape",
        "sexual assault", "assault", "brutality", "beating", "hit",

        // Sexual content
        "sex", "porn", "nude", "naked", "nsfw", "xxx", "adult", "erotic",
        "sexual", "intercourse", "orgasm", "penis", "vagina", "breast",
        "copulate", "seduce", "horny", "arousal",

        // Hate speech and discrimination
        "hate", "racist", "racism", "sexist", "sexism", "antisemitic",
        "discriminate", "discrimination", "slur", "bigot", "nazi", "kkk",

        // Drug and alcohol abuse
        "cocaine", "heroin", "meth", "drug", "overdose", "addiction",
        "marijuana", "weed", "cannabis", "hashish", "molly", "ecstasy",

        // Underage exploitation
        "child", "kid", "minor", "baby", "toddler", "infant", "pedophile",
        "young", "underage", "loli", "shota", "cp", "csam"
    )

    private val minorIndicators = setOf(
        "child", "kid", "minor", "baby", "toddler", "infant", "teen",
        "young", "underage", "school", "daughter", "son"
    )

    private val sexualIndicators = setOf(
        "nude", "naked", "sex", "sexual", "erotic", "adult", "xxx",
        "penetr", "orgasm", "seduce", "horny", "breast", "penis", "vagina"
    )

    fun isContentAppropriate(prompt: String): Boolean {
        val lowerPrompt = prompt.lowercase()

        // Check for blocked keywords
        for (keyword in blockedKeywords) {
            if (lowerPrompt.contains(keyword)) {
                return false
            }
        }

        // TV-14 specific checks: block sexual content involving minors
        val hasMinorIndicator = minorIndicators.any { lowerPrompt.contains(it) }
        val hasSexualIndicator = sexualIndicators.any { lowerPrompt.contains(it) }

        if (hasMinorIndicator && hasSexualIndicator) {
            return false
        }

        // Block extreme violence descriptions
        val violenceIndicators = setOf("kill", "murder", "torture", "rape", "assault")
        val hasViolenceIndicator = violenceIndicators.any { lowerPrompt.contains(it) }
        if (hasViolenceIndicator) {
            return false
        }

        return true
    }

    fun sanitizePrompt(prompt: String): String {
        var sanitized = prompt

        for (keyword in blockedKeywords) {
            sanitized = sanitized.replace(keyword, "***", ignoreCase = true)
        }

        return sanitized
    }

    fun getViolationReason(prompt: String): String? {
        val lowerPrompt = prompt.lowercase()

        for (keyword in blockedKeywords) {
            if (lowerPrompt.contains(keyword)) {
                return "Content contains inappropriate language: '$keyword'"
            }
        }

        val hasMinorIndicator = minorIndicators.any { lowerPrompt.contains(it) }
        val hasSexualIndicator = sexualIndicators.any { lowerPrompt.contains(it) }

        if (hasMinorIndicator && hasSexualIndicator) {
            return "Sexual content involving minors is not allowed"
        }

        val violenceIndicators = setOf("kill", "murder", "torture", "rape", "assault")
        val hasViolenceIndicator = violenceIndicators.any { lowerPrompt.contains(it) }
        if (hasViolenceIndicator) {
            return "Violent or graphic content is not permitted"
        }

        return null
    }
}
