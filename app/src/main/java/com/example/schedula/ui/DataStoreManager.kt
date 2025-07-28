package com.example.schedula.ui

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.first

// Create DataStore property delegate
private val Context.dataStore by preferencesDataStore("user_preferences")

class DataStoreManager(private val context: Context) {

    companion object {
        // Lifestyle keys
        val BED_TIME = stringPreferencesKey("bed_time")
        val WAKE_TIME = stringPreferencesKey("wake_time")
        val EXERCISE_FREQ = stringPreferencesKey("exercise_frequency")

        // Custom Routine keys
        val CUSTOM_ROUTINES = stringPreferencesKey("custom_routines_json")

        // Events keys
        val FIXED_EVENTS_JSON = stringPreferencesKey("fixed_events_json")
        val FLEXIBLE_EVENTS_JSON = stringPreferencesKey("flexible_events_json")

        val QUESTIONNAIRE_COMPLETED = booleanPreferencesKey("questionnaire_completed")

        val HOBBIES_KEY = stringPreferencesKey("hobbies_selection_json")
    }

    // Save lifestyle data
    suspend fun saveLifestyleData(bed: String, wake: String, exercise: String) {
        context.dataStore.edit { prefs ->
            prefs[BED_TIME] = bed
            prefs[WAKE_TIME] = wake
            prefs[EXERCISE_FREQ] = exercise
        }
    }

    suspend fun saveCustomRoutines(routines: List<String>) {
        val jsonString = Json.encodeToString(routines)
        context.dataStore.edit { prefs ->
            prefs[CUSTOM_ROUTINES] = jsonString
        }
    }

    // Event saving
    suspend fun saveFixedEvents(json: String) {
        context.dataStore.edit { prefs ->
            prefs[FIXED_EVENTS_JSON] = json
        }
    }

    suspend fun saveFlexibleEvents(json: String) {
        context.dataStore.edit { prefs ->
            prefs[FLEXIBLE_EVENTS_JSON] = json
        }
    }

    suspend fun getFixedEvents(): String? {
        return context.dataStore.data.map { prefs ->
            prefs[FIXED_EVENTS_JSON]
        }.firstOrNull()
    }

    suspend fun getFlexibleEvents(): String? {
        return context.dataStore.data.map { prefs ->
            prefs[FLEXIBLE_EVENTS_JSON]
        }.firstOrNull()
    }

    // Observe data flows
    val lifestyleDataFlow: Flow<Triple<String, String, String>> =
        context.dataStore.data.map { prefs ->
            Triple(
                prefs[BED_TIME] ?: "",
                prefs[WAKE_TIME] ?: "",
                prefs[EXERCISE_FREQ] ?: ""
            )
        }

    val customRoutinesFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val jsonString = prefs[CUSTOM_ROUTINES] ?: "[]"
        try {
            Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun hasExistingSchedule(): Boolean {
        val dataStoreManager = DataStoreManager(context)
        val prefs = context.dataStore.data.first()
        val fixed = prefs[FIXED_EVENTS_JSON]?.isNotBlank() ?: false
        val flexible = prefs[FLEXIBLE_EVENTS_JSON]?.isNotBlank() ?: false
        val routines = prefs[CUSTOM_ROUTINES]?.isNotBlank() ?: false

        return fixed || flexible || routines
    }

    suspend fun setQuestionnaireCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[QUESTIONNAIRE_COMPLETED] = completed
        }
    }

    suspend fun isQuestionnaireCompleted(): Boolean {
        // Example: check if any key indicating completion exists or a boolean flag
        val prefs = context.dataStore.data.first()
        return prefs[QUESTIONNAIRE_COMPLETED] == true
    }

    suspend fun saveHobbiesSelection(json: String) {
        context.dataStore.edit { prefs ->
            prefs[HOBBIES_KEY] = json
        }
    }

    suspend fun getHobbiesSelection(): String? {
        return context.dataStore.data.firstOrNull()?.get(HOBBIES_KEY)
    }
}
