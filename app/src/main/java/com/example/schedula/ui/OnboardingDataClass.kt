package com.example.schedula.ui

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object OnboardingDataClass {
    var bedTime: String = ""
    var wakeTime: String = ""
    var exerciseFrequency: String = ""

    var studyHours: String = ""
    var socializeFrequency: String = ""
    var hobby: String = ""
    var steps: String = ""
    var water: String = ""
    var stressLevel: String = ""
    var universityYear: String = ""

    val hobbiesSelected: MutableList<String> = mutableListOf()
    val customRoutines: MutableList<String> = MutableList(4) { "" }
    val scheduleData = mutableStateListOf<Event>()

    val fixedEvents = mutableStateListOf<Event>()
    val flexibleEvents = mutableStateListOf<Event>()

    fun clearAll() {
        bedTime = ""
        wakeTime = ""
        exerciseFrequency = ""
        studyHours = ""
        socializeFrequency = ""
        hobby = ""
        steps = ""
        water = ""
        stressLevel = ""
        universityYear = ""
        hobbiesSelected.clear()
        repeat(4) { customRoutines[it] = "" }
        scheduleData.clear()
        fixedEvents.clear()
        flexibleEvents.clear()
    }

    fun updateLifestyleData(bed: String, wake: String, exercise: String) {
        bedTime = bed
        wakeTime = wake
        exerciseFrequency = exercise
    }

    fun updateHobbiesSelection(selected: List<String>) {
        hobbiesSelected.clear()
        hobbiesSelected.addAll(selected)
    }

    // Public wrapper to save all
    suspend fun saveToDataStore(dataStoreManager: DataStoreManager) {
        saveLifestyle(dataStoreManager)
        saveCustomRoutines(dataStoreManager)
        saveEvents(dataStoreManager)
        saveHobbiesSelection(dataStoreManager)
    }

    // Public wrapper to load all
    suspend fun loadFromDataStore(dataStoreManager: DataStoreManager) {
        loadLifestyle(dataStoreManager)
        loadCustomRoutines(dataStoreManager)
        loadEvents(dataStoreManager)
        loadHobbiesSelection(dataStoreManager)
    }

    // Modular private saves
    private suspend fun saveLifestyle(dataStoreManager: DataStoreManager) {
        dataStoreManager.saveLifestyleData(
            bed = bedTime,
            wake = wakeTime,
            exercise = exerciseFrequency
        )
    }

    private suspend fun saveCustomRoutines(dataStoreManager: DataStoreManager) {
        dataStoreManager.saveCustomRoutines(customRoutines)
    }

    private suspend fun saveEvents(dataStoreManager: DataStoreManager) {
        val fixedJson = Json.encodeToString(fixedEvents.toList())
        val flexibleJson = Json.encodeToString(flexibleEvents.toList())
        dataStoreManager.saveFixedEvents(fixedJson)
        dataStoreManager.saveFlexibleEvents(flexibleJson)
    }

    // Modular private loads
    private suspend fun loadLifestyle(dataStoreManager: DataStoreManager) {
        val lifestyle = dataStoreManager.lifestyleDataFlow.firstOrNull()
        lifestyle?.let {
            updateLifestyleData(
                bed = it.first,
                wake = it.second,
                exercise = it.third
            )
        }
    }

    private suspend fun loadCustomRoutines(dataStoreManager: DataStoreManager) {
        val routines = dataStoreManager.customRoutinesFlow.firstOrNull() ?: emptyList()
        customRoutines.clear()
        customRoutines.addAll(List(4) { routines.getOrNull(it) ?: "" })
    }

    private suspend fun loadEvents(dataStoreManager: DataStoreManager) {
        val fixedJson = dataStoreManager.getFixedEvents() ?: "[]"
        fixedEvents.clear()
        fixedEvents.addAll(Json.decodeFromString(fixedJson))

        val flexibleJson = dataStoreManager.getFlexibleEvents() ?: "[]"
        flexibleEvents.clear()
        flexibleEvents.addAll(Json.decodeFromString(flexibleJson))
    }

    private suspend fun saveHobbiesSelection(dataStoreManager: DataStoreManager) {
        val json = Json.encodeToString(hobbiesSelected)
        dataStoreManager.saveHobbiesSelection(json)
    }

    private suspend fun loadHobbiesSelection(dataStoreManager: DataStoreManager) {
        val json = dataStoreManager.getHobbiesSelection() ?: "[]"
        val loaded = Json.decodeFromString<List<String>>(json)
        updateHobbiesSelection(loaded)
    }
}
