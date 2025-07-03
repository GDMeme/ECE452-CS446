package com.example.schedula.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
//TODO complete home screen
fun HomeScreen(navController: NavController) {








    // Bottom navigation bar
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = {},
            label = { Text("Home") },
            icon = {}
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            label = { Text("Questions") },
            icon = {},
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            label = { Text("Calendar") },
            icon = {}
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            label = { Text("Timer") },
            icon = {}
        )
    }
}