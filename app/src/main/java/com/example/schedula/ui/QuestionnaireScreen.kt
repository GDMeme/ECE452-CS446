package com.example.schedula.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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

@Composable
fun QuestionnaireScreen(navController: NavController) {
    val items = listOf(
        "Gym", "Study Session", "Self-Care", "Other",
        "List item", "List item", "List item"
    )
    val checkedStates = remember { mutableStateMapOf<String, Boolean>() }
    items.forEach { checkedStates.putIfAbsent(it, true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Back to Login",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        navController.navigate("login") {
                            popUpTo("questionnaire") { inclusive = true }
                        }
                    }
            )
            Text(
                text = "Questionnaire",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Checkbox list
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(items) { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5D9FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", color = Color(0xFF5B3D9A), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = item,
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp
                    )

                    Checkbox(
                        checked = checkedStates[item] ?: false,
                        onCheckedChange = { checkedStates[item] = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF5B3D9A))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // File upload area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0E7FF), RoundedCornerShape(16.dp))
                .padding(24.dp)
                .padding(vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Menu, // Replace with cloud upload icon
                contentDescription = "Upload",
                tint = Color(0xFFB299EB)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Drag & drop files or ", color = Color.Black)
            Text(
                text = "Browse",
                color = Color(0xFF5B3D9A),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { /* Handle browse */ }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Supported formats: JPEG, PNG, GIF, MP4, PDF, PSD, AI, Word, PPT",
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom navigation bar
        NavigationBar {
            NavigationBarItem(
                selected = true,
                onClick = {},
                label = { Text("Questionnaire") },
                icon = {}
            )
            NavigationBarItem(
                selected = false,
                onClick = {
                    navController.navigate("calendar") {
                        popUpTo("questionnaire") { inclusive = false }
                    }
                },
                label = { Text("Calendar") },
                icon = {}
            )
            NavigationBarItem(
                selected = false,
                onClick = {
                    navController.navigate("timer") {
                        popUpTo("questionnaire") { inclusive = false }
                    }
                },
                label = { Text("Timer") },
                icon = {}
            )
        }
    }
}
