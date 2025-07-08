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
import java.io.BufferedReader
import java.io.InputStreamReader
import com.example.schedula.ui.OnboardingDataClass

@Composable
fun ScheduleUploadScreen(navController: NavController, onHtmlExtracted: (String) -> Unit) {
    val context = LocalContext.current
    var htmlContent by remember { mutableStateOf<String?>(null) }

    // Extracts all HTML parts from MHT content
    fun extractFullHtmlFromMht(mhtText: String): String {
        val htmlParts = mutableListOf<String>()
        val regex = Regex("(?si)(<html.*?</html>)")
        regex.findAll(mhtText).forEach { match ->
            htmlParts.add(match.value)
        }
        return if (htmlParts.isNotEmpty()) {
            htmlParts.joinToString("<hr/>")
        } else {
            "No HTML content found in MHT file."
        }
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

                    // Placeholder schedule data (you'll replace this later)
                    OnboardingDataClass.scheduleData.clear()
                    OnboardingDataClass.scheduleData.addAll(
                        listOf(
                            ScheduleEntry("CS101", "09:00", "10:20", "Monday", "MC 105"),
                            ScheduleEntry("MATH135", "11:00", "12:20", "Tuesday", "RCH 101"),
                            ScheduleEntry("STAT230", "14:00", "15:20", "Wednesday", "DC 1351"),
                            ScheduleEntry("PHYS121", "10:30", "11:50", "Thursday", "PHY 150")
                        )
                    )
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
                } ?: Text(
                    "No file selected yet",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }
        }
    }
}
