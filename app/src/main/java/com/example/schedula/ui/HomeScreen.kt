package com.example.schedula.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.schedula.ui.components.BottomNavBar
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.example.schedula.ui.DailyGoals
import kotlin.random.Random
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

data class HomeEvent(
    val title: String,
    val startTime: String,
    val endTime: String,
    val date: String
)

@Composable
fun HomeScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF0E7F4)
    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName ?: "User"

    val showNotifications = remember { mutableStateOf(false) }
    val notifications = remember {
        mutableStateListOf(
            "💡 Study Session at 5:00 PM",
            "🎯 2 out of 4 daily goals completed",
            "⏰ Timer running for 30 mins"
        )
    }

    val eventList = remember {
        mutableStateListOf(
            HomeEvent("ECE 452 @ MC 2017", "13:00", "14:20", todayDate),
            HomeEvent("MSE 452 @ CPH 3681", "11:30", "12:50", todayDate),
            HomeEvent("🏃 Exercise", "15:00", "16:00", todayDate),
            HomeEvent("💡 Study Session", "17:00", "18:00", todayDate),
            HomeEvent("🍽️ Dinner", "18:00", "19:00", todayDate),
            HomeEvent("💡 Study Session", "20:00", "21:00", todayDate)
        )
    }

    val allGoals = listOf(
        DailyGoals("1", "Complete all classes", 15),
        DailyGoals("2", "Do 30 mins of focused study", 10),
        DailyGoals("3", "Plan tomorrow’s schedule", 5),
        DailyGoals("4", "Drink 8 glasses of water", 5),
        DailyGoals("5", "Sleep before 11 PM", 20)
    )

    val seed = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val goalsToday = remember(seed) {
        mutableStateListOf<DailyGoals>().apply {
            addAll(allGoals.shuffled(Random(seed)).take(4))
        }
    }

    val now = Calendar.getInstance()
    val totalMinutes = 10 * 60  // From 09:00 to 19:00
    val minutesPassed = (now.get(Calendar.HOUR_OF_DAY) - 9) * 60 + now.get(Calendar.MINUTE)
    val percentOfDay = (minutesPassed.toFloat() / totalMinutes).coerceIn(0f, 1f)
    val percentageText = "${(percentOfDay * 100).toInt()}%"

    val message = when {
        percentOfDay >= 1f -> "Well done! You've completed today's plan 🎉"
        percentOfDay < 0.5f -> "Let’s get started! You’ve got this 💪"
        else -> "Excellent! Your today's plan is almost done 🥰"
    }

    Scaffold(
        bottomBar = { BottomNavBar(currentScreen = "home", navController = navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 50.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showNotifications.value = !showNotifications.value }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notification",
                            tint = Color.Gray
                        )
                    }

                    Text(
                        text = "Good Morning,\n$userName!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (showNotifications.value) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Notifications", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (notifications.isEmpty()) {
                                Text("No new notifications", color = Color.Gray, fontSize = 14.sp)
                            } else {
                                notifications.forEach { note ->
                                    Text("• $note", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(140.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1BCE3))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = percentOfDay,
                                modifier = Modifier.size(64.dp),
                                strokeWidth = 6.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = percentageText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            item {
                DailyGoalsSection(goals = goalsToday) { goal ->
                    goal.completed = !goal.completed
                    notifications.add("✅ Completed: ${goal.description}")
                }
            }

            item {
                val totalXP = goalsToday.filter { it.completed }.sumOf { it.xp }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("XP earned today: $totalXP", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Today's Schedule",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            items(eventList.size) { index ->
                val event = eventList[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (index % 4) {
                            0 -> Color(0xFFE6DEF6)
                            1 -> Color(0xFFFFE3D9)
                            2 -> Color(0xFFCCE5FF)
                            else -> Color(0xFFFFF6CC)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(event.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${event.startTime} - ${event.endTime}", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DailyGoalsSection(goals: List<DailyGoals>, onToggle: (DailyGoals) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Daily Goals", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        goals.forEach { goal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (goal.completed) Color(0xFFD1E7DD) else Color(0xFFF8D7DA)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(goal.description, fontWeight = FontWeight.SemiBold)
                        Text("XP: ${goal.xp}", fontSize = 12.sp, color = Color.Gray)
                    }
                    Checkbox(
                        checked = goal.completed,
                        onCheckedChange = {
                            onToggle(goal)
                        }
                    )
                }
            }
        }
    }
}
