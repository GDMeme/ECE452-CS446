package com.example.schedula.ui

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import com.example.schedula.ui.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import com.example.schedula.data.Timer
import com.example.schedula.data.TimerViewModel

@Composable
fun TimerScreen(navController: NavController, timerViewModel: TimerViewModel) {

    val backgroundColor = Color(0xFFF0E7F4)
    val accentPurple = Color(0xFFE6DEF6)
    val borderPurple = Color(0xFF9C89B8)
    val textColor = Color(0xFF3B3B3B)

    var selectedMode by remember { mutableStateOf("Pomodoro") }
    var isRunning by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(25 * 60) }
    var taskInput by remember { mutableStateOf(TextFieldValue("")) }
    val taskList = remember { mutableStateListOf<String>() }

    val context = LocalContext.current

    val timers = timerViewModel.timers.observeAsState()
    val pomodoroTimers = timerViewModel.pomodoroTimers.observeAsState()
    val breakTimers = timerViewModel.breakTimers.observeAsState()

    var currentTimer: Timer

    if (timers.value.isNullOrEmpty()) {
        currentTimer = Timer(
            id = 0,
            isRunning = isRunning,
            startTime = 0,
            timeRemaining = timeLeft,
            timerType = selectedMode
        )
        timerViewModel.addTimer(currentTimer)
    }
    else {
        currentTimer = timers.value!![0]
        selectedMode = currentTimer.timerType
        isRunning = currentTimer.isRunning
        timeLeft = currentTimer.timeRemaining
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    fun updateUserXP(xpDelta: Int, onComplete: ((Boolean) -> Unit)? = null) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        if (currentUser != null) {
            val userRef = db.collection("users").document(currentUser.uid)

            userRef.update("userXP", FieldValue.increment(xpDelta.toLong()))
                .addOnSuccessListener {
                    Log.d("XP_UPDATE", "Incremented XP by $xpDelta for user ${currentUser.uid}")
                    onComplete?.invoke(true)
                }
                .addOnFailureListener { e ->
                    Log.e("XP_UPDATE", "Failed to increment XP: ${e.localizedMessage}")
                    onComplete?.invoke(false)
                }
        } else {
            Log.e("XP_UPDATE", "No authenticated user.")
            onComplete?.invoke(false)
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(currentScreen = "timer", navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(24.dp)
                .padding(padding)
        ) {
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
                                if (selectedMode == "Pomodoro") {
                                    if (!pomodoroTimers.value.isNullOrEmpty()) {
                                        currentTimer = pomodoroTimers.value!![0]
                                    }
                                    else timerViewModel.addTimerGivenType("Pomodoro")
                                    }

                                if (selectedMode == "Break") {
                                    if (!breakTimers.value.isNullOrEmpty()) {
                                        currentTimer = breakTimers.value!![0]
                                    }
                                    else timerViewModel.addTimerGivenType("Break")
                                }
                                // isRunning = false
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    String.format("%02d", minutes),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(":", fontSize = 64.sp, fontWeight = FontWeight.Bold, color = textColor)
                Text(
                    String.format("%02d", seconds),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = {
                    // Condition to check if XP should be awarded
                    val shouldAwardXp =
                        !isRunning && timeLeft == 25 * 60 && selectedMode == "Pomodoro"

                    // Toggle the running state
                    isRunning = !isRunning

                    // Award XP only if the condition was met
                    if (shouldAwardXp) {
                        updateUserXP(10) { success ->
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Gained 10 XP for starting a new Pomodoro!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(context, "Failed to update XP", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                    if (isRunning) timerViewModel.startTimer(currentTimer)
                    else timerViewModel.pauseTimer(currentTimer)
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentPurple),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(if (isRunning) "Pause" else "Start", color = textColor)
            }

            Spacer(modifier = Modifier.width(36.dp))

            Button(
                onClick = {
                    timerViewModel.resetTimer(currentTimer)
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentPurple),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Reset", color = textColor)
            }
        }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Tasks", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
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
                            Text(task, fontSize = 16.sp, modifier = Modifier.weight(1f), color = textColor)
                            IconButton(onClick = { taskList.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete task", tint = borderPurple)
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
        }
    }
}