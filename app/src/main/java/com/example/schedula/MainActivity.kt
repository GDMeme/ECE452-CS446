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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(applicationContext)

        // Load data into OnboardingDataClass
        lifecycleScope.launch {
            OnboardingDataClass.loadFromDataStore(dataStoreManager)
        }

        setContent {
            SchedulaTheme {
                SchedulaApp()
            }
        }
    }
}

@Composable
fun SchedulaApp() {
    val navController = rememberNavController()

    // ✅ Check login state
    val isUserSignedIn = Firebase.auth.currentUser != null
    val startDestination = if (isUserSignedIn) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("lifestyleQuestionnaire") {
            LifestyleQuestionnaireScreen(navController, {}, {})
        }
        composable("scheduleUpload") {
            ScheduleUploadScreen(navController)
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
            CalendarScreen(navController)
        }
        composable("questionsMenu") {
            QuestionnaireMenuScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}
