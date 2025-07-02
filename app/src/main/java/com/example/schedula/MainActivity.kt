package com.example.schedula

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.schedula.ui.LoginScreen
import com.example.schedula.ui.QuestionnaireScreen
import com.example.schedula.ui.theme.SchedulaTheme
import com.example.schedula.ui.SignUpScreen

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
    }
}
