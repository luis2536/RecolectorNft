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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.NexusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeIdentityScreen(viewModel: NexusViewModel) {
    val identities by viewModel.nodeIdentities.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    var profileName by remember { mutableStateOf("") }
    var environment by remember { mutableStateOf("") }
    var rpc by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Identity", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (identities.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No Node Identities configured.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(identities) { identity ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(identity.profileName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Env: ${identity.environment}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("RPC: ${identity.rpcEndpoint}", color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.deleteIdentity(identity) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
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
            title = { Text("New Node Identity") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = profileName, onValueChange = { profileName = it }, label = { Text("Profile Name") })
                    OutlinedTextField(value = environment, onValueChange = { environment = it }, label = { Text("Environment (e.g. Base)") })
                    OutlinedTextField(value = rpc, onValueChange = { rpc = it }, label = { Text("RPC Endpoint") })
                    OutlinedTextField(value = token, onValueChange = { token = it }, label = { Text("Bearer Token") })
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Test Email") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addIdentity(profileName, environment, rpc, token, email)
                    showDialog = false
                    profileName = ""; environment = ""; rpc = ""; token = ""; email = ""
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}
