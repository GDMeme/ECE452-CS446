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


@Composable
fun LifestyleQuestionnaireScreen( navController: NavController,
    onBack: () -> Unit = {},
    onNext: (lifeStyleQuestionnaireAnswers) -> Unit = {}
) {
    var bedTime by remember { mutableStateOf(("11:30 PM")) }
    var wakeTime by remember { mutableStateOf(("7:30 AM")) }
    var exerciseNum by remember { mutableIntStateOf(1)}

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
                        navController.navigate("login") {
                            popUpTo("lifestyleQuestionnaire") { inclusive = true }
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

        Text(
            "Let's get started by answering a few questions about your schedule and routines.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        QuestionBlock(
            number = 1,
            label = "When do you usually go to bed?",
            content = {
                OutlinedTextField(value = bedTime,
                    onValueChange =  {bedTime = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth())
            }
        )

       QuestionBlock(
           number = 2,
           label = "What time do you wake up?",
           content = {
               OutlinedTextField(
                   value = wakeTime,
                   onValueChange = { wakeTime = it },
                   singleLine = true,
                   modifier = Modifier.fillMaxWidth()
               )
           }
       )

        val exerciseChoices = listOf(
            "Not at all",
            "2-3 times", //options here depend on whether we are doing monthly or weekly views
            "3+ times"
        )
        QuestionBlock(
            number = 3,
            label = "How often do you exercise in a week",
            content = {
                exerciseChoices.forEachIndexed{ index, text ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =  Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ){
                        RadioButton(
                            selected = exerciseNum == index,
                            onClick = { exerciseNum = index }
                        )
                        Spacer(Modifier.width(7.dp))
                        Text(text)
                    }
                }
            }
        )

        Button(
            onClick = {
                onNext(
                    lifeStyleQuestionnaireAnswers(
                        bedTime,
                        wakeTime,
                        exerciseChoices[exerciseNum]
                    )
                )
                navController.navigate("extendedQuestionnaire")
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape((16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD7D9F7))

        ){
            Text("Next", fontSize = 18.sp)
        }

    }


}


@Composable
fun QuestionBlock(
    number: Int,
    label: String,
    content: @Composable () -> Unit
) {
    Text(
        "$number.  $label",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    content()
    Spacer(Modifier.height(20.dp))
}

data class lifeStyleQuestionnaireAnswers(
    val bedTime: String,
    val wakeTime: String,
    val exerciseNum: String
)