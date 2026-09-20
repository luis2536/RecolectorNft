package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.NexusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeIdentityScreen(viewModel: NexusViewModel) {
    val identities by viewModel.nodeIdentities.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    var profileName by remember { mutableStateOf("") }
    var environment by remember { mutableStateOf("Base / PlayNixies") }
    var rpc by remember { mutableStateOf("https://mainnet.base.org") }
    var token by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar Cuenta", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Gestor Multicuentas de Nixies",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Administra tus cuentas, proxies SOCKS5/HTTP y tokens de sesión para recolección automatizada.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (identities.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No hay cuentas configuradas. Toca '+' para añadir una.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(identities) { identity ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(identity.profileName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("Red: ${identity.environment}", color = MaterialTheme.colorScheme.onSurface)
                                    Text("RPC / Proxy: ${identity.rpcEndpoint}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                    Text("Email: ${identity.testEmail}", color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
                                }
                                IconButton(onClick = { viewModel.deleteIdentity(identity) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nueva Cuenta Bot / Nodo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = profileName, onValueChange = { profileName = it }, label = { Text("Nombre de Cuenta (ej. Cuenta Principal 1)") })
                    OutlinedTextField(value = environment, onValueChange = { environment = it }, label = { Text("Ecosistema (ej. Base / PlayNixies)") })
                    OutlinedTextField(value = rpc, onValueChange = { rpc = it }, label = { Text("RPC Endpoint o Proxy SOCKS5") })
                    OutlinedTextField(value = token, onValueChange = { token = it }, label = { Text("Token de Sesión / Bearer") })
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo Electrónico (Asociado)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (profileName.isNotBlank()) {
                        viewModel.addIdentity(profileName, environment, rpc, token, email)
                        showDialog = false
                        profileName = ""; environment = "Base / PlayNixies"; rpc = "https://mainnet.base.org"; token = ""; email = ""
                    }
                }) {
                    Text("Guardar Cuenta")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
