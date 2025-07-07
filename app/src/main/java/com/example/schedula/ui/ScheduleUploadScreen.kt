package com.example.schedula.ui

import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import java.io.BufferedReader
import java.io.InputStreamReader
import com.example.schedula.ui.OnboardingDataClass
import android.widget.Toast

@Composable
fun ScheduleUploadScreen(navController: NavController, onHtmlExtracted: (String) -> Unit) {
    val context = LocalContext.current
    var htmlContent by remember { mutableStateOf<String?>(null) }

    // --- Extracts all HTML parts from MHT content ---
    fun extractFullHtmlFromMht(mhtText: String): String {
        val htmlParts = mutableListOf<String>()

        // Find all HTML <html>...</html> sections (could be multiple if frames etc.)
        val regex = Regex("(?si)(<html.*?</html>)")
        regex.findAll(mhtText).forEach { match ->
            htmlParts.add(match.value)
        }

        return if (htmlParts.isNotEmpty()) {
            // Combine all parts into one HTML document
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
            .background(Color(0xFFEFF4F9)) // light blue background
            .padding(horizontal = 24.dp, vertical = 30.dp),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Upload Your Schedule") }
//            )
//        },
            bottomBar = {
                Button(
                    onClick = { navController.navigate("calendar") }, //TODO NEED TO NAVIGATE TO PAGE WITH THE CALENDAR WITH EVERYTHING IN IT
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
                        modifier = Modifier.fillMaxSize().weight(1f)
                    )
                    Toast.makeText(LocalContext.current, "File Upload Success", Toast.LENGTH_LONG).show()
                } ?: Text(
                    "No file selected yet"
                )
            }
        }
    }
}