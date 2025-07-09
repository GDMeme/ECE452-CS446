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
import com.example.schedula.ui.OnboardingDataClass
import android.util.Log
import android.widget.Toast
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

fun convertTo24Hr(time12hr: String): String {
    val cleaned = time12hr.replace(Regex("(?i)(AM|PM)")) { " ${it.value.uppercase()}" }.trim()
    val sdf12 = SimpleDateFormat("hh:mm a", Locale.US)
    val sdf24 = SimpleDateFormat("HH:mm", Locale.US)
    return try {
        sdf24.format(sdf12.parse(cleaned)!!)
    } catch (e: Exception) {
        Log.e("TimeParse", "Failed to parse time: $time12hr → cleaned: $cleaned", e)
        time12hr
    }
}

@Composable
fun ScheduleUploadScreen(navController: NavController, onHtmlExtracted: (String) -> Unit) {
    val context = LocalContext.current
    var htmlContent by remember { mutableStateOf<String?>(null) }

    fun extractFullHtmlFromMht(mhtText: String): String {
        val htmlParts = Regex("(?si)(<html.*?</html>)").findAll(mhtText).map { it.value }.toList()
        return if (htmlParts.isNotEmpty()) htmlParts.joinToString("<hr/>")
        else "No HTML content found in MHT file."
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val text = BufferedReader(InputStreamReader(stream)).readText()
                    val extractedHtml = extractFullHtmlFromMht(text)

                    htmlContent = extractedHtml
                    onHtmlExtracted(extractedHtml)

                    Log.d("MHT_VIEWER", "Extracted HTML content (first 500 chars): ${extractedHtml.take(500)}")

                    val doc = Jsoup.parse(extractedHtml)
                    val entries = mutableListOf<ScheduleEntry>()

                    for (i in 0..20) {
                        val schedEl = doc.getElementById("MTG_SCHED$$i")
                        val locEl = doc.getElementById("MTG_LOC$$i")
                        val compEl = doc.getElementById("MTG_COMP$$i")

                        if (schedEl != null && locEl != null) {
                            val schedText = schedEl.text().trim() // e.g., "MWF 1:00PM - 2:20PM"
                            val location = locEl.text().trim()

                            val match = Regex("""^([MTWRFh]+)\s+(\d{1,2}:\d{2}[AP]M)\s*-\s*(\d{1,2}:\d{2}[AP]M)$""")
                                .find(schedText)

                            if (match != null) {
                                val daysRaw = match.groupValues[1]
                                val start12 = match.groupValues[2]
                                val end12 = match.groupValues[3]

                                val start = convertTo24Hr(start12)
                                val end = convertTo24Hr(end12)

                                val dayMap = mapOf(
                                    'M' to "Monday",
                                    'T' to "Tuesday",
                                    'W' to "Wednesday",
                                    'R' to "Thursday",
                                    'F' to "Friday",
                                    'h' to "Thursday" // in case 'Th' got split into T + h
                                )

                                val days = daysRaw.mapNotNull { dayMap[it] }.distinct()
                                val codeEl = doc.getElementById("MTG_CLASSNAME$$i")
                                val courseCode = codeEl?.text()?.substringBefore(" -")?.trim() ?: "COURSE$i"

                                for (day in days) {
                                    entries.add(ScheduleEntry(courseCode, start, end, day, location))
                                }
                            } else {
                                Log.w("MHT_VIEWER", "Skipping row $i: unexpected schedule format: $schedText")
                            }
                        }
                    }

                    if (entries.isNotEmpty()) {
                        OnboardingDataClass.scheduleData.clear()
                        OnboardingDataClass.scheduleData.addAll(entries)
                        Log.d("MHT_VIEWER", "Parsed ${entries.size} schedule entries.")
                    } else {
                        Log.w("MHT_VIEWER", "No valid schedule entries found.")
                    }

                    Toast.makeText(context, "File Upload Success", Toast.LENGTH_LONG).show()
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
                ) { Text("Next", color = Color(0xFF5B5F9D)) }
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
