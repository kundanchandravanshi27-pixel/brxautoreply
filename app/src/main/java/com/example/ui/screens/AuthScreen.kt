package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.BizRynoViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(viewModel: BizRynoViewModel, onAuthenticated: () -> Unit) {
    var isLoginTab by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val passwordStrength = remember(password) {
        when {
            password.length < 6 -> "Weak"
            password.length < 10 -> "Medium"
            else -> "Strong"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BizDarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BizSurfaceCard)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BizRyno Auto Reply",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = BizTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your AI sales assistant, replying while you sleep.",
                    fontSize = 14.sp,
                    color = BizTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TabButton("Login", isLoginTab) { isLoginTab = true; error = null }
                    Spacer(modifier = Modifier.width(12.dp))
                    TabButton("Sign Up", !isLoginTab) { isLoginTab = false; error = null }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (!isLoginTab) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = { Text("Mobile Number (+91...)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(if (isLoginTab) "Password" else "Create Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (!isLoginTab) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Strength: $passwordStrength",
                        color = when (passwordStrength) {
                            "Weak" -> BizError
                            "Medium" -> Color.Yellow
                            else -> BizSuccess
                        },
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Re-enter Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = error!!, color = BizError, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            error = "Please fill all fields"
                            return@Button
                        }
                        if (!isLoginTab && password != confirmPassword) {
                            error = "Passwords do not match"
                            return@Button
                        }

                        loading = true
                        error = null
                        scope.launch {
                            if (isLoginTab) {
                                viewModel.login(username, password) { success, err ->
                                    loading = false
                                    if (success) onAuthenticated() else error = err
                                }
                            } else {
                                viewModel.signUp(username, mobile, password) { success, err ->
                                    loading = false
                                    if (success) onAuthenticated() else error = err
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BizPrimary)
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = BizTextPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text(text = if (isLoginTab) "Login" else "Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) BizPrimary else BizSurfaceCardHover
    val textColor = if (selected) BizTextPrimary else BizTextSecondary
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = bg),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = text, color = textColor, fontWeight = FontWeight.Bold)
    }
}
