package com.example.schedula.ui

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun CustomRoutineScreen(
    navController: NavController,
    onNext: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFFAF7FC)
    val routines = remember { List(4) { mutableStateOf("") } }
    val selected = remember { mutableStateMapOf<String, Boolean>() }

    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
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
                    .clickable {
                        navController.navigate("hobbiesQuestionnaire") {
                            popUpTo("customRoutineQuestionnaire") { inclusive = true }
                        }
                    }
            )
            Text(
                text = "Schedula",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        Text(
            "Specify Custom Routines",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        Text(
            "Is there anything else you would like to include in your schedule?",
            fontSize = 16.sp
        )
        Spacer(Modifier.height(12.dp))

        routines.forEach { routine ->
            OutlinedTextField(
                value = routine.value,
                onValueChange = { routine.value = it },
                placeholder = { Text("Enter your custom routine here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val nonEmptyRoutines = routines.map { it.value }.filter { it.isNotBlank() }

                coroutineScope.launch {
                    // Save to datastore
                    dataStoreManager.saveCustomRoutines(nonEmptyRoutines)

                    // Update onboarding class too
                    OnboardingDataClass.customRoutines.clear()
                    OnboardingDataClass.customRoutines.addAll(List(4) { nonEmptyRoutines.getOrNull(it) ?: "" })
                }

                OnboardingDataClass.updateHobbiesSelection(selected)
                onNext()
                navController.navigate("scheduleUpload")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C89B8))
        ) {
            Text("Next", fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomRoutineScreenPreview() {
    CustomRoutineScreen(navController = rememberNavController())
}
