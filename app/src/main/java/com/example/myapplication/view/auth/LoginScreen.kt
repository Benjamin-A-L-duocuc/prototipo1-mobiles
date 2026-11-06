package com.example.myapplication.view.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.validation.Validators
import com.example.myapplication.viewmodel.AppViewModel
import com.example.myapplication.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    onNavigate: (String) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var selectedRole by remember { mutableStateOf("tecnico") }
    var username by remember { mutableStateOf("cristian.rojas") }
    var password by remember { mutableStateOf("password123") }
    var usernameTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    val loginError by authViewModel.loginError.collectAsState()
    val usernameError = if (usernameTouched) Validators.loginUsername(username) else null
    val passwordError = if (passwordTouched) Validators.loginPassword(password) else null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF020617), Color(0xFF0F172A), Color(0xFF082F49))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0EA5E9).copy(alpha = 0.2f))
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Build, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "ACIS", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "Servicios Técnicos Ltda. · DSY1105", color = Color(0xFF94A3B8), fontSize = 12.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // Role selection tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val roles = listOf("tecnico" to "Técnico", "jefe" to "Jefe", "cliente" to "Cliente")
                roles.forEach { (role, label) ->
                    val sel = selectedRole == role
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (sel) Color(0xFF0EA5E9) else Color.Transparent)
                            .clickable {
                                selectedRole = role
                                username = when (role) {
                                    "tecnico" -> "cristian.rojas"
                                    "jefe" -> "gerardo.jefe"
                                    else -> "maria.cliente"
                                }
                                usernameTouched = false
                                passwordTouched = false
                                authViewModel.clearLoginError()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = label, color = if (sel) Color.White else Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Credentials Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "INICIAR SESIÓN", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        usernameTouched = true
                        authViewModel.clearLoginError()
                    },
                    label = { Text("Usuario", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = usernameError != null,
                    supportingText = {
                        if (usernameError != null) {
                            Text(text = usernameError, color = Color(0xFFFCA5A5), fontSize = 10.sp)
                        }
                    },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF64748B)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0EA5E9),
                        unfocusedBorderColor = Color(0xFF334155),
                        errorBorderColor = Color(0xFFEF4444),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordTouched = true
                        authViewModel.clearLoginError()
                    },
                    label = { Text("Contraseña", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = passwordError != null,
                    supportingText = {
                        if (passwordError != null) {
                            Text(text = passwordError, color = Color(0xFFFCA5A5), fontSize = 10.sp)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFF64748B)) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = Color(0xFF64748B)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0EA5E9),
                        unfocusedBorderColor = Color(0xFF334155),
                        errorBorderColor = Color(0xFFEF4444),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                )

                AnimatedVisibility(
                    visible = loginError != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = loginError.orEmpty(),
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        usernameTouched = true
                        passwordTouched = true
                        if (Validators.loginUsername(username) != null || Validators.loginPassword(password) != null) {
                            return@Button
                        }
                        val normalizedUsername = username.trim().lowercase()
                        val success = authViewModel.login(normalizedUsername, password, selectedRole)
                        if (success) {
                            onNavigate(viewModel.login(selectedRole, normalizedUsername))
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ingresar al sistema", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
