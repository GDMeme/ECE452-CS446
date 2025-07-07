package com.example.schedula.ui

data class ScheduleEntry(
    val courseCode: String,
    val startTime: String,
    val endTime: String,
    val day: String,
    val location: String
)

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
    val scheduleData: MutableList<ScheduleEntry> = mutableListOf()

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
}

// Example of adding dummy data:
// QuestionnaireDataStore.scheduleData.add(
//     ScheduleEntry("MATH101", "08:30", "09:50", "Monday", "Room 101")
// )