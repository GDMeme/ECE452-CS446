package com.example.schedula

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.*
import com.example.schedula.ui.LifestyleQuestionnaireScreen
import com.example.schedula.ui.LoginScreen
import com.example.schedula.ui.ScheduleUploadScreen
import com.example.schedula.ui.theme.SchedulaTheme
import com.example.schedula.ui.SignUpScreen
import com.example.schedula.ui.TimerScreen
import com.example.schedula.ui.LeaderboardScreen

import com.example.schedula.ui.ExtendedLifestyleScreen
import com.example.schedula.ui.HobbySelectionScreen
import com.example.schedula.ui.CustomRoutineScreen
import com.example.schedula.ui.HomeScreen
import com.example.schedula.ui.CalendarScreen
import com.example.schedula.ui.Event
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import com.example.schedula.ui.SuccessScreen

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
    val eventList = remember { mutableStateListOf<Event>() }
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination =  "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }

        composable("signup") {
            SignUpScreen(navController)
        }
        //make all the pages have a purple theme
        composable("lifestyleQuestionnaire") {
            LifestyleQuestionnaireScreen(navController, {}, {}) //fix callback function
        }
        composable("scheduleUpload") {
            ScheduleUploadScreen(navController, eventList)
        }
        composable("success") {
            SuccessScreen(navController)
        }
        composable("leaderboard") {
            LeaderboardScreen(navController)
        }
        composable("timer") {
            TimerScreen(navController = navController)
        }
        composable("extendedQuestionnaire") {
            ExtendedLifestyleScreen(navController, {}) //TODO fix callback function for all that have empty brackets
        }
        composable("hobbiesQuestionnaire") {
            HobbySelectionScreen(navController, {})
        }
        composable("customRoutineQuestionnaire") {
            CustomRoutineScreen(navController, {})
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("calendar") {
            CalendarScreen(navController, eventList)
        }

    }
}