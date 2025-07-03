package com.example.schedula.ui

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
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExtendedLifestyleScreen(navController: NavController, onNext: () -> Unit = {}) {
    var studyHours by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }
    var hobby by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("Junior") }
    val years = listOf("Freshman", "Sophomore", "Junior", "Senior")

    var socializeIndex by remember { mutableIntStateOf(1) }
    var dietIndex by remember { mutableIntStateOf(0) }
    var stressIndex by remember { mutableIntStateOf(1) }

//    val scroll = rememberScrollState()

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
                        navController.navigate("lifestyleQuestionnaire") {
                            popUpTo("extendedQuestionnaire") { inclusive = true }
                        }
                    }
            )
            Text(
                text = "Schedula",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        Text(
            "Questionnaire - LifeStyle",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Text("Please answer a few questions to help us understand your lifestyle and habits.", fontSize = 16.sp)
        Spacer(Modifier.height(24.dp))

//        Question("1. How long do you study each day?", TextFieldValue = studyHours, onValueChange = { studyHours = it })
//        Question("2. How often do you socialize with friends?", options = listOf("Rarely", "Sometimes", "Frequently"), selected = socializeIndex, onSelect = { socializeIndex = it })
//        Question("3. What is your main hobby?", TextFieldValue = hobby, onValueChange = { hobby = it })
//        Question("4. How many steps do you walk daily?", TextFieldValue = steps, onValueChange = { steps = it })
//        Question("5. How many glasses of water do you drink per day?", TextFieldValue = water, onValueChange = { water = it })
//        Question("6. Do you have any dietary restrictions?", options = listOf("None", "Vegetarian", "Other"), selected = dietIndex, onSelect = { dietIndex = it })
//        Question("7. How stressed do you feel daily?", options = listOf("Not at all", "A little", "Very"), selected = stressIndex, onSelect = { stressIndex = it })
//        Spacer(Modifier.height(12.dp))
//        Text("8. What year are you in?")
//        DropdownMenuBox(selected = year, options = years, onSelect = { year = it })
//        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
//                onNext(
//                    lifeStyleQuestionnaireAnswers(
//                        bedTime,
//                        wakeTime,
//                        exerciseChoices[exerciseNum]
//                    )
//                )
                navController.navigate("hobbiesQuestionnaire")
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape((16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD7D9F7))

        ){
            Text("Next", fontSize = 18.sp)
        }



    }

}
