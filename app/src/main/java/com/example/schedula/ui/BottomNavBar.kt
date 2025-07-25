package com.example.schedula.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun BottomNavBar(currentScreen: String, navController: NavController) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = currentScreen == "home",
            onClick = {
                if (currentScreen != "home") {
                    navController.navigate("home") {
                        popUpTo(currentScreen) { inclusive = false }
                    }
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentScreen == "calendar",
            onClick = {
                if (currentScreen != "calendar") {
                    navController.navigate("calendar") {
                        popUpTo(currentScreen) { inclusive = false }
                    }
                }
            },
            icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Calendar") },
            label = { Text("Calendar") }
        )
        NavigationBarItem(
            selected = currentScreen == "timer",
            onClick = {
                if (currentScreen != "timer") {
                    navController.navigate("timer") {
                        popUpTo(currentScreen) { inclusive = false }
                    }
                }
            },
            icon = { Icon(Icons.Default.Timer, contentDescription = "Timer") },
            label = { Text("Timer") }
        )
        NavigationBarItem(
            selected = currentScreen == "profile",
            onClick = {
                if (currentScreen != "profile") {
                    navController.navigate("profile") {
                        popUpTo(currentScreen) { inclusive = false }
                    }
                }
            },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 10.sp) }
        )
    }
}
