package com.example.schedula.ui

import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomRoutineScreen(navController: NavController, onNext: () -> Unit = {}) {
    val routines = remember { List(4) { mutableStateOf("") } }
    val selected = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        navController.navigate("hobbiesQuestionnaire") {
                            popUpTo("customRoutineQuestionnaire") { inclusive = true }
                        }
                    }
            )
            Text(
                text = "Schedula",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        Text(
            "Specify Custom Routines",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Text("Is there anything else you would like to include in your schedule?", fontSize = 16.sp)
        Spacer(Modifier.height(12.dp))

        routines.forEach { routine ->
            OutlinedTextField(
                value = routine.value,
                onValueChange = { routine.value = it },
                placeholder = { Text("Enter your custom routine here") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.width(20.dp))
        }

        Spacer(Modifier.width(20.dp))

        Button(
            onClick = {
                //                onNext(
                //                    lifeStyleQuestionnaireAnswers(
                //                        bedTime,
                //                        wakeTime,
                //                        exerciseChoices[exerciseNum]
                //                    )
                //                )
                navController.navigate("scheduleUpload")
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape((16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD7D9F7))

        ) {
            Text("Next", fontSize = 18.sp)
        }
    }

}
