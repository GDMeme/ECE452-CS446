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
                        eventListState.clear()
                        eventListState.addAll(entries)
                        Toast.makeText(context, "File Upload Success", Toast.LENGTH_LONG).show()
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
            .background(Color(0xFFEFF4F9))
            .padding(horizontal = 24.dp, vertical = 30.dp),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            bottomBar = {
                Button(
                    onClick = { navController.navigate("calendar") },
                    enabled = htmlContent != null,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD7D9F7))
                ) {
                    Text("Next", color = Color(0xFF5B5F9D))
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
                ) {
                    Text("Select MHT File")
                }

                htmlContent?.let { html ->
                    AndroidView(
                        factory = {
                            WebView(it).apply {
                                webViewClient = WebViewClient()
                                loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    )
                } ?: Text("No file selected yet")
            }
        }
    }
}
