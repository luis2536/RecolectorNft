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
    var scriptPath by remember { mutableStateOf("") }
    var processName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchProcessStatus()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Start Script", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Background Task Runners", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                IconButton(onClick = { viewModel.fetchProcessStatus() }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = MaterialTheme.colorScheme.primary)
                }
            }
            
            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            }

            if (processes.isEmpty()) {
                Text("No tasks running. (Make sure your PM2 backend is running!)", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                Column {
                                    Text(process.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("Status: ${process.status}", color = if (process.status == "online") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                    Text("Mem: ${process.memory / 1024 / 1024} MB | CPU: ${process.cpu}%", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row {
                                    IconButton(onClick = { viewModel.controlProcess("restart", process.name) }) {
                                        Icon(Icons.Filled.Refresh, contentDescription = "Restart", tint = MaterialTheme.colorScheme.secondary)
                                    }
                                    IconButton(onClick = { viewModel.controlProcess("stop", process.name) }) {
                                        Icon(Icons.Filled.Stop, contentDescription = "Stop", tint = MaterialTheme.colorScheme.error)
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
            title = { Text("Start New PM2 Process") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = processName, onValueChange = { processName = it }, label = { Text("Process Name") })
                    OutlinedTextField(value = scriptPath, onValueChange = { scriptPath = it }, label = { Text("Script Path (e.g. index.js)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.controlProcess("start", processName, scriptPath)
                    showDialog = false
                    processName = ""; scriptPath = ""
                }) {
                    Text("Start")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}
