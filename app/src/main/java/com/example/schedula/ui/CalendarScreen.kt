package com.example.schedula.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Week at a Glance", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* do something here, possibly add your own activities */ }) {
                Text("+")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Days Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("Mon", "Tues", "Wed", "Thur", "Fri", "Sat", "Sun").forEach {
                    Text(it, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            //mock data time slots
            listOf(
                TimeSlot("CS 246", "10:30 AM - 11:20 AM", Color(0xFF3B82F6), 1),
                TimeSlot("MATH 135", "9:30 AM - 10:20 AM", Color(0xFF10B981), 2),
                TimeSlot("Study Block", "2:30 PM - 4:00 PM", Color(0xFFEF4444), 1),
                TimeSlot("Workout", "6:00 PM - 7:00 PM", Color(0xFFF97316), 2),
                TimeSlot("Sleep", "11:30 PM - 7:30 AM", Color(0xFF9CA3AF), 0),
            ).forEach {
                CalendarEventCard(it)
            }
        }
    }
}

data class TimeSlot(val title: String, val time: String, val color: Color, val dayIndex: Int)

@Composable
fun CalendarEventCard(slot: TimeSlot) {
    Row(modifier = Modifier.fillMaxWidth()) {
        repeat(slot.dayIndex) {
            Spacer(modifier = Modifier.weight(1f))
        }
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
                .background(slot.color)
        ) {
            Column(
                modifier = Modifier
                    .background(slot.color)
                    .padding(8.dp)
            ) {
                Text(text = slot.title, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = slot.time, fontSize = 12.sp, color = Color.White)
            }
        }
        repeat(6 - slot.dayIndex) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
