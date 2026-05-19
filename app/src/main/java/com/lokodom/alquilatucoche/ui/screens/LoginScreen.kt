package com.lokodom.alquilatucoche.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.session.SessionManager
import com.lokodom.alquilatucoche.ui.components.AppTextField
import com.lokodom.alquilatucoche.ui.theme.*
import com.lokodom.alquilatucoche.utils.UiState
import com.lokodom.alquilatucoche.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegistro: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            val data = (state as UiState.Success).data
            SessionManager.iniciarSesion(data.token, data.usuario?.id ?: 0L)
            onLoginSuccess(data.token)
        }
    }

    // Fondo con gradiente oscuro — garantiza que el fondo nunca sea blanco
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0D1A),
                        Background,
                        Color(0xFF0A0A12)
                    )
                )
            )
    ) {
        if (state is UiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Primary,
                strokeWidth = 3.dp
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(80.dp))

                // — Logo / Brand ———————————————————————————
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.DirectionsCar,
                            null,
                            tint = Primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    "AlquilaTuCoche",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground          // blanco sobre fondo oscuro
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Tu marketplace de alquiler",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant     // gris claro legible
                )

                Spacer(Modifier.height(48.dp))

                // — Formulario ——————————————————————————————
                AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    trailingIcon = {
                        Icon(Icons.Filled.Email, null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                )

                Spacer(Modifier.height(12.dp))

                AppTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                null,
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )

                // — Error ————————————————————————————————————
                if (state is UiState.Error) {
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        color = Color(0xFF4A0D0D),
                        shape = AppShapes.large
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.ErrorOutline, null, tint = AccentRed, modifier = Modifier.size(16.dp))
                            Text(
                                (state as UiState.Error).message,
                                color = AccentRed,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                // — Botón entrar ————————————————————————————
                Button(
                    onClick = { vm.login(email, password) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    enabled = email.isNotBlank() && password.isNotBlank(),
                    shape = AppShapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        disabledContainerColor = Border
                    )
                ) {
                    Icon(Icons.Filled.Login, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Iniciar sesión",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White       // siempre blanco sobre azul
                    )
                }

                Spacer(Modifier.height(16.dp))

                // — Divisor ——————————————————————————————————
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Border)
                    Text(
                        "  o  ",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceMuted
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Border)
                }

                Spacer(Modifier.height(16.dp))

                // — Botón registro — siempre visible ————————
                OutlinedButton(
                    onClick = onNavigateToRegistro,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = AppShapes.large,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Primary     // texto azul sobre fondo oscuro
                    )
                ) {
                    Icon(Icons.Filled.PersonAdd, null, modifier = Modifier.size(18.dp), tint = Primary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Crear cuenta nueva",
                        style = MaterialTheme.typography.labelLarge,
                        color = Primary            // explícito para garantizar contraste
                    )
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    "Al continuar aceptas nuestros términos de servicio",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}