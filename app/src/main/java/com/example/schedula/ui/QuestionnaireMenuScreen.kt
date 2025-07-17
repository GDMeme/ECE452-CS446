package com.example.schedula.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController

@Composable
fun QuestionnaireMenuScreen(navController: NavController) {
    val cardColor = Color(0xFFE6DEF6)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp))
        {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Choose a Questionnaire section to edit",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth().clickable { navController.navigate("lifestyleQuestionnaire")},
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Lifestyle 1",
                    modifier = Modifier.padding(16.dp),
                    fontSize =  18.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth().clickable { navController.navigate("extendedQuestionnaire") },
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Lifestyle 2",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth().clickable { navController.navigate("hobbiesQuestionnaire") },
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Hobbies",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth().clickable { navController.navigate("customRoutineQuestionnaire") },
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Custom Routines",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp
                )
            }

        }

}
