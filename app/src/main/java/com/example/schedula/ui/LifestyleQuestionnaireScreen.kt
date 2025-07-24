package com.example.schedula.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifestyleQuestionnaireScreen(
    navController: NavController,
    onBack: () -> Unit = {},
    onNext: (LifestyleQuestionnaireAnswers) -> Unit = {}
) {
    var bedTime by remember { mutableStateOf("11:30 PM") }
    var wakeTime by remember { mutableStateOf("7:30 AM") }
    var exerciseNum by remember { mutableIntStateOf(1) }

    val exerciseChoices = listOf("Not at all", "2–3 times", "3+ times")

    var showBedPicker by remember { mutableStateOf(false) }
    var showWakePicker by remember { mutableStateOf(false) }
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun formatTime(hour: Int, minute: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return timeFormatter.format(cal.time)
    }

    var bedHour by remember { mutableIntStateOf(23) }
    var bedMinute by remember { mutableIntStateOf(30) }
    var wakeHour by remember { mutableIntStateOf(7) }
    var wakeMinute by remember { mutableIntStateOf(30) }

    if (showBedPicker) {
        TimePickerDialogComposable(
            title = "Select Bed Time",
            initialHour = bedHour,
            initialMinute = bedMinute,
            onDismiss = { showBedPicker = false },
            onConfirm = { h, m ->
                bedHour = h
                bedMinute = m
                bedTime = formatTime(h, m)
                showBedPicker = false
            }
        )
    }

    if (showWakePicker) {
        TimePickerDialogComposable(
            title = "Select Wake Time",
            initialHour = wakeHour,
            initialMinute = wakeMinute,
            onDismiss = { showWakePicker = false },
            onConfirm = { h, m ->
                wakeHour = h
                wakeMinute = m
                wakeTime = formatTime(h, m)
                showWakePicker = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
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
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            "Questionnaire - Lifestyle",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
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
                OutlinedTextField(
                    value = bedTime,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    trailingIcon = {
                        Icon(Icons.Filled.AccessTime, contentDescription = "Pick time")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showBedPicker = true }
                )
            }
        )

        QuestionBlock(
            number = 2,
            label = "What time do you wake up?",
            content = {
                OutlinedTextField(
                    value = wakeTime,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    trailingIcon = {
                        Icon(Icons.Filled.AccessTime, contentDescription = "Pick time")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showWakePicker = true }
                )
            }
        )

        QuestionBlock(
            number = 3,
            label = "How often do you exercise in a week?",
            content = {
                exerciseChoices.forEachIndexed { index, text ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
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
                OnboardingDataClass.updateLifestyleData(
                    bed = bedTime,
                    wake = wakeTime,
                    exercise = exerciseChoices[exerciseNum]
                )

                onNext(
                    LifestyleQuestionnaireAnswers(
                        bedTime = bedTime,
                        wakeTime = wakeTime,
                        exerciseNum = exerciseChoices[exerciseNum]
                    )
                )
                navController.navigate("hobbiesQuestionnaire")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD7D9F7)
            )
        ) {
            Text("Next", color = Color(0xFF5B5F9D))
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

data class LifestyleQuestionnaireAnswers(
    val bedTime: String,
    val wakeTime: String,
    val exerciseNum: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogComposable(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val pickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(pickerState.hour, pickerState.minute) }) {
                Text("OK", color = Color(0xFF5B5F9D))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF5B5F9D))
            }
        },
        title = {
            Text(title, fontWeight = FontWeight.Bold)
        },
        text = {
            TimePicker(state = pickerState)
        },
        containerColor = Color(0xFFE6DEF6),
        shape = RoundedCornerShape(20.dp)
    )
}