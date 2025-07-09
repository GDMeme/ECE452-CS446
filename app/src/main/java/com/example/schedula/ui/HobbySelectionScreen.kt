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
import androidx.navigation.compose.rememberNavController

@Composable
fun HobbySelectionScreen(navController: NavController, onNext: () -> Unit = {}) {
    val hobbies = listOf(
        "Reading", "Playing Sports", "Cooking", "Traveling",
        "Gardening", "Watching Movies", "Drawing or Painting",
        "Playing a Musical Instrument", "Photography", "Writing", "Yoga"
    )
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
                        navController.navigate("extendedQuestionnaire") {
                            popUpTo("hobbiesQuestionnaire") { inclusive = true }
                        }
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

        Spacer(Modifier.width(8.dp))

        Button(
            onClick = {
//                onNext(
                        OnboardingDataClass.updateHobbiesSelection(selected)
//                    )
//                )
                navController.navigate("customRoutineQuestionnaire")
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape((16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD7D9F7))

        ){
            Text("Next", fontSize = 18.sp)
        }

        Spacer(Modifier.height(24.dp))

    }

}
@Preview(showBackground = true)
@Composable
fun HobbySelectionScreen() {
    HobbySelectionScreen(navController = rememberNavController())
}