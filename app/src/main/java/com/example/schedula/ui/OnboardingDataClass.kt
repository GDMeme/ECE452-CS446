package com.example.schedula.ui

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.set

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

    fun updateCustomRoutines(routines: List<String>) {
        for (i in routines.indices) {
            if (i < customRoutines.size) {
                customRoutines[i] = routines[i]
            }
        }
    }

    fun loadFromDataStore(dataStoreManager: DataStoreManager) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreManager.lifestyleDataFlow.collectLatest { (bed, wake, exercise) ->
                bedTime = bed
                wakeTime = wake
                exerciseFrequency = exercise
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            dataStoreManager.extendedLifestyleDataFlow.collectLatest { ext ->
                studyHours = ext.studyHours
                socializeFrequency = ext.socializeFreq
                hobby = ext.hobby
                steps = ext.steps
                water = ext.water
                stressLevel = ext.stressLevel
                universityYear = ext.universityYear
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            dataStoreManager.customRoutinesFlow.collectLatest { routines ->
                for (i in routines.indices) {
                    if (i < customRoutines.size) {
                        customRoutines[i] = routines[i]
                    }
                }
            }
        }
    }
}

// Example of adding dummy data:
// QuestionnaireDataStore.scheduleData.add(
//     ScheduleEntry("MATH101", "08:30", "09:50", "Monday", "Room 101")
// )