package com.example.schedula

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.*
import androidx.navigation.navArgument
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
    private var startDestination = "login" // default is login page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(applicationContext)

        lifecycleScope.launch {
            OnboardingDataClass.loadFromDataStore(dataStoreManager)

            val isUserSignedIn = Firebase.auth.currentUser != null
            val questionnaireCompleted = dataStoreManager.isQuestionnaireCompleted()

            startDestination = when {
                !isUserSignedIn -> "login"
                !questionnaireCompleted -> "lifestyleQuestionnaire" // first questionnaire screen
                else -> "home"
            }

            setContent {
                SchedulaTheme {
                    SchedulaApp(startDestination)
                }
            }
        }
    }
}

@Composable
fun SchedulaApp(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable(route = "lifestyleQuestionnaire?source={source}", arguments = listOf(navArgument("source") {
            defaultValue = "onboarding"
        })) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: "onboarding"
            LifestyleQuestionnaireScreen(navController, {}, {}, source)
        }
        composable("scheduleUpload") {
            ScheduleUploadScreen(navController)
        }
        composable("success") { SuccessScreen(navController) }
        composable("leaderboard") { LeaderboardScreen(navController) }
        composable("timer") { TimerScreen(navController) }
        composable("hobbiesQuestionnaire?source={source}", arguments = listOf(navArgument("source") {
            defaultValue = "onboarding"
        })) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: "onboarding"
            HobbySelectionScreen(navController, {}, source)
        }
        composable("customRoutineQuestionnaire?source={source}", arguments = listOf(navArgument("source") {
            defaultValue = "onboarding"
        })) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: "onboarding"
            CustomRoutineScreen(navController, {}, source)
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
