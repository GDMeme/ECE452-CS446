package com.example.schedula.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.schedula.AuthenticationRepo
import kotlinx.coroutines.launch
import com.example.schedula.R
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.alpha

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var pwVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val backgroundColor = Color(0xFFFAF7FC)
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // light blue background
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.schedula_logo),
            contentDescription = "BackgroundLogo",
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.Center)
                .alpha(0.1f),
            contentScale = ContentScale.Fit
        )

        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Schedula",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter University Credentials",
                fontSize = 20.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Uploads course times for efficient scheduling",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("School email") },
                placeholder = { Text("yourwatid@uwaterloo.ca") },
                trailingIcon = {
                    if (email.text.isNotEmpty()) {
                        IconButton(onClick = { email = TextFieldValue("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (pwVisible) Icons.Filled.VisibilityOff else Icons.Default.Visibility
                    val description = if (pwVisible) "Hide password" else "Show password"
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = Color.Black,
                    fontSize = 13.sp
                )
                Text(
                    text = "Create Account",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("signup")
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val trimmedEmail = email.text.trim()
                    val trimmedPassword = password.text.trim()

                    if (trimmedEmail.isNotBlank() && trimmedPassword.isNotBlank()) {
                        AuthenticationRepo.login(trimmedEmail, trimmedPassword) { success, error ->
                            if (success) {
                                coroutineScope.launch {
                                    val dataStoreManager = DataStoreManager(context)
                                    val hasSchedule = dataStoreManager.hasExistingSchedule()
                                    if (hasSchedule) {
                                        navController.navigate("calendar") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate("lifestyleQuestionnaire") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, error ?: "Login failed", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C89B8))
            ) {
                Text(text = "Continue", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "By clicking continue, you agree to our Terms of Service\nand Privacy Policy",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}
