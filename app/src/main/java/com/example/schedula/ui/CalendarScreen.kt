package com.example.schedula.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
    var selectedView by remember { mutableStateOf("Month") }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)
    val isToday = selectedDate == todayDate

    val deduplicatedEvents = eventList.distinctBy { Triple(it.title, it.startTime, it.date) }

    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddEventDialog(
            onDismiss = { showAddDialog = false },
            onSave = { data ->
                eventList.add(Event(data.title, data.startTime, data.endTime, data.date))
                selectedDate = data.date
            }
        )
    }

    if (selectedEvent != null) {
        AlertDialog(
                onDismissRequest = { selectedEvent = null },
                confirmButton = {
                    Button(onClick = { selectedEvent = null },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = purple,
                            contentColor = Color.White
                        )) {
                        Text("Close")
                    }
                },
                title = {
                    Text(selectedEvent!!.title)
                },
                text = {
                    Column {
                        Text("Start: ${selectedEvent!!.startTime}")
                        Text("End: ${selectedEvent!!.endTime}")
                        Text("Date: ${selectedEvent!!.date}")

                        if (selectedEvent!!.title.contains("study", ignoreCase = true)) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                selectedEvent = null
                                navController.navigate(("timer"))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = purple,
                                contentColor = Color.White
                            )) {
                                Text("Open Timer")
                            }
                        }
                    }
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Month", "Week", "Day").forEach { view ->
                    val isSelected = selectedView == view
                    Text(
                        view,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color.White else purple)
                            .clickable { selectedView = view }
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (selectedView == "Month") {
                val daysOfWeek = listOf("S", "M", "T", "W", "Th", "F", "S")
                val startDayRaw = Calendar.getInstance().apply { set(2025, Calendar.JULY, 1) }.get(Calendar.DAY_OF_WEEK)
                val startDay = (startDayRaw - 1 + 7) % 7
                val totalDays = 31

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        daysOfWeek.forEach { label ->
                            Text(label, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        }
                    }

                    val calendarRows = mutableListOf<List<Int?>>()
                    var currentDay = 1
                    for (week in 0..5) {
                        val row = mutableListOf<Int?>()
                        for (day in 0..6) {
                            if ((week == 0 && day < startDay) || currentDay > totalDays) {
                                row.add(null)
                            } else {
                                row.add(currentDay++)
                            }
                        }
                        calendarRows.add(row)
                    }

                    calendarRows.forEach { week ->
                        Row(Modifier.fillMaxWidth()) {
                            week.forEach { day ->
                                val dateStr = if (day != null) "2025-07-${"%02d".format(day)}" else ""
                                val isSelected = selectedDate == dateStr
                                val hasEvent = deduplicatedEvents.any { it.date == dateStr }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) purple else Color.Transparent)
                                        .clickable { if (dateStr.isNotEmpty()) selectedDate = dateStr },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        if (day != null) {
                                            Text(
                                                day.toString(),
                                                color = if (isSelected) Color.White else Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (hasEvent) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Box(
                                                    Modifier
                                                        .size(6.dp)
                                                        .clip(RoundedCornerShape(50))
                                                        .background(Color.White)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (selectedView == "Week") {
                val days = (1..31).map { day ->
                    val cal = Calendar.getInstance().apply { set(2025, Calendar.JULY, day) }
                    val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val label = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                    Triple(label ?: "", day, df.format(cal.time))
                }

                val lazyListState = rememberLazyListState()
                val todayIndex = days.indexOfFirst { it.third == todayDate }

                LaunchedEffect(selectedView) {
                    if (selectedView == "Week" && todayIndex != -1) {
                        lazyListState.scrollToItem(todayIndex)
                    }
                }

                LazyRow(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    items(days.size) { index ->
                        val (label, day, fullDate) = days[index]
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
                if (bedHour >= wakeHour) (wakeHour..bedHour).toList()
                else (wakeHour..23).toList() + (0..bedHour).toList()
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
                                                    .padding(vertical = 4.dp)
                                                    .clickable { selectedEvent = event },
                                                colors = CardDefaults.cardColors(containerColor = lightPurple),
                                                shape = RoundedCornerShape(16.dp)
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Text(event.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
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
