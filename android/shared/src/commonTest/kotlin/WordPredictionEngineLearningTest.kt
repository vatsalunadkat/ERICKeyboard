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

    @Test
    fun predictionDomainBoostsDomainVocabulary() {
        val engine = WordPredictionEngine.createWithDefaultDictionary()

        engine.setPredictionDomain(PredictionDomain.GAMING)

        val suggestions = engine.getSuggestions("pa", limit = 3)
        assertTrue(suggestions.contains("party"))
    }

    @Test
    fun spanishDictionaryLoadsLocalizedSuggestions() {
        val engine = WordPredictionEngine.createWithDefaultDictionary(KeyboardLanguage.SPANISH)

        assertTrue(engine.getDefaultSuggestions(limit = 3).contains("Hola"))
        assertTrue(engine.getSuggestions("ni", limit = 5).contains("niño"))
    }

    @Test
    fun germanDictionaryKeepsEszettWordsAvailable() {
        val engine = WordPredictionEngine.createWithDefaultDictionary(KeyboardLanguage.GERMAN)

        assertTrue(engine.getSuggestions("str", limit = 5).contains("straße"))
    }

    @Test
    fun exportedProfilesIncludeVersionHeaderAndLegacyProfilesStillImport() {
        val engine = WordPredictionEngine.createWithDefaultDictionary()
        engine.addUserWord("erick", count = 2)

        val serializedProfile = engine.exportLearnedProfile()
        assertTrue(serializedProfile.contains("[meta]"))
        assertTrue(serializedProfile.contains("version\t2"))

        val legacyProfile = "[words]\nerick\t2\t1\n[bigrams]\nhello\terick\t3\n"
        val restoredEngine = WordPredictionEngine.createWithDefaultDictionary()
        restoredEngine.importLearnedProfile(legacyProfile)

        val suggestions = restoredEngine.getSuggestions("eri", limit = 3)
        assertTrue(suggestions.contains("erick"))
    }
}