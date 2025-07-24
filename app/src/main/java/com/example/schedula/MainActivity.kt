package com.example.schedula

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.*
import com.example.schedula.ui.*
import com.example.schedula.ui.theme.SchedulaTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchedulaTheme {
                SchedulaApp()
            }
        }
    }
}

@Composable
fun SchedulaApp() {
    val eventList = remember {
        mutableStateListOf<Event>().apply {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val calendar = Calendar.getInstance().apply { set(2025, Calendar.JULY, 1) }

            while (calendar.get(Calendar.MONTH) == Calendar.JULY) {
                val dateStr = dateFormat.format(calendar.time)
                val dayName = dayFormat.format(calendar.time).uppercase()

                // Static recurring events
                when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> {
                        add(Event("🍽️ Breakfast", "08:00", "08:30", dateStr))
                        add(Event("💡 Study Session 1", "09:00", "10:00", dateStr))
                        add(Event("🍱 Lunch", "12:00", "13:00", dateStr))
                        add(Event("🍽️ Dinner", "18:00", "19:00", dateStr))
                        add(Event("💡 Study Session 2", "20:00", "21:00", dateStr))
                    }
                    Calendar.TUESDAY -> {
                        add(Event("🍽️ Breakfast", "08:00", "08:30", dateStr))
                        add(Event("💡 Study Session 1", "09:00", "10:00", dateStr))
                        add(Event("🍱 Lunch", "12:00", "13:00", dateStr))
                        add(Event("🏋️ Exercise", "18:00", "19:00", dateStr))
                        add(Event("🍽️ Dinner", "18:00", "19:00", dateStr))
                        add(Event("🎬 Movie Night", "21:00", "22:00", dateStr))
                        add(Event("💡 Study Session 2", "20:00", "21:00", dateStr))
                    }
                    Calendar.WEDNESDAY -> {
                        add(Event("🍽️ Breakfast", "08:00", "08:30", dateStr))
                        add(Event("💡 Study Session 1", "09:00", "10:00", dateStr))
                        add(Event("🍱 Lunch", "12:00", "13:00", dateStr))
                        add(Event("🏃 Exercise", "15:00", "16:00", dateStr))
                        add(Event("💡 Study Session", "17:00", "18:00", dateStr))
                        add(Event("🍽️ Dinner", "18:00", "19:00", dateStr))
                        add(Event("💡 Study Session 2", "20:00", "21:00", dateStr))
                    }
                    Calendar.THURSDAY -> {
                        add(Event("🍽️ Breakfast", "08:00", "08:30", dateStr))
                        add(Event("💡 Study Session 1", "09:00", "10:00", dateStr))
                        add(Event("🧘 Yoga", "10:00", "11:00", dateStr))
                        add(Event("🍱 Lunch", "12:00", "13:00", dateStr))
                        add(Event("🍽️ Dinner", "18:00", "19:00", dateStr))
                        add(Event("💡 Study Session 2", "20:00", "21:00", dateStr))
                    }
                    Calendar.FRIDAY -> {
                        add(Event("🍽️ Breakfast", "08:00", "08:30", dateStr))
                        add(Event("💡 Study Session 1", "09:00", "10:00", dateStr))
                        add(Event("🍱 Lunch", "12:00", "13:00", dateStr))
                        add(Event("🎸 Guitar Practice", "14:00", "15:00", dateStr))
                        add(Event("🍽️ Dinner", "18:00", "19:00", dateStr))
                        add(Event("💡 Study Session 2", "20:00", "21:00", dateStr))
                    }
                    Calendar.SATURDAY, Calendar.SUNDAY -> {
                        add(Event("🍽️ Breakfast", "08:00", "08:30", dateStr))
                        add(Event("💡 Study Session 1", "09:00", "10:00", dateStr))
                        add(Event("🍱 Lunch", "12:00", "13:00", dateStr))
                        add(Event("🍽️ Dinner", "18:00", "19:00", dateStr))
                        add(Event("💡 Study Session 2", "20:00", "21:00", dateStr))
                    }
                }

                // Add course schedule from onboarding
                OnboardingDataClass.scheduleData.forEach { item ->
                    if (item.day.uppercase() == dayName) {
                        add(
                            Event(
                                title = "${item.courseCode} @ ${item.location}",
                                startTime = item.startTime,
                                endTime = item.endTime,
                                date = dateStr
                            )
                        )
                    }
                }

                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("lifestyleQuestionnaire") {
            LifestyleQuestionnaireScreen(navController, {}, {})
        }
        composable("scheduleUpload") {
            ScheduleUploadScreen(navController, eventList)
        }
        composable("success") { SuccessScreen(navController) }
        composable("leaderboard") { LeaderboardScreen(navController) }
        composable("timer") { TimerScreen(navController) }
        composable("extendedQuestionnaire") {
            ExtendedLifestyleScreen(navController, {})
        }
        composable("hobbiesQuestionnaire") {
            HobbySelectionScreen(navController, {})
        }
        composable("customRoutineQuestionnaire") {
            CustomRoutineScreen(navController, {})
        }
        composable("home") { HomeScreen(navController) }
        composable("calendar") {
            CalendarScreen(navController, eventList)
        }
        composable("questionsMenu") {
            QuestionnaireMenuScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }

    }
}
