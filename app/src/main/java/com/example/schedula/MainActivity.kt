package com.example.schedula

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.schedula.ui.LoginScreen
import com.example.schedula.ui.QuestionnaireScreen
import com.example.schedula.ui.ScheduleUploadScreen
import com.example.schedula.ui.theme.SchedulaTheme
import com.example.schedula.ui.SignUpScreen
import com.example.schedula.ui.TimerScreen

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
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("questionnaire") {
            QuestionnaireScreen(navController)
        }
        composable("signup") {
            SignUpScreen(navController)
        }
        composable("scheduleUpload") {
            ScheduleUploadScreen(navController, {}) //make callback function empty for now

        }
        composable("timer") {
            TimerScreen(navController = navController)
        }

    }
}
