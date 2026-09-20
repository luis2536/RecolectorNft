package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.NexusViewModel

@Composable
fun ProcessControllerScreen(viewModel: NexusViewModel) {
    val processes by viewModel.processStatus.collectAsStateWithLifecycle()
    val error by viewModel.processError.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var scriptPath by remember { mutableStateOf("server.js") }
    var processName by remember { mutableStateOf("nexus-bot-worker") }

    LaunchedEffect(Unit) {
        viewModel.fetchProcessStatus()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Iniciar Tarea", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Column {
                    Text("Controlador Termux & PM2", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Gestión de bots y recolección 24/7 en segundo plano", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { viewModel.fetchProcessStatus() }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Actualizar", tint = MaterialTheme.colorScheme.primary)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            if (error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(error!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp), fontSize = 12.sp)
                }
            }

            if (processes.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No hay tareas PM2 activas. Asegúrate de ejecutar tu backend en Termux (http://127.0.0.1:3000).", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(processes) { process ->
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
                                    Text(process.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("Estado: ${process.status}", color = if (process.status == "online") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                    Text("Memoria: ${process.memory / 1024 / 1024} MB | CPU: ${process.cpu}%", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                }
                                Row {
                                    IconButton(onClick = { viewModel.controlProcess("restart", process.name) }) {
                                        Icon(Icons.Filled.Refresh, contentDescription = "Reiniciar", tint = MaterialTheme.colorScheme.secondary)
                                    }
                                    IconButton(onClick = { viewModel.controlProcess("stop", process.name) }) {
                                        Icon(Icons.Filled.Stop, contentDescription = "Detener", tint = MaterialTheme.colorScheme.error)
                                    }
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
            title = { Text("Iniciar Proceso en Termux") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = processName, onValueChange = { processName = it }, label = { Text("Nombre del Proceso") })
                    OutlinedTextField(value = scriptPath, onValueChange = { scriptPath = it }, label = { Text("Archivo Script (ej. server.js)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.controlProcess("start", processName, scriptPath)
                    showDialog = false
                }) {
                    Text("Iniciar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
