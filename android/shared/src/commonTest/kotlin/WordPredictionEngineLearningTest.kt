package com.vatoo.erick.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WordPredictionEngineLearningTest {

    @Test
    fun exportedLearnedWordsRoundTripIntoNewEngine() {
        val engine = WordPredictionEngine.createWithDefaultDictionary()
        engine.addUserWord("erick", count = 3)

        val serializedProfile = engine.exportLearnedProfile()
        val restoredEngine = WordPredictionEngine.createWithDefaultDictionary()
        restoredEngine.importLearnedProfile(serializedProfile)

        val suggestions = restoredEngine.getSuggestions("eri", limit = 3)
        assertTrue(suggestions.contains("erick"))
    }

    @Test
    fun learnedFrequenciesPromoteFrequentlyUsedWords() {
        val engine = WordPredictionEngine()
        engine.insert("apple", frequency = 10)
        engine.insert("apex", frequency = 10)
        engine.learnWord("apex", count = 5, userAdded = true)

        val suggestions = engine.getSuggestions("ap", limit = 2)
        assertEquals("apex", suggestions.first())
    }

    @Test
    fun learnedBigramsImproveNextWordPredictions() {
        val engine = WordPredictionEngine.createWithDefaultDictionary()
        engine.learnBigram("hello", "erick", count = 3)

        val suggestions = engine.getNextWordSuggestions("hello", limit = 3)
        assertEquals("erick", suggestions.first())
    }
}