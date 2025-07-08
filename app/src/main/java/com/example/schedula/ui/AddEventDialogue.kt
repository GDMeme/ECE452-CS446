package com.example.schedula.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schedula.ui.Event
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onSave: (Event) -> Unit
) {
    val backgroundColor = Color(0xFFF0E7F4)
    val borderPurple = Color(0xFF9C89B8)

    var title by remember { mutableStateOf("") }

    val startCalendar = remember { mutableStateOf(Calendar.getInstance()) }
    val endCalendar = remember { mutableStateOf(Calendar.getInstance()) }

    var showStartDialog by remember { mutableStateOf(false) }
    var showEndDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val event = Event(
                    title = title,
                    startTime = timeFormat.format(startCalendar.value.time),
                    endTime = timeFormat.format(endCalendar.value.time),
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(startCalendar.value.time)
                )
                onSave(event)
                onDismiss()
            }) {
                Text("Save", color = borderPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = borderPurple)
            }
        },
        title = {
            Text("New Schedule", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Starts", fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${dateFormat.format(startCalendar.value.time)}    ${timeFormat.format(startCalendar.value.time)}",
                    modifier = Modifier
                        .clickable { showStartDialog = true }
                        .padding(vertical = 8.dp),
                    color = borderPurple
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Ends", fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${dateFormat.format(endCalendar.value.time)}    ${timeFormat.format(endCalendar.value.time)}",
                    modifier = Modifier
                        .clickable { showEndDialog = true }
                        .padding(vertical = 8.dp),
                    color = borderPurple
                )
            }
        },
        containerColor = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    )

    if (showStartDialog) {
        DateTimePickerDialog(
            initialCalendar = startCalendar.value,
            onConfirm = {
                startCalendar.value = it
                showStartDialog = false
            },
            onDismiss = { showStartDialog = false }
        )
    }

    if (showEndDialog) {
        DateTimePickerDialog(
            initialCalendar = endCalendar.value,
            onConfirm = {
                endCalendar.value = it
                showEndDialog = false
            },
            onDismiss = { showEndDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    initialCalendar: Calendar,
    onConfirm: (Calendar) -> Unit,
    onDismiss: () -> Unit
) {
    var stage by remember { mutableStateOf("date") }
    val selectedDateMillis = remember { mutableStateOf(initialCalendar.timeInMillis) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis.value
    )

    val timePickerState = rememberTimePickerState(
        initialHour = initialCalendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = initialCalendar.get(Calendar.MINUTE),
        is24Hour = false
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (stage == "date") {
                    selectedDateMillis.value = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    stage = "time"
                } else {
                    val selectedCalendar = Calendar.getInstance().apply {
                        // Convert UTC millis to local date
                        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                            timeInMillis = selectedDateMillis.value
                        }
                        set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
                        set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
                        set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                        set(Calendar.SECOND, 0)
                    }
                    onConfirm(selectedCalendar)
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (stage == "time") stage = "date"
                else onDismiss()
            }) {
                Text("Cancel")
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (stage == "date") {
                DatePicker(state = datePickerState)
            } else {
                TimePicker(state = timePickerState)
            }
        }
    }
}
