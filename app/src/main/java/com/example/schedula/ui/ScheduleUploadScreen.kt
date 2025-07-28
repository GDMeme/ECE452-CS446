package com.example.schedula.ui

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.navigationBarsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleUploadScreen(
    navController: androidx.navigation.NavController,
    scheduleViewModel: ScheduleViewModel = viewModel()
) {
    val context = LocalContext.current
    var htmlContent by remember { mutableStateOf<String?>(null) }
    var isGeneratingSchedule by remember { mutableStateOf(false) }

    fun convertTo24Hr(time12hr: String): String {
        return try {
            val sdf12 = SimpleDateFormat("hh:mma", Locale.US)
            val sdf24 = SimpleDateFormat("HH:mm", Locale.US)
            sdf24.format(sdf12.parse(time12hr.replace(" ", "").uppercase(Locale.US))!!)
        } catch (e: Exception) {
            Log.e("TimeParse", "Failed to parse time: $time12hr", e)
            time12hr
        }
    }

    fun expandWeeklyRecurringEvents(
        baseEvents: List<Event>,
        startDateStr: String = "2025-05-01",
        endDateStr: String = "2025-08-07"
    ): List<Event> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startDate = sdf.parse(startDateStr) ?: return emptyList()
        val endDate = sdf.parse(endDateStr) ?: return emptyList()

        val expandedEvents = mutableListOf<Event>()

        fun getDayOfWeekFromDate(dateStr: String): Int {
            val date = sdf.parse(dateStr) ?: return Calendar.MONDAY
            val cal = Calendar.getInstance()
            cal.time = date
            return cal.get(Calendar.DAY_OF_WEEK)
        }

        baseEvents.forEach { event ->
            val eventDayOfWeek = getDayOfWeekFromDate(event.date)
            val weeklyCal = Calendar.getInstance().apply { time = startDate }

            while (weeklyCal.get(Calendar.DAY_OF_WEEK) != eventDayOfWeek) {
                weeklyCal.add(Calendar.DATE, 1)
            }

            while (!weeklyCal.time.after(endDate)) {
                val dateStr = sdf.format(weeklyCal.time)
                expandedEvents.add(event.copy(date = dateStr))
                weeklyCal.add(Calendar.DATE, 7)
            }
        }

        return expandedEvents
    }

    fun sendScheduleRequestOverWebSocket(
        json: JSONObject,
        onResponse: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder()
            .url("wss://ece452-cs446-fcft.onrender.com")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(json.toString())
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                onResponse(text)
                webSocket.close(1000, "Completed")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onError(t.message ?: "Unknown error")
            }
        }

        client.newWebSocket(request, listener)
        client.dispatcher.executorService.shutdown()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val text = BufferedReader(InputStreamReader(stream)).readText()
                    val doc = Jsoup.parse(text)
                    val rows = doc.select("tr")
                    val entries = mutableListOf<Event>()
                    var currentCourse: String? = null

                    for (row in rows) {
                        val cells = row.select("td")
                        if (cells.isEmpty()) continue
                        val firstText = cells[0].text().trim()
                        if (Regex("^[A-Z]{2,4} \\d{3}[A-Z]? -").containsMatchIn(firstText)) {
                            currentCourse = firstText.split(" -")[0].trim()
                            continue
                        }
                        if (currentCourse == null || currentCourse == "ECE 401") continue

                        try {
                            val component = cells.getOrNull(2)?.text()?.trim() ?: continue
                            val timeText = cells.getOrNull(3)?.text()?.trim() ?: continue
                            val dateRange = cells.getOrNull(6)?.text()?.trim() ?: continue
                            if ("TBA" in timeText || !timeText.contains("-")) continue

                            val match = Regex("([MTWRF]+)\\s+(\\d{1,2}:\\d{2}[APMapm]+)\\s*-\\s*(\\d{1,2}:\\d{2}[APMapm]+)").find(timeText)
                            if (match == null) continue

                            val days = match.groupValues[1]
                            val start24 = convertTo24Hr(match.groupValues[2])
                            val end24 = convertTo24Hr(match.groupValues[3])

                            val rangeParts = dateRange.split(" - ")
                            if (rangeParts.size != 2) continue

                            val (startStr, endStr) = rangeParts
                            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                            val startDate = sdf.parse(startStr.trim()) ?: continue
                            val endDate = sdf.parse(endStr.trim()) ?: continue

                            val dayMap = mapOf(
                                'M' to Calendar.MONDAY,
                                'T' to Calendar.TUESDAY,
                                'W' to Calendar.WEDNESDAY,
                                'R' to Calendar.THURSDAY,
                                'F' to Calendar.FRIDAY
                            )

                            val windowStart = Calendar.getInstance().apply {
                                set(2025, Calendar.MAY, 1, 0, 0, 0)
                            }
                            val windowEnd = Calendar.getInstance().apply {
                                set(2025, Calendar.AUGUST, 7, 23, 59, 59)
                            }

                            for (d in days) {
                                val dayCode = dayMap[d] ?: continue
                                val cal = Calendar.getInstance()
                                cal.time = windowStart.time

                                // Advance to the first occurrence of this weekday
                                while (cal.get(Calendar.DAY_OF_WEEK) != dayCode) {
                                    cal.add(Calendar.DATE, 1)
                                }

                                // Add weekly recurring entries until end of window
                                while (!cal.after(windowEnd)) {
                                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
                                    entries.add(Event("$currentCourse $component", start24, end24, dateStr))
                                    cal.add(Calendar.DATE, 7)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("SCHEDULE_PARSE", "Skipping malformed row", e)
                            continue
                        }
                    }

                    Log.d("SCHEDULE_ENTRIES", "Parsed entries: ${entries.joinToString("\n")}")

                    if (entries.isNotEmpty()) {
                        htmlContent = "Uploaded"
                        Toast.makeText(context, "File upload successful, generating schedule...", Toast.LENGTH_LONG).show()
                        isGeneratingSchedule = true

                        val dedupedEntries = entries.distinctBy { Triple(it.title, it.startTime, it.date) }

                        // Prepare first week of May fixed events to send
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        val firstWeekStart = sdf.parse("2025-05-01")!!
                        val firstWeekEndCal = Calendar.getInstance().apply {
                            time = firstWeekStart
                            add(Calendar.DATE, 6) // 7 days total
                        }
                        val firstWeekEnd = firstWeekEndCal.time

                        val firstWeekEvents = dedupedEntries.filter { event ->
                            val eventDate = sdf.parse(event.date)
                            eventDate != null && !eventDate.before(firstWeekStart) && !eventDate.after(firstWeekEnd)
                        }

                        // Expand the full fixed events range locally after response
                        val expandedFixedFullRange = expandWeeklyRecurringEvents(dedupedEntries)

                        // Prepare flexibleTasks JSON array
                        val flexibleTasks = JSONArray()
                        if (OnboardingDataClass.studyHours.isNotBlank()) flexibleTasks.put("Study")
                        if (!OnboardingDataClass.exerciseFrequency.equals("Never", ignoreCase = true)) flexibleTasks.put("Workout")
                        if (OnboardingDataClass.hobbiesSelected.size != 0) flexibleTasks.put(OnboardingDataClass.hobbiesSelected)
                        OnboardingDataClass.customRoutines.forEach { routine ->
                            if (routine.isNotBlank()) flexibleTasks.put(routine)
                        }

                        val payload = JSONObject().apply {
                            put("fixedEvents", JSONArray(Json.encodeToString(firstWeekEvents))) // only first week sent
                            put("flexibleTasks", flexibleTasks)
                        }
                        val json = JSONObject().apply {
                            put("type", "generate-schedule")
                            put("payload", payload)
                        }

                        sendScheduleRequestOverWebSocket(
                            json,
                            onResponse = { responseText ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        Log.d("responseText: ", responseText)

                                        val jsonObject = JSONObject(responseText)
                                        val rawPayload = jsonObject.getString("payload")

                                        // Strip code block formatting
                                        val cleanJson = rawPayload
                                            .removePrefix("```json")
                                            .removePrefix("```")
                                            .removeSuffix("```")
                                            .trim()

                                        val wsEvents = Json.decodeFromString<List<Event>>(cleanJson)
                                        val expandedWs = expandWeeklyRecurringEvents(wsEvents)

                                        // Combine full expanded fixed events + expanded ws events
                                        val combined = (expandedFixedFullRange + expandedWs)
                                            .distinctBy { Triple(it.title, it.startTime, it.date) }

                                        scheduleViewModel.setEvents(combined)

                                        Toast.makeText(context, "Schedule generated successfully!", Toast.LENGTH_SHORT).show()
                                        isGeneratingSchedule = false
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Failed to parse schedule response.", Toast.LENGTH_LONG).show()
                                        isGeneratingSchedule = false
                                        Log.e("ScheduleUpload", "WS parse error", e)
                                    }
                                }
                            },
                            onError = { errorMsg ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(context, "WebSocket error: $errorMsg", Toast.LENGTH_LONG).show()
                                    isGeneratingSchedule = false
                                }
                            }
                        )
                    } else {
                        Toast.makeText(context, "No valid schedule entries found", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    )

    val hasUploadedBefore = OnboardingDataClass.fixedEvents.isNotEmpty()

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            if (htmlContent != null) {
                Box(
                    Modifier
                        .background(Color.White)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .navigationBarsPadding()
                ) {
                    Button(
                        onClick = {
                            navController.navigate("success") {
                                popUpTo("scheduleUpload") { inclusive = true }
                            }
                        },
                        enabled = (!isGeneratingSchedule && scheduleViewModel.events.isNotEmpty()) || hasUploadedBefore,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(bottom = 24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C89B8))
                    ) {
                        Text("Next", color = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Custom top bar (matches CustomRoutineScreen)
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
                        .clickable { navController.popBackStack() }
                )
                Text(
                    text = "Schedula",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Schedule Upload",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                "Personalize your calendar by uploading your school schedule from the UW Portal",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = { launcher.launch(arrayOf("*/*")) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C89B8))
            ) {
                Text("Select MHT File", color = Color.White)
            }

            htmlContent?.let {
                Text(
                    "File uploaded successfully!",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF4CAF50))
                )
            } ?: Text(
                "No file selected yet",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )

            if (isGeneratingSchedule) {
                Spacer(Modifier.height(20.dp))
                Text(
                    "Generating schedule, please wait...",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFF9C89B8),
                    style = MaterialTheme.typography.bodyMedium
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            Spacer(Modifier.weight(1f)) // Push content up so bottomBar does not overlap
        }
    }
}
