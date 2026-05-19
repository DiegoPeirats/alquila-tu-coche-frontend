package com.lokodom.alquilatucoche.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.ui.components.LoadingScreen
import com.lokodom.alquilatucoche.utils.UiState
import com.lokodom.alquilatucoche.viewmodels.RegistroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onBack: () -> Unit,
    vm: RegistroViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val form by vm.formState.collectAsState()
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    // Selector de imagen de perfil
    val imagenLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imagenUri = it
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            vm.updateForm { copy(imagenPerfil = bytes) }
        }
    }

    // Navegar a login tras registro exitoso
    LaunchedEffect(state) {
        if (state is UiState.Success) {
            onRegistroExitoso()
            vm.resetState()
        }
    }

    when (state) {
        is UiState.Loading -> LoadingScreen()
        else -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Crear cuenta", fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Filled.ArrowBack, "Volver")
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(Modifier.height(8.dp))

                    // Error global
                    if (state is UiState.Error) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                (state as UiState.Error).message,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Campos del formulario
                    RegistroTextField("Nombre *", form.nombre) {
                        vm.updateForm { copy(nombre = it) }
                    }
                    RegistroTextField("Apellidos *", form.apellidos) {
                        vm.updateForm { copy(apellidos = it) }
                    }

                    // Género — dropdown
                    GeneroDropdown(
                        valor = form.genero,
                        onValorChange = { vm.updateForm { copy(genero = it) } }
                    )

                    RegistroTextField("Dirección", form.direccion) {
                        vm.updateForm { copy(direccion = it) }
                    }
                    RegistroTextField("Provincia", form.provincia) {
                        vm.updateForm { copy(provincia = it) }
                    }
                    RegistroTextField(
                        label = "Email *",
                        value = form.email,
                        keyboardType = KeyboardType.Email
                    ) {
                        vm.updateForm { copy(email = it) }
                    }
                    RegistroTextField(
                        label = "Teléfono *",
                        value = form.numeroTelefono,
                        keyboardType = KeyboardType.Phone
                    ) {
                        vm.updateForm { copy(numeroTelefono = it) }
                    }

                    // Password
                    OutlinedTextField(
                        value = form.password,
                        onValueChange = { vm.updateForm { copy(password = it) } },
                        label = { Text("Contraseña *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(if (passwordVisible) "Ocultar" else "Ver")
                            }
                        }
                    )

                    OutlinedTextField(
                        value = form.confirmarPassword,
                        onValueChange = { vm.updateForm { copy(confirmarPassword = it) } },
                        label = { Text("Confirmar contraseña *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        isError = form.confirmarPassword.isNotBlank() && !form.passwordsCoinciden,
                        supportingText = {
                            if (form.confirmarPassword.isNotBlank() && !form.passwordsCoinciden) {
                                Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )

                    // Selector imagen de perfil (opcional)
                    OutlinedButton(
                        onClick = { imagenLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Image, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (imagenUri != null) "Imagen seleccionada ✓"
                            else "Seleccionar imagen de perfil (opcional)"
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = { vm.registrar() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = form.formularioValido,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Crear cuenta", style = MaterialTheme.typography.labelLarge)
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

// ─── Componentes auxiliares del formulario ────────────────────

@Composable
private fun RegistroTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GeneroDropdown(valor: String, onValorChange: (String) -> Unit) {
    val opciones = listOf("hombre", "mujer", "otro", "prefiero no decirlo")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = valor.ifBlank { "Seleccionar género" },
            onValueChange = {},
            readOnly = true,
            label = { Text("Género") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(10.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion.replaceFirstChar { it.uppercase() }) },
                    onClick = { onValorChange(opcion); expanded = false }
                )
            }
        }
    }
}
