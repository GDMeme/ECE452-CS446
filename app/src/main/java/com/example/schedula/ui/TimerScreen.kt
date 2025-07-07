package com.example.schedula.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun TimerScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF0E7F4)
    val accentPurple = Color(0xFFE6DEF6)
    val borderPurple = Color(0xFF9C89B8)
    val textColor = Color(0xFF3B3B3B)

    var selectedMode by remember { mutableStateOf("Pomodoro") }
    var isRunning by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(25 * 60) }
    var taskInput by remember { mutableStateOf(TextFieldValue("")) }
    val taskList = remember { mutableStateListOf<String>() }

    LaunchedEffect(isRunning, selectedMode) {
        while (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    fun resetTimer() {
        timeLeft = if (selectedMode == "Pomodoro") 25 * 60 else 5 * 60
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                NavigationBar(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate("questionnaire") {
                                popUpTo("timer") { inclusive = false }
                            }
                        },
                        label = { Text("Questionnaire") },
                        icon = {}
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate("calendar") {
                                popUpTo("timer") { inclusive = false }
                            }
                        },
                        label = { Text("Calendar") },
                        icon = {}
                    )
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        label = { Text("Timer") },
                        icon = {}
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(24.dp)
                .padding(padding)
        ) {
            // Mode selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, borderPurple, RoundedCornerShape(50))
            ) {
                listOf("Pomodoro", "Break").forEachIndexed { index, mode ->
                    val isSelected = selectedMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(
                                when (index) {
                                    0 -> RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp)
                                    1 -> RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp)
                                    else -> RoundedCornerShape(0.dp)
                                }
                            )
                            .background(if (isSelected) accentPurple else backgroundColor)
                            .clickable {
                                selectedMode = mode
                                resetTimer()
                                isRunning = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = textColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Timer display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = String.format("%02d", minutes),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = ":",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = String.format("%02d", seconds),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start/Pause button
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(containerColor = accentPurple),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = if (isRunning) "Pause" else "Start", color = textColor)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Task section
            Text(
                text = "Tasks",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                itemsIndexed(taskList) { index, task ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = task,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f),
                                color = textColor
                            )
                            IconButton(onClick = { taskList.removeAt(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete task",
                                    tint = borderPurple
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = taskInput,
                onValueChange = { taskInput = it },
                placeholder = { Text("New Task") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (taskInput.text.isNotBlank()) {
                        taskList.add(taskInput.text.trim())
                        taskInput = TextFieldValue("")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentPurple),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+  Add Task", color = textColor)
            }

            Spacer(modifier = Modifier.height(16.dp))


            // Bottom navigation bar
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    label = { Text("Home") },
                    icon = {}
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    label = { Text("Questions") },
                    icon = {},
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("calendar") {
                            popUpTo("questionnaire") { inclusive = false }
                        }
                    },
                    label = { Text("Calendar") },
                    icon = {}
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("timer") {
                            popUpTo("questionnaire") { inclusive = false }
                        }
                    },
                    label = { Text("Timer") },
                    icon = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerScreen() {
    TimerScreen(navController = rememberNavController())
}