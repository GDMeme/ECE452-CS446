package com.example.schedula.ui

import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun HobbySelectionScreen(navController: NavController, onNext: () -> Unit = {}, source: String) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    val backgroundColor = Color(0xFFFAF7FC)
    val hobbies = listOf(
        "Reading", "Playing Sports", "Cooking", "Traveling",
        "Gardening", "Watching Movies", "Drawing or Painting",
        "Playing a Musical Instrument", "Photography", "Writing", "Yoga"
    )
    val selected = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp),
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
                        when (source) {
                            "onboarding" -> navController.navigate("lifestyleQuestionnaire") {
                                popUpTo("hobbiesQuestionnaire") { inclusive = true }
                            }
                            "profile" -> navController.popBackStack()
                        }
//                        navController.navigate("lifestyleQuestionnaire") {
//                            popUpTo("hobbiesQuestionnaire") { inclusive = true }
//                        }
                    }
            )
            Text(
                text = "Schedula",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        Text(
            "Questionnaire - Hobbies",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Text("Select all hobbies you enjoy:", fontSize = 16.sp)
        Spacer(Modifier.height(12.dp))

        hobbies.forEach { hobby ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selected[hobby] == true,
                    onCheckedChange = { selected[hobby] = it }
                )
                Spacer(Modifier.width(8.dp))
                Text(hobby)
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val selectedHobbies = selected.filter { it.value }.keys.toList()
                OnboardingDataClass.updateHobbiesSelection(selectedHobbies)

                // Save to DataStore in coroutine
                CoroutineScope(Dispatchers.IO).launch {
                    val json = Json.encodeToString(selectedHobbies)
                    dataStoreManager.saveHobbiesSelection(json)
                }

                navController.navigate("customRoutineQuestionnaire")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C89B8))
        ) {
            Text("Next", fontSize = 18.sp)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun HobbySelectionScreenPreview(source: String) {
    HobbySelectionScreen(navController = rememberNavController(), {}, source)
}
