package com.example.schedula.ui

import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import android.util.Log
import android.widget.Toast
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.schedula.ui.OnboardingDataClass
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import org.json.JSONArray
import kotlinx.coroutines.*
import okio.ByteString
import java.util.concurrent.TimeUnit

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

// Helper function to send WebSocket request and handle callbacks
fun sendScheduleRequestOverWebSocket(
    json: JSONObject,
    onResponse: (String) -> Unit,
    onError: (String) -> Unit
) {
    val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS) // Disable timeout for WebSocket
        .build()

    val request = Request.Builder()
        .url("wss://ece452-cs446-fcft.onrender.com") // WebSocket URL
        .build()

    val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            webSocket.send(json.toString())
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            onResponse(text)
            // Close after response received (optional)
            webSocket.close(1000, "Completed")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            onError(t.message ?: "Unknown error")
        }
    }

    client.newWebSocket(request, listener)
    client.dispatcher.executorService.shutdown()
}

@Composable
fun ScheduleUploadScreen(navController: NavController, eventListState: SnapshotStateList<Event>) {
    val context = LocalContext.current
    var htmlContent by remember { mutableStateOf<String?>(null) }

    fun extractFullHtmlFromMht(mhtText: String): String {
        val htmlParts = Regex("(?si)(<html.*?</html>)").findAll(mhtText).map { it.value }.toList()
        return if (htmlParts.isNotEmpty()) htmlParts.joinToString("<hr/>") else "No HTML content found."
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val text = BufferedReader(InputStreamReader(stream)).readText()
                    val extractedHtml = extractFullHtmlFromMht(text)
                    htmlContent = extractedHtml
                    Log.d("MHT_VIEWER", "Extracted HTML content")

                    val doc = Jsoup.parse(extractedHtml)
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

                        // Ignore ECE401 (Information Session)
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
                            if (rangeParts.size != 2) {
                                Log.e("SCHEDULE_PARSE", "Skipping row with malformed dateRange: $dateRange")
                                continue
                            }

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
                                set(2025, Calendar.JULY, 7, 0, 0, 0)
                            }
                            val windowEnd = Calendar.getInstance().apply {
                                set(2025, Calendar.JULY, 11, 23, 59, 59)
                            }

                            for (d in days) {
                                val dayCode = dayMap[d] ?: continue
                                val cal = Calendar.getInstance()
                                cal.time = windowStart.time
                                while (!cal.after(windowEnd)) {
                                    if (cal.get(Calendar.DAY_OF_WEEK) == dayCode) {
                                        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
                                        entries.add(Event("$currentCourse $component", start24, end24, dateStr))
                                        break
                                    }
                                    cal.add(Calendar.DATE, 1)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("SCHEDULE_PARSE", "Skipping malformed row", e)
                            continue
                        }
                    }

                    Log.d("SCHEDULE_ENTRIES", "Parsed entries: ${entries.joinToString("\n")}")

                    if (entries.isNotEmpty()) {
                        eventListState.addAll(entries)
                        Toast.makeText(context, "File Upload Success", Toast.LENGTH_LONG).show()

                        // Build the JSON payload for WebSocket request
                        val fixedEvents = JSONArray().apply {
                            put(JSONObject().apply {
                                put("day", "Monday")
                                put("start", "10:00")
                                put("end", "12:00")
                                put("title", "Math Class")
                            })
                            put(JSONObject().apply {
                                put("day", "Wednesday")
                                put("start", "14:00")
                                put("end", "15:30")
                                put("title", "Chemistry Lab")
                            })
                        }

                        // Get flexible tasks (questionnaire data)
                        val flexibleTasks = JSONArray()

                        if (OnboardingDataClass.studyHours.isNotBlank()) {
                            flexibleTasks.put("Study")
                        }

                        if (!OnboardingDataClass.exerciseFrequency.equals("Never", ignoreCase = true)) {
                            flexibleTasks.put("Workout")
                        }

                        if (OnboardingDataClass.hobby.isNotBlank()) {
                            flexibleTasks.put(OnboardingDataClass.hobby)
                        }

                        OnboardingDataClass.customRoutines.forEach { routine ->
                            if (routine.isNotBlank()) {
                                flexibleTasks.put(routine)
                            }
                        }

                        val payload = JSONObject().apply {
                            put("fixedEvents", fixedEvents)
                            put("flexibleTasks", flexibleTasks)
                        }

                        val json = JSONObject().apply {
                            put("type", "generate-schedule")
                            put("payload", payload)
                        }

                        // Send via WebSocket
                        CoroutineScope(Dispatchers.Main).launch {
                            sendScheduleRequestOverWebSocket(json,
                                onResponse = { responseText ->
                                    CoroutineScope(Dispatchers.Main).launch {
                                        println("WebSocket Response: $responseText")
                                        Toast.makeText(context, "Received schedule response", Toast.LENGTH_SHORT).show()
                                        // TODO: Parse responseText and update your UI or eventListState if needed
                                    }
                                },
                                onError = { errorMsg ->
                                    CoroutineScope(Dispatchers.Main).launch {
                                        println("WebSocket error: $errorMsg")
                                        Toast.makeText(context, "WebSocket error: $errorMsg", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    } else {
                        Toast.makeText(context, "No valid schedule entries found", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0E7F4)) // match your app's background
            .padding(horizontal = 24.dp, vertical = 30.dp),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            bottomBar = {
                Button(
                    onClick = {
                        navController.navigate("success") {
                            popUpTo("scheduleUpload") { inclusive = true }
                        }
                    },
                    enabled = htmlContent != null,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                ) {
                    Text("Next", color = Color.White)
                }
            }
        ) { padding ->
            Column(Modifier.padding(padding).fillMaxSize()) {
                Text(
                    "Personalize your calendar by uploading your school schedule from the UW Portal",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = { launcher.launch(arrayOf("*/*")) },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                ) {
                    Text("Select MHT File", color = Color.White)
                }

                htmlContent?.let { _ ->
                    Text(
                        "File uploaded successfully!",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF4CAF50) // green for success
                    )
                } ?: Text(
                    "No file selected yet",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }
        }
    }
}
