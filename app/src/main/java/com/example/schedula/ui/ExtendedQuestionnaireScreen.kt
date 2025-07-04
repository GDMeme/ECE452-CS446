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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val uniYears = listOf("Freshman", "Sophomore", "Junior", "Senior")
    var uniYear by remember { mutableIntStateOf(1) }

    var socializeIndex by remember { mutableIntStateOf(1) }
    var dietIndex by remember { mutableIntStateOf(0) }
    var stressIndex by remember { mutableIntStateOf(1) }

    val socializeOpts = listOf("Rarely", "Sometimes", "Frequently")
    val stressOpts = listOf("Not at all", "A little", "Very")


//    val scroll = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(
            rememberScrollState()
        ),
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
            "Questionnaire - LifeStyle (cont.)",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Text("Tell us a bit more so you can help us understand your lifestyle and habits.", fontSize = 16.sp)
        Spacer(Modifier.height(24.dp))

//        Question("1. How long do you study each day?", TextFieldValue = studyHours, onValueChange = { studyHours = it })
//        Question("2. How often do you socialize with friends?", options = listOf("Rarely", "Sometimes", "Frequently"), selected = socializeIndex, onSelect = { socializeIndex = it })
//        Question("3. What is your main hobby?", TextFieldValue = hobby, onValueChange = { hobby = it })
//        Question("4. How many steps do you walk daily?", TextFieldValue = steps, onValueChange = { steps = it })
//        Question("5. How many glasses of water do you drink per day?", TextFieldValue = water, onValueChange = { water = it })
//        Question("7. How stressed do you feel daily?", options = listOf("Not at all", "A little", "Very"), selected = stressIndex, onSelect = { stressIndex = it })
//        Spacer(Modifier.height(12.dp))
//        Text("8. What year are you in?")
//        DropdownMenuBox(selected = year, options = years, onSelect = { year = it })
//        Spacer(Modifier.height(24.dp))


        QuestionBlock(1, "How many hours do you study each day?") {
            OutlinedTextField(studyHours, { studyHours = it }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        QuestionBlock(2, "How often do you socialize with friends?") {
            socializeOpts.forEachIndexed { i, txt ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = socializeIndex == i, onClick = { socializeIndex = i })
                    Text(txt)
                }
            }
        }
        QuestionBlock(3, "Your main hobby?") {
            OutlinedTextField(hobby, { hobby = it }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        QuestionBlock(4, "How many steps do you walk daily?") {
            OutlinedTextField(steps, { steps = it }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        QuestionBlock(5, "How many glasses of water do you drink per day?") {
            OutlinedTextField(water, { water = it }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        QuestionBlock(6, "How stressed do you feel daily?") {
            stressOpts.forEachIndexed { i, txt ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = stressIndex == i, onClick = { stressIndex = i })
                    Text(txt)
                }
            }
        }
        QuestionBlock(
            number = 4,
            label = "What year of your studies are you in?",
            content = {
                uniYears.forEachIndexed{ index, text ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =  Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ){
                        RadioButton(
                            selected = uniYear == index,
                            onClick = { uniYear = index }
                        )
                        Spacer(Modifier.width(7.dp))
                        Text(text)
                    }
                }
            }
        )

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

data class ExtendedLifestyleAnswers(
    val studyHours: String,
    val socializeFreq: String,
    val hobby: String,
    val steps: String,
    val water: String,
    val stress: String,
    val year: String
)

//@Composable
//fun Question(
//    number: Int,
//    label: String,
//    content: @Composable () -> Unit
//) {
//    Text(
//        "$number.  $label",
//        style = MaterialTheme.typography.bodyLarge,
//        modifier = Modifier.padding(vertical = 8.dp)
//    )
//    content()
//    Spacer(Modifier.height(20.dp))
//}
