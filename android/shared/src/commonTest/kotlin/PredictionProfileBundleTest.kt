package com.vatoo.erick.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PredictionProfileBundleTest {

    @Test
    fun bundleRoundTripsMultipleLanguages() {
        val serialized = PredictionProfileBundle.serialize(
            mapOf(
                KeyboardLanguage.ENGLISH to "[meta]\nversion\t2\n",
                KeyboardLanguage.SPANISH to "[words]\nniño\t3\t1\n[bigrams]\nhola\tamigo\t2\n"
            )
        )

        val restored = PredictionProfileBundle.deserialize(serialized)

        assertEquals("[meta]\nversion\t2\n", restored[KeyboardLanguage.ENGLISH])
        assertEquals("[words]\nniño\t3\t1\n[bigrams]\nhola\tamigo\t2\n", restored[KeyboardLanguage.SPANISH])
    }

    @Test
    fun legacyProfilesStillImportAsEnglish() {
        val legacyProfile = "[words]\nerick\t2\t1\n[bigrams]\nhello\terick\t3\n"

        val restored = PredictionProfileBundle.deserialize(legacyProfile)

        assertEquals(legacyProfile, restored[KeyboardLanguage.ENGLISH])
        assertTrue(restored.keys.contains(KeyboardLanguage.ENGLISH))
    }
}