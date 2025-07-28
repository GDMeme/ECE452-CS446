package com.example.schedula.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.schedula.ui.components.BottomNavBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireMenuScreen(navController: NavController) {
    val cardColor = Color(0xFFF3EBFF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Questionnaire") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(currentScreen = "questionsMenu", navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Choose a Questionnaire section to edit",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            QuestionnaireOptionCard(
                title = "Lifestyle",
                icon = Icons.Default.AccessibilityNew, // Replace with a better matching icon if needed
                onClick = { navController.navigate("lifestyleQuestionnaire?source=profile") },
                color = cardColor
            )

            QuestionnaireOptionCard(
                title = "Hobbies",
                icon = Icons.Default.Star, // Replace icon if needed
                onClick = { navController.navigate("hobbiesQuestionnaire?source=profile") },
                color = cardColor
            )

            QuestionnaireOptionCard(
                title = "Custom Routines",
                icon = Icons.Default.Schedule,
                onClick = { navController.navigate("customRoutineQuestionnaire?source=profile") },
                color = cardColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Keep your routines fresh and up-to-date ✨",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

    }
}

@Composable
fun QuestionnaireOptionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF5B5F9D), // match your theme
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
            )
        }
    }
}
