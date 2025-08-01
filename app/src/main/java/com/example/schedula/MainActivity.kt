package com.example.schedula

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.schedula.ui.*
import com.example.schedula.ui.theme.SchedulaTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.schedula.data.AppDatabase
import com.example.schedula.data.TimerRepository
import com.example.schedula.data.TimerViewModel
import com.example.schedula.data.TimerViewModelFactory
import com.example.schedula.ui.ScheduleViewModel


class MainActivity : ComponentActivity() {
    private lateinit var dataStoreManager: DataStoreManager
    private var startDestination = "login" // default is login page

    // Create a single ViewModel instance for the activity
    private val scheduleViewModel: ScheduleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(applicationContext)

        val database = AppDatabase.getDatabase(context = applicationContext)
        val timerDao = database.timerDao()

        lifecycleScope.launch {
            OnboardingDataClass.loadFromDataStore(dataStoreManager)

            val isUserSignedIn = Firebase.auth.currentUser != null
//            val questionnaireCompleted = dataStoreManager.isQuestionnaireCompleted()

            startDestination = when {
                !isUserSignedIn -> "login"
                else -> "home"
            }

            setContent {
                SchedulaTheme {
                    val timerRepository = TimerRepository(timerDao)
                    val timerViewModel:TimerViewModel = viewModel(factory = TimerViewModelFactory(timerRepository))
                    SchedulaApp(startDestination, scheduleViewModel, timerViewModel)
                }
            }
        }
    }
}

@Composable
fun SchedulaApp(
    startDestination: String,
    scheduleViewModel: ScheduleViewModel,
    timerViewModel: TimerViewModel
) {
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
            ScheduleUploadScreen(navController, scheduleViewModel)
        }
        composable("success") { SuccessScreen(navController) }
        composable("leaderboard") { LeaderboardScreen(navController) }
        composable("timer") { TimerScreen(navController,timerViewModel) }
        composable(route = "hobbiesQuestionnaire?source={source}", arguments = listOf(navArgument("source") {
            defaultValue = "onboarding"
        })) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: "onboarding"
            HobbySelectionScreen(navController, {}, source)
        }
        composable(route = "customRoutineQuestionnaire?source={source}", arguments = listOf(navArgument("source") {
            defaultValue = "onboarding"
        })) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: "onboarding"
            CustomRoutineScreen(navController, {}, source)
        }
        composable("home") { HomeScreen(navController) }
        composable("calendar") {
            CalendarScreen(navController, scheduleViewModel)
        }
        composable("questionsMenu") {
            QuestionnaireMenuScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}