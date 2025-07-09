package com.example.schedula.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.schedula.ui.components.BottomNavBar
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.snapshots.SnapshotStateList

data class Event(
    val title: String,
    val startTime: String,
    val endTime: String,
    val date: String
)

@Composable
fun CalendarScreen(navController: NavController, eventList: SnapshotStateList<Event>) {
    val backgroundColor = Color(0xFFF0E7F4)
    val purple = Color(0xFF9C89B8)
    val lightPurple = Color(0xFFE6DEF6)
    val red = Color(0xFFDC143C)

    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    var selectedDate by remember { mutableStateOf(todayDate) }

    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)
    val isToday = selectedDate == todayDate

    // Deduplicate event list based on title, time, and date
    val deduplicatedEvents = eventList.distinctBy { Triple(it.title, it.startTime, it.date) }

    Scaffold(
        bottomBar = { BottomNavBar(currentScreen = "calendar", navController = navController) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
        ) {
            Text(
                "July 2025",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            val days = (1..31).map { day ->
                val cal = Calendar.getInstance().apply { set(2025, Calendar.JULY, day) }
                val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val label = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                Triple(label ?: "", day, df.format(cal.time))
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp)
            ) {
                days.forEach { (label, day, fullDate) ->
                    val isSelected = selectedDate == fullDate
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) purple else Color.Transparent)
                            .clickable { selectedDate = fullDate }
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(label, color = if (isSelected) Color.White else Color.Black)
                        Text(day.toString(), fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.Black)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            val hours = (9..19).toList()
            val eventsToday = deduplicatedEvents.filter { it.date == selectedDate }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                hours.forEach { hour ->
                    val timeLabel = String.format("%02d:00", hour)
                    val eventsAtHour = eventsToday.filter {
                        val eventHour = it.startTime.substringBefore(":").toIntOrNull()
                        eventHour == hour || (eventHour == hour - 1 && it.startTime.contains(":30"))
                    }

                    val showNowBar = isToday && hour == currentHour && currentHour in 9..19

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(IntrinsicSize.Min)
                        ) {
                            Text(
                                text = timeLabel,
                                modifier = Modifier.width(60.dp),
                                fontSize = 14.sp,
                                textAlign = TextAlign.End,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                if (eventsAtHour.isNotEmpty()) {
                                    Column {
                                        eventsAtHour.forEach { event ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                colors = CardDefaults.cardColors(containerColor = lightPurple),
                                                shape = RoundedCornerShape(16.dp)
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Text(
                                                        event.title,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.AccessTime,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(16.dp),
                                                            tint = Color.Black
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("${event.startTime} - ${event.endTime}", fontSize = 13.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    )
                                }

                                if (showNowBar) {
                                    val percent = currentMinute / 60f
                                    Divider(
                                        color = red,
                                        thickness = 2.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = (48.dp * percent))
                                            .align(Alignment.TopStart)
                                    )
                                }
                            }
                        }

                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}
