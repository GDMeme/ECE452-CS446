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
import com.example.schedula.ui.components.AddEventDialog
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

//    val eventList = remember {
//        mutableStateListOf(
//            Event("🧹 House chores", "09:00", "10:00", todayDate),
//            Event("🧘 Yoga Class", "10:00", "11:00", todayDate),
//            Event("🍳 Breakfast", "12:00", "12:30", todayDate),
//            Event("💡 Focus Time", "13:00", "15:00", todayDate),
//            Event("💡 Focus Time", "16:00", "18:00", todayDate)
//        )
//    }

    eventList.apply {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

            val calendar = Calendar.getInstance()
            calendar.set(2025, Calendar.JULY, 1) // Start of July

            while (calendar.get(Calendar.MONTH) == Calendar.JULY) {
                val dateStr = dateFormat.format(calendar.time)
                val dayName = dayFormat.format(calendar.time).uppercase()
                // === Monday July 7 ===
                add(Event("🍽️ Breakfast", "08:00", "08:30", "2025-07-07"))
                add(Event("💡 Study Session 1", "09:00", "10:00", "2025-07-07"))
                add(Event("🍱 Lunch", "12:00", "13:00", "2025-07-07"))
                add(Event("🍽️ Dinner", "18:00", "19:00", "2025-07-07"))
                add(Event("💡 Study Session 2", "20:00", "21:00", "2025-07-07"))

                // === Tuesday July 8 ===
                add(Event("🍽️ Breakfast", "08:00", "08:30", "2025-07-08"))
                add(Event("💡 Study Session 1", "09:00", "10:00", "2025-07-08"))
                add(Event("🍱 Lunch", "12:00", "13:00", "2025-07-08"))
                add(Event("🏋️ Exercise", "18:00", "19:00", "2025-07-08"))
                add(Event("🍽️ Dinner", "18:00", "19:00", "2025-07-08"))
                add(Event("🎬 Movie Night", "21:00", "22:00", "2025-07-08"))
                add(Event("💡 Study Session 2", "20:00", "21:00", "2025-07-08"))

                // === Wednesday July 9 ===
                add(Event("🍽️ Breakfast", "08:00", "08:30", "2025-07-09"))
                add(Event("💡 Study Session 1", "09:00", "10:00", "2025-07-09"))
                add(Event("🍱 Lunch", "12:00", "13:00", "2025-07-09"))
                add(Event("🏃 Exercise", "15:00", "16:00", "2025-07-09"))
                add(Event("💡 Study Session", "17:00", "18:00", "2025-07-09"))
                add(Event("🍽️ Dinner", "18:00", "19:00", "2025-07-09"))
                add(Event("💡 Study Session 2", "20:00", "21:00", "2025-07-09"))

                // === Thursday July 10 ===
                add(Event("🍽️ Breakfast", "08:00", "08:30", "2025-07-10"))
                add(Event("💡 Study Session 1", "09:00", "10:00", "2025-07-10"))
                add(Event("🧘 Yoga", "10:00", "11:00", "2025-07-10"))
                add(Event("🍱 Lunch", "12:00", "13:00", "2025-07-10"))
                add(Event("🍽️ Dinner", "18:00", "19:00", "2025-07-10"))
                add(Event("💡 Study Session 2", "20:00", "21:00", "2025-07-10"))

                // === Friday July 11 ===
                add(Event("🍽️ Breakfast", "08:00", "08:30", "2025-07-11"))
                add(Event("💡 Study Session 1", "09:00", "10:00", "2025-07-11"))
                add(Event("🍱 Lunch", "12:00", "13:00", "2025-07-11"))
                add(Event("🎸 Guitar Practice", "14:00", "15:00", "2025-07-11"))
                add(Event("🍽️ Dinner", "18:00", "19:00", "2025-07-11"))
                add(Event("💡 Study Session 2", "20:00", "21:00", "2025-07-11"))

                // === Saturday July 12 ===
                add(Event("🍽️ Breakfast", "08:00", "08:30", "2025-07-12"))
                add(Event("💡 Study Session 1", "09:00", "10:00", "2025-07-12"))
                add(Event("🍱 Lunch", "12:00", "13:00", "2025-07-12"))
                add(Event("🍽️ Dinner", "18:00", "19:00", "2025-07-12"))
                add(Event("💡 Study Session 2", "20:00", "21:00", "2025-07-12"))

                // === Sunday July 13 ===
                add(Event("🍽️ Breakfast", "08:00", "08:30", "2025-07-13"))
                add(Event("💡 Study Session 1", "09:00", "10:00", "2025-07-13"))
                add(Event("🍱 Lunch", "12:00", "13:00", "2025-07-13"))
                add(Event("🍽️ Dinner", "18:00", "19:00", "2025-07-13"))
                add(Event("💡 Study Session 2", "20:00", "21:00", "2025-07-13"))


                OnboardingDataClass.scheduleData.forEach { item ->
                    if (item.day.uppercase() == dayName) {
                        add(
                            Event(
                                title = "${item.courseCode} @ ${item.location}",
                                startTime = item.startTime,
                                endTime = item.endTime,
                                date = dateStr
                            )
                        )
                    }
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }


    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddEventDialog(
            onDismiss = { showAddDialog = false },
            onSave = { data ->
                eventList.add(
                    Event(
                        title = data.title,
                        startTime = data.startTime,
                        endTime = data.endTime,
                        date = data.date
                    )
                )
                selectedDate = data.date // update selected date after saving
            }
        )
    }


    Scaffold(
        bottomBar = { BottomNavBar(currentScreen = "calendar", navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = purple,
                contentColor = Color.White
            ) {
                Text("+", fontSize = 24.sp)
            }
        }
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

            val eventsToday = deduplicatedEvents.filter { it.date == selectedDate }

            val wakeTime = OnboardingDataClass.wakeTime
            val bedTime = OnboardingDataClass.bedTime

            val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

            val wakeHour by remember(wakeTime) {
                mutableStateOf(
                    try {
                        val wakeDate = timeFormat.parse(wakeTime)
                        Calendar.getInstance().apply { time = wakeDate!! }.get(Calendar.HOUR_OF_DAY)
                    } catch (e: Exception) {
                        9
                    }
                )
            }

            val bedHour by remember(bedTime) {
                mutableStateOf(
                    try {
                        val bedDate = timeFormat.parse(bedTime)
                        Calendar.getInstance().apply { time = bedDate!! }.get(Calendar.HOUR_OF_DAY)
                    } catch (e: Exception) {
                        19
                    }
                )
            }

            val hours = remember(wakeTime, bedTime) {
                val parsedWake = try {
                    val date = timeFormat.parse(wakeTime)
                    Calendar.getInstance().apply { time = date!! }
                } catch (e: Exception) {
                    Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 9) }
                }

                val parsedBed = try {
                    val date = timeFormat.parse(bedTime)
                    Calendar.getInstance().apply { time = date!! }
                } catch (e: Exception) {
                    Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 19) }
                }

                val startHour = parsedWake.get(Calendar.HOUR_OF_DAY)
                val endHour = parsedBed.get(Calendar.HOUR_OF_DAY)

                if (endHour >= startHour) {
                    (startHour..endHour).toList()
                } else {
                    // Handle wraparound, e.g., 10 PM to 6 AM
                    (startHour..23).toList() + (0..endHour).toList()
                }
            }

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

fun convertDayToDate(dayName: String): String {
    val daysOfWeek = listOf("SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY")
    val today = Calendar.getInstance()
    val target = daysOfWeek.indexOf(dayName.uppercase())

    for (i in 0..6) {
        val check = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, i) }
        if (check.get(Calendar.DAY_OF_WEEK) == target + 1) {
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(check.time)
        }
    }

    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today.time) // fallback
}
