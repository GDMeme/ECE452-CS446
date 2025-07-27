package com.example.schedula.ui

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
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

    val hobbiesSelected: MutableMap<String, Boolean> = mutableMapOf()
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

    fun updateExtendedLifestyleData(
        study: String,
        socialize: String,
        hob: String,
        stepsWalked: String,
        waterIntake: String,
        stress: String,
        year: String
    ) {
        studyHours = study
        socializeFrequency = socialize
        hobby = hob
        steps = stepsWalked
        water = waterIntake
        stressLevel = stress
        universityYear = year
    }

    fun updateHobbiesSelection(selected: Map<String, Boolean>) {
        hobbiesSelected.clear()
        hobbiesSelected.putAll(selected)
    }

    // Public wrapper to save all
    suspend fun saveToDataStore(dataStoreManager: DataStoreManager) {
        saveLifestyle(dataStoreManager)
        saveExtendedLifestyle(dataStoreManager)
        saveCustomRoutines(dataStoreManager)
        saveEvents(dataStoreManager)
    }

    // Public wrapper to load all
    suspend fun loadFromDataStore(dataStoreManager: DataStoreManager) {
        loadLifestyle(dataStoreManager)
        loadExtendedLifestyle(dataStoreManager)
        loadCustomRoutines(dataStoreManager)
        loadEvents(dataStoreManager)
    }

    // Modular private saves
    private suspend fun saveLifestyle(dataStoreManager: DataStoreManager) {
        dataStoreManager.saveLifestyleData(
            bed = bedTime,
            wake = wakeTime,
            exercise = exerciseFrequency
        )
    }

    private suspend fun saveExtendedLifestyle(dataStoreManager: DataStoreManager) {
        dataStoreManager.saveExtendedLifestyleData(
            study = studyHours,
            socialize = socializeFrequency,
            hob = hobby,
            stepsWalked = steps,
            waterIntake = water,
            stress = stressLevel,
            year = universityYear
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

    private suspend fun loadExtendedLifestyle(dataStoreManager: DataStoreManager) {
        val extended = dataStoreManager.extendedLifestyleDataFlow.firstOrNull()
        extended?.let {
            updateExtendedLifestyleData(
                study = it.studyHours,
                socialize = it.socializeFreq,
                hob = it.hobby,
                stepsWalked = it.steps,
                waterIntake = it.water,
                stress = it.stressLevel,
                year = it.universityYear
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
}
