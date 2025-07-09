package com.example.schedula.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

data class HomeEvent(
    val title: String,
    val startTime: String,
    val endTime: String,
    val date: String
)

@Composable
fun HomeScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF0E7F4)
    val purple = Color(0xFF9C89B8)
    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val eventList = remember {
        mutableStateListOf(
            HomeEvent("ECE 452 @ MC 2017", "13:00", "14:20", "2025-07-09"),
            HomeEvent("MSE 452 @ CPH 3681", "11:30", "12:50", "2025-07-09"),
            HomeEvent("🏃 Exercise", "15:00", "16:00", "2025-07-09"),
            HomeEvent("💡 Study Session", "17:00", "18:00", "2025-07-09"),
            HomeEvent("🍽️ Dinner", "18:00", "19:00", "2025-07-09"),
            HomeEvent("💡 Study Session", "20:00", "21:00", "2025-07-09")
        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notification",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray
                )
                Text(
                    text = "Good Morning,\nAlex!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Today's Schedule",
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(eventList.size) { index ->
                    val event = eventList[index]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
}
